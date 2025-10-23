package com.example.loginsignup.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.asFlow
import com.example.loginsignup.data.db.StockAppDatabase
import com.example.loginsignup.data.db.StockAppRepository
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

private const val CALLS_PER_MINUTE = 60              // your real limit
private const val GAP_MS = 60_000L / CALLS_PER_MINUTE
@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class WatchListViewModel(application: Application) : AndroidViewModel(application) {

    // Toggle this if you want to seed the DB once
    private val first = false
    private var priceJob: Job? = null
    private val newDay = false
    private val debounceDuration = 500L

    // --- Search state exposed to UI ---
    private val _stockList = MutableStateFlow<List<Stock>>(emptyList())
    val stockList: StateFlow<List<Stock>> = _stockList.asStateFlow()

    // The chosen stock from the search results (for add/edit actions)
    private val _selectedStock = MutableStateFlow<Stock?>(null)

    private val _watchRows = MutableStateFlow<List<WatchUi>>(emptyList())
    val watchRows: StateFlow<List<WatchUi>> = _watchRows.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()


    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val repository: StockAppRepository

    init {
        val db = StockAppDatabase.getDatabase(application)
        val userDao = db.userDao()
        val watchListDao = db.watchListDao()
        val stockDao = db.stockDao()

        repository = StockAppRepository(userDao, watchListDao, stockDao)

        // Search pipeline (single source of truth = `searchQuery`)
        viewModelScope.launch {

            searchQuery
                .debounce(debounceDuration)
                .map { it.trim() }
                .distinctUntilChanged()
                .flatMapLatest { q ->
                    if (q.isBlank() || q.length < 2) {
                        flowOf(emptyList())
                    } else {
                        // If repository.searchStocks returns LiveData<List<Stock>>:
                        repository.searchStocks(q).asFlow()
                        // If it returns Flow<List<Stock>> instead, use:
                        // repository.searchStocks(q)
                    }
                }
                .catch { _stockList.value = emptyList() }
                .onEach { results -> _stockList.value = results }
                .launchIn(viewModelScope) // launch the collection once
        }

        //if (first) {
        //    viewModelScope.launch { insertStocks() }
        //}

    }


    private fun reconcileWithWatchlist(watchlist: List<WatchListWithSymbol>) {
        _watchRows.update { current ->
            val existingById = current.associateBy { it.id }
            watchlist.map { w ->
                val prev = existingById[w.id]
                prev?.// existing item → keep live fields, refresh static labels
                copy(
                    name = w.name,
                    ticker = w.ticker,
                    note = w.note
                    // price/change/isUp preserved
                )
                    ?: // new item → seed placeholder (keeps UI smooth; prices come in live)
                    WatchUi(
                        id = w.id,
                        name = w.name,
                        ticker = w.ticker,
                        note = w.note,
                        price = null,
                        change = null,
                        changePercent = null,
                        isUp = null
                    )
            }
        }
    }

    /** Call this once when the screen opens (pass the signed-in user's id). */
    fun startWatchlistPriceUpdate(userId: String) {
        priceJob?.cancel()
        priceJob = viewModelScope.launch {
            // 1) Keep a live, mutable snapshot of the current watchlist for rotation
            var rotation: List<WatchListWithSymbol> = emptyList()

            // 2) Listen to watchlist changes and reconcile UI labels immediately
            val watchlistCollector = launch {
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
            while (isActive) {
                val snapshot = rotation // read current list
                if (snapshot.isNotEmpty()) {
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
                delay(GAP_MS)
            }
            // cancel child collector if loop exits
            watchlistCollector.cancel()
        }
    }

    /** Upsert a single row atomically, preserving watchlist order. */
    private fun mergeRow(row: WatchUi) {
        _watchRows.update { current ->
            val byId = current.associateBy { it.id }.toMutableMap()
            byId[row.id] = row
            current.map { byId[it.id]!! }
        }
    }


    private suspend fun updateOneRow(w: WatchListWithSymbol): WatchUi {
        val existing = _watchRows.value.find { it.id == w.id }
        return try {

           val resp = repository.fetchPrice(w.ticker)
            val latestPx: Double? = resp.price
            val change: Double? = resp.change
            val changePc: Double? = resp.percentChange
           // Log.d("WatchListViewModel", "updateOneRow: $resp")
            //Log.d("WatchListViewModel", "updateOneRow: $latestPx")

            WatchUi(
                id = w.id,
                name = w.name,
                ticker = w.ticker,
                note = w.note,
                price = latestPx,
                change = change,
                changePercent = changePc,
                isUp = change?.let { it > 0.0 }
            )
        } catch (t: Throwable) {
            // Fall back to whatever we had
            existing ?: WatchUi(
                id = w.id,
                name = w.name,
                ticker = w.ticker,
                note = w.note,
                price = null,
                change = null,
                changePercent = null,
                isUp = null
            )
        }
    }
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        _selectedStock.value = null
    }

    // --- Watchlist operations ---
    enum class UpsertResult { Inserted, Updated, AlreadyExists }

    suspend fun upsertByUserAndStock(item: WatchList): UpsertResult {
        val updated = repository.updateWatchListItem(item)
        if (updated > 0) return UpsertResult.Updated
        val rowId = repository.addWatchListItem(item)
        return if (rowId != -1L) UpsertResult.Inserted else UpsertResult.AlreadyExists
    }

    suspend fun getStockId(symbol: String): Long =
        repository.getStockId(symbol)

    suspend fun existsForUser(userId: String, stockId: Long): Boolean =
        repository.existsForUser(userId, stockId)

    fun getAllForUser(id: String): LiveData<List<WatchList>> =
        repository.getAllForUser(id)

    fun getWatchListItem(id: String, itemId: Long): LiveData<WatchList?> =
        repository.getWatchListItem(id, itemId)

    fun delete(itemId: Long) = viewModelScope.launch {
        // instantly remove from UI
        _watchRows.update { it.filterNot { row -> row.id == itemId } }
        repository.deleteWatchListItem(itemId)
    }

    fun getAllForUserWithSymbol(userId: String): LiveData<List<WatchListWithSymbol>> =
        repository.getAllForUserWithSymbol(userId)

    fun getWatchListItemWithSymbol(userId: String, stockId: Long): LiveData<WatchListWithSymbol?> =
        repository.getWatchListItemWithSymbol(userId, stockId)

    suspend fun getStockSymbol(stockId: Long): String =
        repository.getStockSymbol(stockId)

    // --- Seed list ---
    /*
    private suspend fun insertStocks() {
        try {
            val resp = repository.fetchAllUsStocks()
            val rows = resp.map{
                Stock(
                    ticker = it.ticker,
                    name = it.name,
                    market = it.market,
                    locale = it.locale,
                    type = it.type,
                    currencyName = it.currencyName,
                    primaryExchange = it.primaryExchange
                )

            }

            if (rows.isNotEmpty()) {
                repository.upsertAll(rows)
            }
        }
        catch (t: Throwable) {
            Log.d("Insert Failed", "insertStocks: ${t.message}")
        }

    }
    */


    fun onStockSelected(stock: Stock) {
        _selectedStock.value = stock
        // Prefill the text field for UX (optional)
        _searchQuery.value = stock.ticker
        _stockList.value = emptyList()
    }
}
