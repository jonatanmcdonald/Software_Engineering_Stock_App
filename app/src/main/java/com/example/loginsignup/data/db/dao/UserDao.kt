package com.example.loginsignup.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.loginsignup.data.db.entity.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun addUser(user: User)

    @Query("SELECT * FROM user_table ORDER BY id ASC")
    fun readAllData(): LiveData<List<User>>

    @Query("SELECT * FROM user_table WHERE email = :email AND password = :password Limit 1")
    fun getUserByEmailAndPassword(email: String, password: String): User?

    @Query("SELECT * FROM user_table WHERE email = :email Limit 1")
    fun getUserByEmail(email: String): User?


}