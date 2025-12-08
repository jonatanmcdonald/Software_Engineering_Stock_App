package com.example.loginsignup.viewModels

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginsignup.data.db.StockAppDatabase
import com.example.loginsignup.data.db.StockAppRepository
import com.example.loginsignup.data.models.NewsItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

// This class is the ViewModel for the News screen.
@RequiresApi(Build.VERSION_CODES.O)
class NewsViewModel(application: Application): AndroidViewModel(application) {
    private val _isLoading = MutableStateFlow(false) // A private mutable state flow to track the loading state.
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow() // A public state flow to expose the loading state.
    private val _news = MutableStateFlow(emptyList<NewsItem>()) // A private mutable state flow to hold the list of news items.
    val news: StateFlow<List<NewsItem>> = _news.asStateFlow() // A public state flow to expose the list of news items.
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


        repository = // Creates the repository.
            StockAppRepository(userDao, watchListDao, stockDao, transactionDao, portfolioDao, alertDao, noteDao)
        getNews() // Fetches the news.
    }

    // This function fetches the news.
    fun getNews() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val items = repository.getNewsThisMonth()
                _news.value = items
            } catch (e: IOException) {
                // No internet / timeout / DNS, etc.
                Log.e("NewsViewModel", "Network error while loading news", e)
                _news.value = emptyList()
            } catch (e: Exception) {
                // Any other unexpected error
                Log.e("NewsViewModel", "Unexpected error while loading news", e)
                _news.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

}