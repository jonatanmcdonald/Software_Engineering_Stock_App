package com.example.loginsignup.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.loginsignup.data.db.entity.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert
    suspend fun addTransaction (items: Transaction)

    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY timestamp DESC")
    fun getTransForUser(userId: Int): Flow<List<Transaction>>

    @Delete
    suspend fun deleteTransaction(item: Transaction)

    @Query("DELETE FROM transactions WHERE userId = :userId")
    suspend fun deleteAllTransForUser(userId: Int)


}