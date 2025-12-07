package com.example.loginsignup.data.db

import android.util.Log
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
import com.example.loginsignup.data.db.entity.NoteMedia
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
import com.example.loginsignup.screens.UiMedia
import kotlinx.coroutines.flow.Flow


// This class is the single source of truth for all app data.
class StockAppRepository(private val userDao: UserDao,
                         private val watchListDao: WatchListDao,
                         private val stockDao: StockDao,
                         private val transactionDao: TransactionDao,
                         private val portfolioDao: PortfolioDao,
                         private val alertDao: AlertDao,
                         private val noteDao: NoteDao,

                         ) {

    // A LiveData object that contains a list of all users.
    val readAllData: LiveData<List<User>> = userDao.readAllData()

    // This function adds a new user to the database.
    suspend fun addUser(user: User) {
        userDao.addUser(user)
    }

    // This function retrieves a user from the database by email and password.
     fun getUserByEmailAndPassword(email: String, password: String): User? {
        return userDao.getUserByEmailAndPassword(email, password)
    }

    // This function checks if an email address is already taken.
    suspend fun isEmailTaken(email: String): Boolean {
        return userDao.getUserByEmail(email) != null
    }

    // This function retrieves a user from the database by email.
    suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email.trim().lowercase())
    }

    // This function resets the password for a user.
    suspend fun resetPassword(email: String, newPassword: String): Boolean {
        val rows = userDao.resetPassword(email, newPassword)
        return rows > 0
    }

    // This function checks if an email address exists in the database.
    suspend fun emailExists(email: String): Boolean {
        return userDao.emailExists(email.trim().lowercase())
    }

    // This function adds a new item to the watchlist.
    suspend fun addWatchListItem(watchList: WatchList): Long {
        return watchListDao.addWatchListItem(watchList)
    }

    // This function updates an existing item in the watchlist.
    suspend fun updateWatchListItem(item: WatchList): Int {
        return watchListDao.updateWatchListItem(
            item.userId,
            item.id,
            item.name,
            item.stockId,
            updatedAt = System.currentTimeMillis()
        )
    }

    // This function deletes an item from the watchlist.
    suspend fun deleteWatchListItem(itemId: Long): Int {
        return watchListDao.deleteWatchListItem(itemId)
    }


    // This function searches for stocks by a given query.
    fun searchStocks(query: String): LiveData<List<Stock>> {
        return stockDao.searchStocks(query)
    }


    // This function retrieves the ID of a stock by its ticker.
    suspend fun getStockId( ticker: String): Long {
        return stockDao.getStockId(ticker)
    }

    // This function observes all watchlist items for a specific user.
    fun observeAllForUsers(userId: Int): Flow<List<WatchListWithSymbol>> {
        return watchListDao.observeAllForUser(userId)
    }

    // This function retrieves the news for the current month.
    suspend fun getNewsThisMonth(): List<NewsItem>
    {
        val news = api.getNews(token = getApiKey())
        //Log.d("StockAppRepository News", news.toString())
        return news
    }

    // This function fetches the last price for a given stock symbol.
     suspend fun fetchPrice(symbol: String): LastQuote =
         api.getLastQuote(symbol = symbol, token = getApiKey())

    // This function retrieves the profile for a given stock symbol.
    suspend fun getProfile(symbol: String): Profile =
        api.getProfile(symbol = symbol, token = getApiKey())


    // This function retrieves all transactions for a specific user.
     fun getTransForUser(userId: Int): Flow<List<Transaction>> {
        return transactionDao.getTransForUser(userId)
     }

    // This function adds a new transaction to the database.
    suspend fun addTransaction(transaction: Transaction) {
        return transactionDao.addTransaction(transaction)
    }

    // This function observes the portfolio of a specific user.
    fun observeUserPortfolio(userId: Int): Flow<List<Portfolio>> {
        return portfolioDao.getUserPortfolio(userId)
    }



    // This function inserts a new note into the database.
    suspend fun insertNote(note: Note): Long {
         return noteDao.insert(note)
    }

    // This function updates an existing note in the database.
    suspend fun updateNote(note: Note, userId: Int): Int {
        return noteDao.update(note.watchlistId, note.content, userId)
    }

    // This function deletes a note from the database.
    suspend fun deleteNote(note: Note) {
        noteDao.delete(note)
    }

    // This function retrieves all notes for a specific watchlist item.
    suspend fun getNotesForWatchlist(watchlistId: Long, userId: Int): Note {
        return noteDao.getNotesForWatchlist(watchlistId, userId)
    }
    // This function inserts a new alert into the database.
    suspend fun insertAlert(alert: Alert): Long {
        return alertDao.insertAlert(alert)
    }

    // This function updates an existing alert in the database.
    suspend fun updateAlert(alert: Alert): Int {
        return alertDao.updateAlert(alert.triggerParent, alert.userId, alert.symbol, alert.triggerPrice, alert.runCondition)
    }

    // This function deletes an alert from the database.
    suspend fun deleteAlert(alert: Alert) {
        alertDao.deleteAlert(alert)
    }

    // This function inserts a new media item for a note into the database.
    suspend fun insertNoteMedia(noteId:Long, uri:String, type:String, userId: Int) {
        noteDao.insertNoteMedia(
            NoteMedia(
                noteId = noteId,
                userId = userId,
                uri = uri,
                type = type
            )
        )
    }

    // This function retrieves an alert from the database by its user ID, symbol, and trigger parent.
    suspend fun getAlerts(userId: Int, symbol: String, triggerParent: String): Alert? {
        return alertDao.getAlerts(userId, symbol, triggerParent)
    }

    // This function toggles the active state of an alert.
    suspend fun toggleAlertActive(parent: String, userId: Int, symbol: String, isActive: Boolean, ) {
        alertDao.toggleAlertActive(parent, userId, symbol, isActive)
    }

    // This function saves a note with its associated media.
    suspend fun saveNoteWithMedia(
        existingNoteId: Long?,
        watchlistId: Long,
        userId: Int,
        content: String,
        media: List<UiMedia>
    ) {

            val now = System.currentTimeMillis()

            // 1) Upsert note and get noteId
            val noteId = if (existingNoteId != null) {
                // Update existing note
                noteDao.update(
                    watchListId = watchlistId,
                    content = content,
                    userId = userId
                )
                existingNoteId
            } else {
                // Insert new note
                noteDao.insert(
                    Note(
                        watchlistId = watchlistId,
                        userId = userId,
                        content = content,
                        timestamp = now
                    )
                )
            }

        Log.d("StockAppRepository", "saveNoteWithMedia: $noteId")

            // 2) Replace media for that note with the provided list
            noteDao.deleteNoteMediaById(noteId, userId)
            //noteDao.deleteNote(noteId)

            media.forEachIndexed { index, m ->
                noteDao.insertNoteMedia(
                    NoteMedia(
                        noteId = noteId,
                        uri = m.uri,
                        type = m.type,
                        sortOrder = index,
                        userId = userId
                    )
                )
            }
    }




}

