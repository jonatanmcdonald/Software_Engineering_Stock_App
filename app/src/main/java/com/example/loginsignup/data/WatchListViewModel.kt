import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginsignup.screens.Stock
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WatchListViewModel : ViewModel() {

    private val _stockList = MutableStateFlow<List<Stock>>(emptyList())
    val stockList = _stockList.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private var searchJob: Job? = null
    private val debounceDuration = 500L

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(debounceDuration)
            if (query.isNotBlank()) {
                performSearch(query)
            } else {
                _stockList.value = emptyList()
            }
        }
    }

    fun onStockSelected(stock: Stock) {
        _searchQuery.value = "${stock.name} (${stock.symbol})"
        _stockList.value = emptyList()
    }
    private fun performSearch(query: String) {
        viewModelScope.launch {
            _isLoading.value = true

            // --- MOCK API IMPLEMENTATION ---

            try {
                // 1. Simulate network delay
                println("Simulating API search for: $query")
                delay(800) // 800ms delay to simulate a network call

                // 2. Create a list of all possible stocks to search from
                val allStocks = listOf(
                    Stock("Apple Inc.", "AAPL"),
                    Stock("Microsoft Corporation", "MSFT"),
                    Stock("Amazon.com, Inc.", "AMZN"),
                    Stock("Tesla, Inc.", "TSLA"),
                    Stock("Alphabet Inc. (Google)", "GOOGL"),
                    Stock("NVIDIA Corporation", "NVDA"),
                    Stock("Meta Platforms, Inc.", "META"),
                    Stock("International Business Machines", "IBM")
                )

                // 3. Filter the list based on the search query (case-insensitive)
                _stockList.value = allStocks.filter { stock ->
                    stock.name.contains(query, ignoreCase = true) ||
                            stock.symbol.contains(query, ignoreCase = true)
                }
                println("Found ${_stockList.value.size} results for '$query'")

            } catch (e: Exception) {
                // This is unlikely to happen with mock data, but good to keep
                println("Mock search failed: ${e.message}")
                _stockList.value = emptyList()
            } finally {
                // 4. Set loading to false after the search is complete
                _isLoading.value = false
            }
            /***
            try {

                // Get the api key from our RetrofitInstance
                val apiKey = RetrofitInstance.getApiKey()

                if (apiKey == "YOUR_API_KEY" || apiKey.isBlank()) {
                    println("API Key is not set in RetrofitInstance.kt")
                    _stockList.value = emptyList()
                } else {
                    // Call the Retrofit service
                    val response = RetrofitInstance.api.searchSymbols(keywords = query, apiKey = apiKey)

                    // Map from the API model to your UI model
                    _stockList.value = response.bestMatches?.map { apiStock ->
                        Stock(name = apiStock.name, symbol = apiStock.symbol)
                    } ?: emptyList()
                }
                // --- END OF UPDATE ---
            } catch (e: Exception) {
                // Proper error handling
                println("API search failed: ${e.message}")
                _stockList.value = emptyList()
            } finally {
                _isLoading.value = false
            }
            */

        }
    }
}
