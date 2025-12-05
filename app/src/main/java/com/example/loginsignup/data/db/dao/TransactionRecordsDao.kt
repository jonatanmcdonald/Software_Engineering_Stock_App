// Package declaration for the DAO
package com.example.loginsignup.data.db.dao

// Import statements for necessary classes and libraries
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.loginsignup.data.db.entity.TransactionRecords
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the TransactionRecords entity.
 */
@Dao
interface TransactionRecordsDao {

    /**
     * Inserts a transaction into the database.
     *
     * @param transaction The transaction to insert.
     */
    // This function inserts a transaction into the database. If a transaction with the same primary key already exists, it will be replaced.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionRecords)

    /**
     * Gets all transactions for a user, sorted by date.
     *
     * @param userId The ID of the user.
     * @return A Flow of the list of transactions.
     */
    // This query selects all columns from the transactions table where the userId matches the given id, ordered by date in descending order.
    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY date DESC")
    fun getTransactions(userId: Int): Flow<List<TransactionRecords>>

    /**
     * Gets all transactions for a user, sorted by ticker.
     *
     * @param userId The ID of the user.
     * @return A Flow of the list of transactions.
     */
    // This query selects all columns from the transactions table where the userId matches the given id, ordered by ticker in ascending order.
    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY ticker ASC")
    fun getTransactionsAlphabetical(userId: Int): Flow<List<TransactionRecords>>



    /**
     * Gets a transaction by its ID.
     *
     * @param id The ID of the transaction.
     * @return The transaction, or null if it doesn't exist.
     */
    // This query selects all columns from the transactions table where the id matches the given id, and limits the result to one.
    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    suspend fun getTransactionById(id: Int): TransactionRecords?

    /**
     * Updates a transaction in the database.
     *
     * @param transaction The transaction to update.
     */
    // This function updates a transaction in the database.
    @Update
    suspend fun updateTransaction(transaction: TransactionRecords)

    /**
     * Deletes a transaction from the database.
     *
     * @param transaction The transaction to delete.
     */
    // This function deletes a transaction from the database.
    @Delete
    suspend fun deleteTransaction(transaction: TransactionRecords)
}