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
class SearchViewModel(application: Application): AndroidViewModel(application) {
        private var priceJob: Job? = null
        private val limiter = (application as App).rateLimiter
        private val debounceDuration = 500L

        // --- Search state exposed to UI ---
        private val _stockList = MutableStateFlow<List<Stock>>(emptyList())

        val stockList: StateFlow<List<Stock>> = _stockList.asStateFlow()

        private val _viewPage = MutableStateFlow<DetailsUi>(DetailsUi())
        val viewPage: StateFlow<DetailsUi> = _viewPage.asStateFlow()

        private val _ticker = MutableStateFlow("")
        val ticker: StateFlow<String> = _ticker
        private val _searchQuery = MutableStateFlow("")
        val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

        private val _isLoading = MutableStateFlow(false)
        val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

        private val _price = MutableStateFlow<WatchUi>(WatchUi())
        val price: StateFlow<WatchUi> = _price.asStateFlow()

        private val repository: StockAppRepository

        init {
            val db = StockAppDatabase.getDatabase(application)
            val userDao = db.userDao()
            val watchListDao = db.watchListDao()
            val stockDao = db.stockDao()
            val transactionDao = db.transactionDao()
            val portfolioDao = db.portfolioDao()
            val alertDao = db.alertDao()
            val noteDao = db.noteDao()



            repository = StockAppRepository(userDao, watchListDao, stockDao, transactionDao, portfolioDao, alertDao, noteDao)

            // Search pipeline (single source of truth = `searchQuery`)
            viewModelScope.launch {
                searchQuery
                    .debounce(debounceDuration)
                    .map {it.trim()}
                    .distinctUntilChanged() // only care if text changed
                    .flatMapLatest { q ->
                        if (q.isBlank() || q.length < 2) {
                            flowOf(emptyList())
                        } else {
                            // If repository.searchStocks returns LiveData:
                            repository.searchStocks(q).asFlow()
                            // If Flow: emitAll(repository.searchStocks(q))
                        }
                    }
                    .catch { _stockList.value = emptyList() }
                    .onEach { results -> _stockList.value = results }
                    .launchIn(viewModelScope)
            }

        }


        /** Call this once when the screen opens (pass the signed-in user's id). */
        fun startDetailsPriceUpdate(ticker:String) {
            priceJob?.cancel()
            priceJob = viewModelScope.launch {
                _ticker.value = ticker
                updateScreen(ticker) // one API call

                //Log.d("SearchVM", "startDetailsPriceUpdate: $page)")

                while (isActive){
                    try {
                        val resp = limiter.run{repository.fetchPrice(_ticker.value)}

                        _price.value =
                            WatchUi(
                                ticker = _ticker.value,
                                price = resp.price,
                                change = resp.change,
                                changePercent = resp.percentChange,
                                isUp = resp.change?.let { it > 0.0 }
                            )// update price flow
                    }
                    catch (t: Throwable) {
                            // swallow; we'll try again next cycle
                            Log.w("SearchVM", "fetch failed for ${ticker}: ${t.message}")
                        }

                    delay(GAP_MS)
                    }

                }

            }
            private suspend fun updateScreen(ticker: String){
                try {
                    val resp = limiter.run{repository.getProfile(ticker)}


                    val new = DetailsUi(
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

                    _viewPage.value = new
                    Log.d("SearchVM", "updateScreen OK: $new")
                } catch (t: Throwable) {
                    // Fall back to whatever we had
                    Log.e("SearchVM", "updateScreen FAILED for $ticker: ${t.message}", t)
                }
            }

            fun onStockSelected(stock: Stock)
            {
                _ticker.value = stock.ticker
            }

            fun onSearchQueryChanged(query: String) {
                _searchQuery.value = query
            }

            fun saveTransaction(transaction: Transaction)
            {
                viewModelScope.launch {
                    repository.addTransaction(transaction)
                }
            }
}


