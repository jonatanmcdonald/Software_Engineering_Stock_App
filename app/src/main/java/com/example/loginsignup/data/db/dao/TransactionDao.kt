package com.example.loginsignup.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.loginsignup.data.db.entity.Transaction
import kotlinx.coroutines.flow.Flow

// This interface defines the Data Access Object (DAO) for the Transaction entity.
@Dao
interface TransactionDao {
    // This function inserts a transaction into the database.
    @Insert
    suspend fun addTransaction (items: Transaction)

    // This function retrieves all transactions for a specific user, ordered by timestamp.
    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY timestamp DESC")
    fun getTransForUser(userId: Int): Flow<List<Transaction>>

    // This function deletes a transaction from the database.
    @Delete
    suspend fun deleteTransaction(item: Transaction)

    // This function deletes all transactions for a specific user.
    @Query("DELETE FROM transactions WHERE userId = :userId")
    suspend fun deleteAllTransForUser(userId: Int)


}