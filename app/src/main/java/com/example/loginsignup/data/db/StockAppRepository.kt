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
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class StockAppRepository(private val priceDao: PricesTodayDao, private val userDao: UserDao, private val watchListDao: WatchListDao, private val stockDao: StockDao) {

    private val MARKET_ZONE = ZoneId.of("US/Eastern")
    private val FEED_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.US)

    fun slotFromEasternString(ts: String): Int {
        val ldt = LocalDateTime.parse(ts, FEED_FMT)           // ts is in Eastern per meta
        val minutes = ldt.atZone(MARKET_ZONE).toLocalTime().toSecondOfDay() / 60
        return minutes / 5
    }
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

    fun ApiResponse.toTodayRows(
        stockId: Long,
    ): List<PriceToday> {
        val series = timeSeries5m ?: return emptyList()

        return series.entries
            .asSequence()
            .map { (ts, c) ->
                PriceToday(
                    stockId = stockId,
                    slot5m = slotFromEasternString(ts),
                    open = c.open?.toDoubleOrNull(),
                    high = c.high?.toDoubleOrNull(),
                    low = c.low?.toDoubleOrNull(),
                    close = c.close?.toDoubleOrNull(),
                    volume = c.volume?.toLongOrNull()
                )
            }
            .sortedBy { it.slot5m }
            .toList()
    }
}