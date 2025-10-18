package com.example.loginsignup.data

import androidx.lifecycle.LiveData


class StockAppRepository(private val userDao: UserDao, private val watchListDao: WatchListDao) {

    val readAllData: LiveData<List<User>> = userDao.readAllData()

    suspend fun addUser(user: User){
        userDao.addUser(user)
    }

    suspend fun getUserByEmailAndPassword(email: String, password: String): User? {
        return userDao.getUserByEmailAndPassword(email, password)
    }

    suspend fun isEmailTaken(email: String): Boolean {
        return userDao.getUserByEmail(email) != null
    }

    suspend fun existsForUser(userId: String, stock: String): Boolean{
        return watchListDao.existsForUser(userId, stock)
    }
    suspend fun addWatchListItem(watchList: WatchList): Long{
        return watchListDao.addWatchListItem(watchList)
    }

    suspend fun updateWatchListItem(item: WatchList): Int {
       return watchListDao.updateWatchListItem(item.userId, item.name, item.note, item.stock, updatedAt = System.currentTimeMillis())
    }

    suspend fun deleteWatchListItem(userId: String, itemId: String ): Int
    {
        return watchListDao.deleteWatchListItem(userId, itemId)
    }

    fun getAllForUser(userId: String): LiveData<List<WatchList>>
    {
        return watchListDao.getAllForUser(userId)
    }

    fun getWatchListItem(userId: String, itemId: String): LiveData<WatchList?>
    {
        return watchListDao.getWatchListItem(userId, itemId)
    }

}