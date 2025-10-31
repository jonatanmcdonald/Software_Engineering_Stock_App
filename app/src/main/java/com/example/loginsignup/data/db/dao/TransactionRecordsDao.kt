package com.example.loginsignup.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.loginsignup.data.db.entity.TransactionRecords
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionRecordsDao {

    // Insert a transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionRecords)

    // Get all transactions for a specific user, sorted by date descending
    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY date DESC")
    fun getTransactions(userId: Int): Flow<List<TransactionRecords>>

    // Get all transactions for a specific user, sorted alphabetically by ticker
    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY ticker ASC")
    fun getTransactionsAlphabetical(userId: Int): Flow<List<TransactionRecords>>



    // Get a transaction by ID
    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    suspend fun getTransactionById(id: Int): TransactionRecords?

    // Update a transaction
    @Update
    suspend fun updateTransaction(transaction: TransactionRecords)

    // Delete a transaction
    @Delete
    suspend fun deleteTransaction(transaction: TransactionRecords)
}