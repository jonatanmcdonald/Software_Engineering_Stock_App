package com.example.loginsignup.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.asFlow
import com.example.loginsignup.data.db.StockAppDatabase
import com.example.loginsignup.data.db.StockAppRepository
import com.example.loginsignup.data.db.entity.Stock
import com.example.loginsignup.data.db.entity.WatchList
import com.example.loginsignup.data.db.view.WatchListWithSymbol
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class WatchListViewModel(application: Application) : AndroidViewModel(application) {

    // Toggle this if you want to seed the DB once
    private val first = false
    private val debounceDuration = 500L

    // --- Search state exposed to UI ---
    private val _stockList = MutableStateFlow<List<Stock>>(emptyList())
    val stockList: StateFlow<List<Stock>> = _stockList.asStateFlow()

    // The chosen stock from the search results (for add/edit actions)
    private val _selectedStock = MutableStateFlow<Stock?>(null)


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
        val priceDao = db.pricesTodayDao()

        repository = StockAppRepository(priceDao, userDao, watchListDao, stockDao)

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
                .collect { results -> _stockList.value = results }
        }

        if (first) {
            viewModelScope.launch { insertStocks() }
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
        repository.deleteWatchListItem(itemId)
    }

    fun getAllForUserWithSymbol(userId: String): LiveData<List<WatchListWithSymbol>> =
        repository.getAllForUserWithSymbol(userId)

    fun getWatchListItemWithSymbol(userId: String, stockId: Long): LiveData<WatchListWithSymbol?> =
        repository.getWatchListItemWithSymbol(userId, stockId)

    suspend fun getStockSymbol(stockId: Long): String =
        repository.getStockSymbol(stockId)

    // --- Seed list ---
    private suspend fun insertStocks() {
        val allStocks = listOf(
            Stock(symbol = "AAPL", name = "Apple Inc."),
            Stock(symbol = "MSFT", name = "Microsoft Corporation"),
            Stock(symbol = "AMZN", name = "Amazon.com, Inc."),
            Stock(symbol = "TSLA", name = "Tesla, Inc."),
            Stock(symbol = "GOOGL", name = "Alphabet Inc. (Google)"),
            Stock(symbol = "NVDA", name = "NVIDIA Corporation"),
            Stock(symbol = "META", name = "Meta Platforms, Inc."),
            Stock(symbol = "IBM", name = "International Business Machines"),
            Stock(symbol = "NFLX", name = "Netflix, Inc."),
            Stock(symbol = "INTC", name = "Intel Corporation"),
            Stock(symbol = "AMD", name = "Advanced Micro Devices, Inc."),
            Stock(symbol = "ORCL", name = "Oracle Corporation"),
            Stock(symbol = "ADBE", name = "Adobe Inc."),
            Stock(symbol = "PYPL", name = "PayPal Holdings, Inc."),
            Stock(symbol = "DIS", name = "The Walt Disney Company")
        )
        repository.upsertAll(allStocks)
    }

    fun onStockSelected(stock: Stock) {
        _selectedStock.value = stock
        // Prefill the text field for UX (optional)
        _searchQuery.value = stock.symbol
        _stockList.value = emptyList()
    }
}
