package com.example.loginsignup.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.loginsignup.data.db.entity.Stock

@Dao
interface StockDao {
    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE) // ABORT to respect the unique index
    suspend fun upsertAll(items: List<Stock>): List<Long>

    @Query("SELECT id, name, ticker FROM stocks ORDER BY ticker")
    suspend fun getAllStocks(): List<Stock>

    @Query("SELECT id FROM stocks WHERE ticker = :ticker")
    suspend fun getStockId(ticker: String): Long

    @Query("SELECT ticker FROM stocks WHERE id = :id")
    suspend fun getStockSymbol(id: Long): String

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