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

    @Query("SELECT id FROM stocks WHERE symbol = :symbol")
    suspend fun getStockId(symbol: String): Long

    @Query("SELECT symbol FROM stocks WHERE id = :id")
    suspend fun getStockSymbol(id: Long): String

    @Query(
        """
        SELECT * FROM stocks
        WHERE name   LIKE '%' || :q || '%' ESCAPE '\' COLLATE NOCASE
           OR symbol LIKE '%' || :q || '%' ESCAPE '\' COLLATE NOCASE
        ORDER BY
          (symbol = :q) DESC,                                 -- exact symbol first
          (name   = :q) DESC,                                 -- exact name next
          (symbol LIKE :q || '%') DESC,                       -- prefix on symbol
          (name   LIKE :q || '%') DESC,                       -- prefix on name
          INSTR(LOWER(symbol), LOWER(:q)),                    -- then substring position
          INSTR(LOWER(name),   LOWER(:q)),
          symbol ASC
        LIMIT :limit
    """
    )
    fun searchStocks(q: String, limit: Int = 20): LiveData<List<Stock>>

}