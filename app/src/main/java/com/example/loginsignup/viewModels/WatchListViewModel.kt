package com.example.loginsignup.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.asFlow
import com.example.loginsignup.App
import com.example.loginsignup.data.db.StockAppDatabase
import com.example.loginsignup.data.db.StockAppRepository
import com.example.loginsignup.data.db.entity.Alert
import com.example.loginsignup.data.db.entity.Note
import com.example.loginsignup.data.db.entity.Stock
import com.example.loginsignup.data.db.entity.WatchList
import com.example.loginsignup.data.db.view.WatchListWithSymbol
import com.example.loginsignup.screens.WatchUi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger
import com.example.loginsignup.PriceNotificationService
import com.example.loginsignup.screens.UiMedia

private const val CALLS_PER_MINUTE = 60              // your real limit
private const val GAP_MS = 60_000L / CALLS_PER_MINUTE
@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
// This class is the ViewModel for the Watchlist screen.
class WatchListViewModel(application: Application) : AndroidViewModel(application) {
    private val limiter = (application as App).rateLimiter // The rate limiter for API calls.
    private var priceJob: Job? = null // The job for fetching prices.
    private val debounceDuration = 500L // The duration for debouncing search queries.
    private val notificationService = PriceNotificationService(application) // The service for sending notifications.

    private val ignoreNextSearch = MutableStateFlow(false) // A flag to ignore the next search query.

    // --- Search state exposed to UI ---
    private val _stockList = MutableStateFlow<List<Stock>>(emptyList()) // A private mutable state flow to hold the list of stocks.
    val stockList: StateFlow<List<Stock>> = _stockList.asStateFlow() // A public state flow to expose the list of stocks.

    // The chosen stock from the search results (for add/edit actions)
    private val _selectedStock = MutableStateFlow<Stock?>(null)


    private val _watchRows = MutableStateFlow<List<WatchUi>>(emptyList()) // A private mutable state flow to hold the list of watchlist rows.
    val watchRows: StateFlow<List<WatchUi>> = _watchRows.asStateFlow() // A public state flow to expose the list of watchlist rows.

    private val _searchQuery = MutableStateFlow("") // A private mutable state flow to hold the search query.
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow() // A public state flow to expose the search query.


    private val _isLoading = MutableStateFlow(false) // A private mutable state flow to track the loading state.
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow() // A public state flow to expose the loading state.

    private val repository: StockAppRepository // The repository for accessing data.

    init { // The initializer block for the ViewModel.
        val db = StockAppDatabase.getDatabase(application) // Gets the database instance.
        val userDao = db.userDao() // Gets the user DAO.
        val watchListDao = db.watchListDao() // Gets the watchlist DAO.
        val stockDao = db.stockDao() // Gets the stock DAO.
        val transactionDao = db.transactionDao() // Gets the transaction DAO.
        val portfolioDao = db.portfolioDao() // Gets the portfolio DAO.
        val alertDao = db.alertDao() // Gets the alert DAO.
        val noteDao = db.noteDao() // Gets the note DAO.


        repository = StockAppRepository(userDao, watchListDao, stockDao, transactionDao, portfolioDao, alertDao, noteDao) // Creates the repository.

        // Search pipeline (single source of truth = `searchQuery`)
        viewModelScope.launch { // Launches a new coroutine in the ViewModel scope.
            combine(searchQuery, ignoreNextSearch) { q, ignore -> q to ignore } // Combines the search query and the ignore flag.
                .debounce(debounceDuration) // Debounces the search query.
                .map { (q, ignore) -> (q.trim() to ignore) } // Trims the search query.
                .distinctUntilChanged { old, new -> old.first == new.first } // Only proceeds if the search query has changed.
                .transformLatest { (q, ignore) -> // Transforms the flow to get the latest search results.
                    if (ignore) { // If the ignore flag is set.
                        // consume exactly this emission (the programmatic set), then clear the flag
                        ignoreNextSearch.value = false
                        return@transformLatest
                    }
                    if (q.isBlank() || q.length < 2) { // If the search query is blank or too short.
                        emit(emptyList<Stock>())
                    } else {
                        // If repository.searchStocks returns LiveData:
                        emitAll(repository.searchStocks(q).asFlow()) // Converts the LiveData to a flow.
                        // If Flow: emitAll(repository.searchStocks(q))
                    }
                }
                .catch { _stockList.value = emptyList() } // Catches any exceptions and sets the stock list to empty.
                .onEach { results -> _stockList.value = results } // Sets the stock list to the search results.
                .launchIn(this) // Launches the flow in the ViewModel scope.
        }

        //if (first) {
        //    viewModelScope.launch { insertStocks() }
        //}

    }


