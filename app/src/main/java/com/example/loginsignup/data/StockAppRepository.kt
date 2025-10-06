package com.example.loginsignup.data

import androidx.lifecycle.LiveData

class StockAppRepository(private val userDao: UserDao) {

    val readAllData: LiveData<List<User>> = userDao.readAllData()

    suspend fun addUser(user: User){
        userDao.addUser(user)

    }
}