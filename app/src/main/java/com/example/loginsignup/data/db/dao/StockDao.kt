package com.example.loginsignup.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.loginsignup.data.db.entity.Stock

// This interface defines the Data Access Object (DAO) for the Stock entity.
@Dao
interface StockDao {

    // This function inserts a list of stocks into the database, ignoring any conflicts.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(list: List<Stock>)

    // This function counts the number of rows in the stocks table.
    @Query("SELECT COUNT(*) FROM stocks")
    suspend fun countRows(): Long

    // (optional) for ultra-fast batch via small chunks:
    // This function inserts a list of stocks in chunks.
    @Transaction
    suspend fun insertChunked(all: List<Stock>, chunkSize: Int = 500) {
        var i = 0
        while (i < all.size) {
            val j = minOf(i + chunkSize, all.size)
            insertAll(all.subList(i, j))
            i = j
        }
    }
    // This function inserts or updates a list of stocks.
    @Insert(onConflict = OnConflictStrategy.IGNORE) // ABORT to respect the unique index
    suspend fun upsertAll(items: List<Stock>): List<Long>

    // This function retrieves all stocks from the database.
    @Query("SELECT id, name, ticker FROM stocks ORDER BY ticker")
    suspend fun getAllStocks(): List<Stock>

    // This function retrieves the ID of a stock by its ticker.
    @Query("SELECT id FROM stocks WHERE ticker = :ticker")
    suspend fun getStockId(ticker: String): Long

    // This function retrieves the ticker of a stock by its ID.
    @Query("SELECT ticker FROM stocks WHERE id = :id")
    suspend fun getStockSymbol(id: Long): String

    // This function searches for stocks by name or ticker.
    @Query(
        """
        SELECT id, name, ticker FROM stocks
        WHERE name   LIKE '%' || :q || '%' ESCAPE '\' COLLATE NOCASE
           OR ticker LIKE '%' || :q || '%' ESCAPE '\' COLLATE NOCASE
        ORDER BY
          (ticker = :q) DESC,                                 -- exact symbol first
          (name   = :q) DESC,                                 -- exact name next
          (ticker LIKE :q || '%') DESC,                       -- prefix on symbol
          (name   LIKE :q || '%') DESC,                       -- prefix on name
          INSTR(LOWER(ticker), LOWER(:q)),                    -- then substring position
          INSTR(LOWER(name),   LOWER(:q)),
          ticker ASC
        LIMIT :limit
    """
    )
    fun searchStocks(q: String, limit: Int = 20): LiveData<List<Stock>>

}