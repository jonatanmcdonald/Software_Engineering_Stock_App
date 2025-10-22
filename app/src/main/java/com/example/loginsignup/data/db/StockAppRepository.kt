package com.example.loginsignup.data.db

import androidx.lifecycle.LiveData
import com.example.loginsignup.data.db.entity.PriceToday
import com.example.loginsignup.data.db.dao.PricesTodayDao
import com.example.loginsignup.data.db.dao.StockDao
import com.example.loginsignup.data.db.dao.UserDao
import com.example.loginsignup.data.db.dao.WatchListDao
import com.example.loginsignup.data.db.entity.Stock
import com.example.loginsignup.data.db.entity.User
import com.example.loginsignup.data.db.entity.WatchList
import com.example.loginsignup.data.db.view.WatchListWithSymbol
import com.example.loginsignup.data.models.ApiResponse
import com.example.loginsignup.data.models.RetrofitInstance.api
import kotlinx.coroutines.flow.Flow

class StockAppRepository(private val priceDao: PricesTodayDao, private val userDao: UserDao, private val watchListDao: WatchListDao, private val stockDao: StockDao) {

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

    suspend fun existsForUser(userId: String, stockId: Long): Boolean {
        return watchListDao.existsForUser(userId, stockId)
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

    fun getAllForUser(userId: String): LiveData<List<WatchList>> {
        return watchListDao.getAllForUser(userId)
    }

    fun getWatchListItem(userId: String, stockId: Long): LiveData<WatchList?> {
        return watchListDao.getWatchListItem(userId, stockId)
    }

    fun getAllForUserWithSymbol(userId: String): LiveData<List<WatchListWithSymbol>>
    {
        return watchListDao.getAllForUserWithSymbol(userId)
    }

    fun getWatchListItemWithSymbol(userId: String, stockId: Long): LiveData<WatchListWithSymbol?>
    {
        return watchListDao.getWatchListItemWithSymbol(userId, stockId)
    }

     suspend fun getStockSymbol(id: Long): String
    {
        return stockDao.getStockSymbol(id)
    }

    suspend fun upsertAll(items: List<Stock>): List<Long> {
        return stockDao.upsertAll(items)
    }

    fun searchStocks(query: String): LiveData<List<Stock>> {
        return stockDao.searchStocks(query)
    }

    suspend fun getAllStocks(): List<Stock> {
        return stockDao.getAllStocks()
    }


    suspend fun getStockId( stockSymbol: String): Long {
        return stockDao.getStockId(stockSymbol)
    }

    suspend fun upsertAllPrice(items: List<PriceToday>) {
        priceDao.upsertAll(items)
    }

    fun observeSlot(stockId: Long, slot: Int): Flow<PriceToday?> {
        return priceDao.observeSlot(stockId, slot)
    }

    suspend fun getForSlot(stockId: Long, slot: Int): PriceToday? {
        return priceDao.getForSlot(stockId, slot)
    }

    suspend fun fetchDailyPrices(symbol: String, apiKey: String): ApiResponse =
        api.getDailyPrices(symbol = symbol, apiKey = apiKey)
}