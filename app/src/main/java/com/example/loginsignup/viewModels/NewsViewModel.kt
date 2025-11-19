package com.example.loginsignup.viewModels

import android.app.Application
import android.os.Build
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

class NewsViewModel(application: Application): AndroidViewModel(application) {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _news = MutableStateFlow(emptyList<NewsItem>())
    val news: StateFlow<List<NewsItem>> = _news.asStateFlow()


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


        repository =
            StockAppRepository(userDao, watchListDao, stockDao, transactionDao, portfolioDao, alertDao, noteDao)
        getNews()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getNews()
    {
        viewModelScope.launch {
            _isLoading.value = true
             _news.value = repository.getNewsThisMonth()
            _isLoading.value = false
        }

    }

}