    // This function reconciles the current watchlist with the one from the database.
    private fun reconcileWithWatchlist(watchlist: List<WatchListWithSymbol>) {
        _watchRows.update { current ->
            val existingById = current.associateBy { it.id }
            watchlist.map { w ->
                val prev = existingById[w.id]

                val mediaList: List<String> =
                    w.mediaUris?.split("|") ?.filter{it.isNotBlank()} ?: emptyList()

                prev?.copy(
                    // existing item → keep live fields, refresh static labels
                    name = w.name ?: "",
                    ticker = w.ticker ?: "",
                    noteId = w.noteId,
                    content = w.content,
                    media = mediaList

                    // price/change/isUp preserved
                )
                    ?: // new item → seed placeholder (keeps UI smooth; prices come in live)
                    WatchUi(
                        id = w.id,
                        name = w.name ?: "",
                        noteId = w.noteId,
                        content = w.content,
                        ticker = w.ticker ?: "",
                        media = mediaList,
                        isUp = null,
                    )
            }
        }
    }

    /** Call this once when the screen opens (pass the signed-in user's id). */
    // This function starts the live update of the watchlist prices.
    fun startWatchlistPriceUpdate(userId: Int) {
        priceJob?.cancel() // Cancels the previous price job.
        priceJob = viewModelScope.launch { // Launches a new coroutine in the ViewModel scope.
            // 1) Keep a live, mutable snapshot of the current watchlist for rotation
            var rotation: List<WatchListWithSymbol> = emptyList()

            // 2) Listen to watchlist changes and reconcile UI labels immediately
            val watchlistCollector = launch { // Launches a new coroutine to collect watchlist changes.
                repository.observeAllForUsers(userId)
                    .distinctUntilChanged() // avoid noisy re-emits
                    .collectLatest { list ->
                        rotation = list
                        reconcileWithWatchlist(list) // update names/tickers/notes instantly
                    }
            }

            // 3) Continuous drip loop: one API call every GAP_MS, cycling through rotation
            val existingIndex = AtomicInteger(0)
            val index = existingIndex // (or keep a local var if you prefer)
            while (isActive) { // Loops while the coroutine is active.
                val snapshot = rotation // read current list
                if (snapshot.isNotEmpty()) { // If the snapshot is not empty.
                    val i = index.getAndIncrement()
                    val target = snapshot[i % snapshot.size]

                    try {
                        val row = updateOneRow(target) // one API call
                        mergeRow(row)                  // atomic UI update
                    } catch (t: Throwable) {
                        // swallow; we'll try again next cycle
                        Log.w("WatchListVM", "fetch failed for ${target.ticker}: ${t.message}")
                    }
                }
                delay(GAP_MS) // Delays for the specified gap time.
            }
            // cancel child collector if loop exits
            watchlistCollector.cancel() // Cancels the watchlist collector.
        }
    }

    /** Upsert a single row atomically, preserving watchlist order. */
    // This function merges a single row into the watchlist rows.
    private fun mergeRow(row: WatchUi) {
        _watchRows.update { current ->
            val byId = current.associateBy { it.id }.toMutableMap()
            byId[row.id] = row
            current.map { byId[it.id]!! }
        }
    }


