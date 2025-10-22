package com.example.loginsignup.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.loginsignup.data.db.entity.PriceToday
import kotlinx.coroutines.flow.Flow
@Dao
interface PricesTodayDao {

    //Upsert many bars (safe to call repeatedly)
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun upsertAll(items: List<PriceToday>)

        // Exact slot for a stock
        @Query("""
            SELECT * FROM prices_today
            WHERE stockId = :stockId AND slot5m = :slot
            LIMIT 1
          """)
        suspend fun getForSlot(stockId: Long, slot: Int): PriceToday?

        // Observe one slot as it updates (ticker UI)
        @Query("""
            SELECT * FROM prices_today
            WHERE stockId = :stockId AND slot5m = :slot
            LIMIT 1
          """)
        fun observeSlot(stockId: Long, slot: Int): Flow<PriceToday?>

        // Midnight reset
        @Query("DELETE FROM prices_today")
        suspend fun resetForNewDay()
    }