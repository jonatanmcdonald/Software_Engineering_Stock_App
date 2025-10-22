package com.example.loginsignup.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Dao
import androidx.room.Query
import com.example.loginsignup.data.db.entity.WatchList
import com.example.loginsignup.data.db.view.WatchListWithSymbol

@Dao
interface WatchListDao {
    // CREATE
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addWatchListItem(item: WatchList): Long

    @Query("""
        UPDATE watchlist
        SET name = :name,
            note = :note,
            stockId = :stockId,
            updatedAt = :updatedAt
        WHERE id = :itemId AND userId = :userId
    """)
    suspend fun updateWatchListItem(
        userId: String,
        itemId: Long,
        name: String,
        note: String?,
        stockId: Long,
        updatedAt: Long
    ): Int

    // EXISTS
    @Query("SELECT EXISTS(SELECT 1 FROM watchlist WHERE userId = :userId AND stockId = :stockId LIMIT 1)")
    suspend fun existsForUser(userId: String, stockId: Long): Boolean

    // DELETE
    @Query("DELETE FROM watchlist WHERE id = :itemId")
    suspend fun deleteWatchListItem(itemId: Long): Int

    // READS (raw table rows)
    @Query("SELECT * FROM watchlist WHERE userId = :userId ORDER BY updatedAt DESC, createdAt DESC")
    fun getAllForUser(userId: String): LiveData<List<WatchList>>

    @Query("SELECT * FROM watchlist WHERE userId = :userId AND stockId = :stockId LIMIT 1")
    fun getWatchListItem(userId: String, stockId: Long): LiveData<WatchList?>

    // READS (JOINed view that already includes symbol) — use these in your UI!
    @Query("""
        SELECT * FROM WatchListWithSymbol
        WHERE userId = :userId
        ORDER BY updatedAt DESC, createdAt DESC
    """)
    fun getAllForUserWithSymbol(userId: String): LiveData<List<WatchListWithSymbol>>

    @Query("""
        SELECT * FROM WatchListWithSymbol
        WHERE userId = :userId AND stockId = :stockId
        LIMIT 1
    """)
    fun getWatchListItemWithSymbol(userId: String, stockId: Long): LiveData<WatchListWithSymbol?>
}
