package com.example.loginsignup.data.db

import androidx.lifecycle.LiveData
import com.example.loginsignup.data.db.dao.PortfolioDao
import com.example.loginsignup.data.db.dao.StockDao
import com.example.loginsignup.data.db.dao.TransactionDao
import com.example.loginsignup.data.db.dao.UserDao
import com.example.loginsignup.data.db.dao.WatchListDao
import com.example.loginsignup.data.db.entity.Portfolio
import com.example.loginsignup.data.db.entity.Stock
import com.example.loginsignup.data.db.entity.Transaction
import com.example.loginsignup.data.db.entity.User
import com.example.loginsignup.data.db.entity.WatchList
import com.example.loginsignup.data.db.view.WatchListWithSymbol
import com.example.loginsignup.data.models.LastQuote
import com.example.loginsignup.data.models.Profile
import com.example.loginsignup.data.models.RetrofitInstance.api
import com.example.loginsignup.data.models.RetrofitInstance.getApiKey
import kotlinx.coroutines.flow.Flow

class StockAppRepository(private val userDao: UserDao,
                         private val watchListDao: WatchListDao,
                         private val stockDao: StockDao,
                         private val transactionDao: TransactionDao,
                         private val portfolioDao: PortfolioDao) {

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
            item.note,
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

    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.deleteTransaction(transaction)
    }

    suspend fun deleteAllTransForUser(userId: Int) {
        transactionDao.deleteAllTransForUser(userId)
    }

    fun observeUserPortfolio(userId: Int): Flow<List<Portfolio>> {
        return portfolioDao.getUserPortfolio(userId)
    }

    suspend fun sellStock(stockId: Long, qty: Double) {
        val portfolioItem = portfolioDao.getPortfolioById(stockId)

        if (portfolioItem != null && qty <= portfolioItem.qty) {
            val newQty = portfolioItem.qty - qty

            if (newQty > 0) {
                portfolioDao.updateQty(stockId, newQty)
            } else {
                portfolioDao.delete(portfolioItem)
            }

            val transaction = Transaction(
                userId = portfolioItem.userId,
                symbol = portfolioItem.symbol,
                qty = qty.toInt(),
                price = portfolioItem.avg_cost,
                side = "SELL",
                fees = 0.0,
                timestamp = System.currentTimeMillis()
            )
            transactionDao.addTransaction(transaction)
        } else {
            throw IllegalArgumentException("Not enough shares to sell")
        }
    }
}

