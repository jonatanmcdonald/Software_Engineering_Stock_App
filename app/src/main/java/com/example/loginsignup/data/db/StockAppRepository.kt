package com.example.loginsignup.data.db

import androidx.lifecycle.LiveData
import com.example.loginsignup.data.db.dao.AlertDao
import com.example.loginsignup.data.db.dao.NoteDao
import com.example.loginsignup.data.db.dao.PortfolioDao
import com.example.loginsignup.data.db.dao.StockDao
import com.example.loginsignup.data.db.dao.TransactionDao
import com.example.loginsignup.data.db.dao.UserDao
import com.example.loginsignup.data.db.dao.WatchListDao
import com.example.loginsignup.data.db.entity.Alert
import com.example.loginsignup.data.db.entity.Note
import com.example.loginsignup.data.db.entity.Portfolio
import com.example.loginsignup.data.db.entity.Stock
import com.example.loginsignup.data.db.entity.Transaction
import com.example.loginsignup.data.db.entity.User
import com.example.loginsignup.data.db.entity.WatchList
import com.example.loginsignup.data.db.view.WatchListWithSymbol
import com.example.loginsignup.data.models.LastQuote
import com.example.loginsignup.data.models.NewsItem
import com.example.loginsignup.data.models.Profile
import com.example.loginsignup.data.models.RetrofitInstance.api
import com.example.loginsignup.data.models.RetrofitInstance.getApiKey
import kotlinx.coroutines.flow.Flow


class StockAppRepository(private val userDao: UserDao,
                         private val watchListDao: WatchListDao,
                         private val stockDao: StockDao,
                         private val transactionDao: TransactionDao,
                         private val portfolioDao: PortfolioDao,
                         private val alertDao: AlertDao,
                         private val noteDao: NoteDao,

                         ) {

    val readAllData: LiveData<List<User>> = userDao.readAllData()

    suspend fun addUser(user: User) {
        userDao.addUser(user)
    }

     fun getUserByEmailAndPassword(email: String, password: String): User? {
        return userDao.getUserByEmailAndPassword(email, password)
    }

     fun isEmailTaken(email: String): Boolean {
        return userDao.getUserByEmail(email) != null
    }

    suspend fun addWatchListItem(watchList: WatchList): Long {
        return watchListDao.addWatchListItem(watchList)
    }

    suspend fun updateWatchListItem(item: WatchList): Int {
        return watchListDao.updateWatchListItem(
            item.userId,
            item.id,
            item.name,
            item.stockId,
            updatedAt = System.currentTimeMillis()
        )
    }

    suspend fun deleteWatchListItem(itemId: Long): Int {
        return watchListDao.deleteWatchListItem(itemId)
    }


    fun searchStocks(query: String): LiveData<List<Stock>> {
        return stockDao.searchStocks(query)
    }


    suspend fun getStockId( ticker: String): Long {
        return stockDao.getStockId(ticker)
    }

    fun observeAllForUsers(userId: Int): Flow<List<WatchListWithSymbol>> {
        return watchListDao.observeAllForUser(userId)
    }

    suspend fun getNewsThisMonth(): List<NewsItem>
    {
        val news = api.getNews(token = getApiKey())
        //Log.d("StockAppRepository News", news.toString())
        return news
    }

     suspend fun fetchPrice(symbol: String): LastQuote =
         api.getLastQuote(symbol = symbol, token = getApiKey())

    suspend fun getProfile(symbol: String): Profile =
        api.getProfile(symbol = symbol, token = getApiKey())


     fun getTransForUser(userId: Int): Flow<List<Transaction>> {
        return transactionDao.getTransForUser(userId)
     }

    suspend fun addTransaction(transaction: Transaction) {
        return transactionDao.addTransaction(transaction)
    }

    fun observeUserPortfolio(userId: Int): Flow<List<Portfolio>> {
        return portfolioDao.getUserPortfolio(userId)
    }



    suspend fun insertNote(note: Note): Long {
         return noteDao.insert(note)
    }

    suspend fun updateNote(note: Note, userId: Int): Int {
        return noteDao.update(note.watchlistId, note.content, userId)
    }

    suspend fun deleteNote(note: Note) {
        noteDao.delete(note)
    }

    suspend fun getNotesForWatchlist(watchlistId: Long, userId: Int): Note {
        return noteDao.getNotesForWatchlist(watchlistId, userId)
    }
    suspend fun insertAlert(alert: Alert): Long {
        return alertDao.insertAlert(alert)
    }

    suspend fun updateAlert(alert: Alert): Int {
        return alertDao.updateAlert(alert.triggerParent, alert.userId, alert.symbol, alert.triggerPrice, alert.runCondition)
    }

    suspend fun deleteAlert(alert: Alert) {
        alertDao.deleteAlert(alert)
    }

    suspend fun getAlerts(userId: Int, symbol: String, triggerParent: String): Alert? {
        return alertDao.getAlerts(userId, symbol, triggerParent)
    }

    suspend fun toggleAlertActive(parent: String, userId: Int, symbol: String, isActive: Boolean, ) {
        alertDao.toggleAlertActive(parent, userId, symbol, isActive)
    }



}

