package com.example.loginsignup.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.loginsignup.App
import com.example.loginsignup.data.db.StockAppDatabase
import com.example.loginsignup.data.db.StockAppRepository
import com.example.loginsignup.data.db.entity.Stock
import com.example.loginsignup.data.db.entity.Transaction
import com.example.loginsignup.screens.DetailsUi
import com.example.loginsignup.screens.WatchUi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private const val CALLS_PER_MINUTE = 60              // your real limit
private const val GAP_MS = 60_000L / CALLS_PER_MINUTE

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
// This class is the ViewModel for the Search screen.
class SearchViewModel(application: Application): AndroidViewModel(application) {
        private var priceJob: Job? = null // The job for fetching prices.
        private val limiter = (application as App).rateLimiter // The rate limiter for API calls.
        private val debounceDuration = 500L // The duration for debouncing search queries.

        // --- Search state exposed to UI ---
        private val _stockList = MutableStateFlow<List<Stock>>(emptyList()) // A private mutable state flow to hold the list of stocks.

        val stockList: StateFlow<List<Stock>> = _stockList.asStateFlow() // A public state flow to expose the list of stocks.

        private val _viewPage = MutableStateFlow<DetailsUi>(DetailsUi()) // A private mutable state flow to hold the details of a stock.
        val viewPage: StateFlow<DetailsUi> = _viewPage.asStateFlow() // A public state flow to expose the details of a stock.

        private val _ticker = MutableStateFlow("") // A private mutable state flow to hold the ticker of a stock.
        val ticker: StateFlow<String> = _ticker // A public state flow to expose the ticker of a stock.
        private val _searchQuery = MutableStateFlow("") // A private mutable state flow to hold the search query.
        val searchQuery: StateFlow<String> = _searchQuery.asStateFlow() // A public state flow to expose the search query.

        private val _isLoading = MutableStateFlow(false) // A private mutable state flow to track the loading state.
        val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow() // A public state flow to expose the loading state.

        private val _price = MutableStateFlow<WatchUi>(WatchUi()) // A private mutable state flow to hold the price of a stock.
        val price: StateFlow<WatchUi> = _price.asStateFlow() // A public state flow to expose the price of a stock.

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
                searchQuery
                    .debounce(debounceDuration) // Debounces the search query.
                    .map {it.trim()} // Trims the search query.
                    .distinctUntilChanged() // Only proceeds if the search query has changed.
                    .flatMapLatest { q -> // Flattens the flow to get the latest search results.
                        if (q.isBlank() || q.length < 2) { // If the search query is blank or too short.
                            flowOf(emptyList()) // Returns an empty list.
                        } else { // Otherwise.
                            // If repository.searchStocks returns LiveData:
                            repository.searchStocks(q).asFlow() // Converts the LiveData to a flow.
                            // If Flow: emitAll(repository.searchStocks(q))
                        }
                    }
                    .catch { _stockList.value = emptyList() } // Catches any exceptions and sets the stock list to empty.
                    .onEach { results -> _stockList.value = results } // Sets the stock list to the search results.
                    .launchIn(viewModelScope) // Launches the flow in the ViewModel scope.
            }

        }


        /** Call this once when the screen opens (pass the signed-in user's id). */
        // This function starts the live update of the details price.
        fun startDetailsPriceUpdate(ticker:String) {
            priceJob?.cancel() // Cancels the previous price job.
            priceJob = viewModelScope.launch { // Launches a new coroutine in the ViewModel scope.
                _ticker.value = ticker // Sets the ticker value.
                updateScreen(ticker) // one API call

                //Log.d("SearchVM", "startDetailsPriceUpdate: $page)")

                while (isActive){ // Loops while the coroutine is active.
                    try {
                        val resp = limiter.run{repository.fetchPrice(_ticker.value)} // Fetches the price.

                        _price.value =
                            WatchUi( // Updates the price flow.
                                ticker = _ticker.value,
                                price = resp.price,
                                change = resp.change,
                                changePercent = resp.percentChange,
                                isUp = resp.change?.let { it > 0.0 },
                            )// update price flow
                    }
                    catch (t: Throwable) {
                            // swallow; we'll try again next cycle
                            Log.w("SearchVM", "fetch failed for ${ticker}: ${t.message}")
                        }

                    delay(GAP_MS) // Delays for the specified gap time.
                    }

                }

            }
            // This function updates the screen with the details of a stock.
            private suspend fun updateScreen(ticker: String){
                try {
                    val resp = limiter.run{repository.getProfile(ticker)} // Fetches the profile.


                    val new = DetailsUi( // Creates a new DetailsUi object.
                      country = resp.country,
                      currency = resp.currency,
                      exchange = resp.exchange,
                        ipo = resp.ipo,
                        name = resp.name,
                        phone = resp.phone,
                        shareOutstanding = resp.shareOutstanding,
                        ticker = resp.ticker,
                        weburl = resp.weburl,
                        logo = resp.logo,
                        finnhubIndustry = resp.finnhubIndustry,
                        marketCapitalization = resp.marketCapitalization
                    )

                    _viewPage.value = new // Sets the view page value.
                    Log.d("SearchVM", "updateScreen OK: $new")
                } catch (t: Throwable) {
                    // Fall back to whatever we had
                    Log.e("SearchVM", "updateScreen FAILED for $ticker: ${t.message}", t)
                }
            }

            // This function is called when a stock is selected.
            fun onStockSelected(stock: Stock)
            {
                _ticker.value = stock.ticker
            }

            // This function is called when the search query is changed.
            fun onSearchQueryChanged(query: String) {
                _searchQuery.value = query
            }

            // This function saves a transaction.
            fun saveTransaction(transaction: Transaction)
            {
                viewModelScope.launch {
                    repository.addTransaction(transaction)
                }
            }
}