    // This function updates a single row in the watchlist.
    private suspend fun updateOneRow(w: WatchListWithSymbol): WatchUi {
        val existing = _watchRows.value.find { it.id == w.id }
        return try {

           val resp = limiter.run{repository.fetchPrice(w.ticker ?: "")} // Fetches the price.
            val latestPx: Double = resp.price
            val change: Double? = resp.change
            val changePc: Double? = resp.percentChange
           // Log.d("WatchListViewModel", "updateOneRow: $resp")
            //Log.d("WatchListViewModel", "updateOneRow: $latestPx")
            val alert = repository.getAlerts(w.userId, w.ticker ?: "", "Watchlist")
            //Log.d("WatchListViewModel", "updateOneRow: $w")
            //Log.d("WatchListViewModel", "updateOneRow: $alert")

            if(alert != null && alert.isActive){
                checkAlertCondition(alert, latestPx)
            }


            WatchUi( // Creates a new WatchUi object.
                id = w.id,
                name = w.name ?: "",
                ticker = w.ticker ?: "",
                noteId = w.noteId,
                content = w.content,
                price = latestPx,
                hasAlert = alert != null,
                alertParameter = alert?.runCondition ?: "",
                alertPrice = alert?.triggerPrice ?: 0.0,
                change = change,
                alertActive = alert?.isActive ?: true,
                changePercent = changePc,
                isUp = change?.let { it > 0.0 },
                media = w.mediaUris?.split("|") ?.filter{it.isNotBlank()} ?: emptyList()
            )
        } catch (t: Throwable) {
            // Fall back to whatever we had
            existing ?: WatchUi(
                id = w.id,
                name = w.name ?: "",
                ticker = w.ticker ?: "",
                noteId = w.noteId,
                content = w.content,
                price = 0.0,
                hasAlert = false,
                alertParameter = "",
                alertPrice = 0.0,
                alertActive = true,
                change = null,
                changePercent = null,
                isUp = null,
                media = emptyList()
            )
        }
    }

    // This function checks if an alert condition has been met.
    private fun checkAlertCondition(
        alert: Alert,
        latestPx: Double
    ): Boolean {
        val isTriggered = when (alert.runCondition) {
            "GREATER_THAN" -> latestPx > alert.triggerPrice
            "LESS_THAN" -> latestPx < alert.triggerPrice
            "EQUAL_TO" -> kotlin.math.abs(latestPx - alert.triggerPrice) < 0.0001
            else -> false
        }

        Log.d("WatchListViewModel", "checkAlertCondition: $isTriggered")
        if (isTriggered) {
            val alertText = when (alert.runCondition) {
                "GREATER_THAN" -> "has gone above watch price"
                "LESS_THAN" -> "has gone below watch price"
                "EQUAL_TO" -> "has reached watch price"
                else -> ""
            }
            notificationService.sendPriceNotification(latestPx, alert.triggerParent, alert.symbol, alertText)
            viewModelScope.launch {
                repository.toggleAlertActive(alert.triggerParent, alert.userId, alert.symbol, false)
            }
        }

        return isTriggered
    }

    // This function is called when the search query is changed.
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        _selectedStock.value = null
    }

    // --- Watchlist operations ---
    enum class UpsertResult { Inserted, Updated, AlreadyExists }

    // This function upserts a watchlist item.
    suspend fun upsertByUserAndStock(item: WatchList): UpsertResult {
        val updated = repository.updateWatchListItem(item)
        if (updated > 0) return UpsertResult.Updated
        val rowId = repository.addWatchListItem(item)
        return if (rowId != -1L) UpsertResult.Inserted else UpsertResult.AlreadyExists
    }

    // This function retrieves the ID of a stock by its symbol.
    suspend fun getStockId(symbol: String): Long =
        repository.getStockId(symbol)

    // This function deletes a watchlist item.
    fun delete(itemId: Long) = viewModelScope.launch {
        // instantly remove from UI
        _watchRows.update { it.filterNot { row -> row.id == itemId } }
        repository.deleteWatchListItem(itemId)
    }

    // This function is called when a stock is selected.
    fun onStockSelected(stock: Stock) {
        _selectedStock.value = stock
        ignoreNextSearch.value = true
        // Prefill the text field for UX (optional)
        _searchQuery.value = stock.ticker
        _stockList.value = emptyList()
    }

    // This function saves a note with its associated media.
    fun saveNoteWithMedia(
        existingNoteId: Long?,
        watchlistId: Long,
        userId: Int,
        content: String,
        media: List<UiMedia>
    ) {
        viewModelScope.launch {
            repository.saveNoteWithMedia(
                existingNoteId = existingNoteId,
                watchlistId = watchlistId,
                userId = userId,
                content = content,
                media = media
            )
        }
    }

    // This function upserts an alert.
    suspend fun upsertAlert(alert: Alert): UpsertResult{
        val updated = repository.updateAlert(alert)
        if (updated > 0) return UpsertResult.Updated
        val rowId = repository.insertAlert(alert)
        return if (rowId != -1L) UpsertResult.Inserted else UpsertResult.AlreadyExists
    }

    // This function toggles the active state of an alert.
    suspend fun toggleAlertActive(parent: String, userId: Int, symbol: String, isActive: Boolean)
    {
        repository.toggleAlertActive(parent, userId,  symbol,isActive)
    }



}
