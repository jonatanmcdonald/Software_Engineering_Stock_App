package com.example.loginsignup.data

import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.Date

@Dao
interface WatchListDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE) // ABORT to respect the unique index
    suspend fun addWatchListItem(item: WatchList): Long

    @Query("UPDATE watchlist SET name = :name, note = :note, updatedAt = :updatedAt WHERE userId = :userId AND stock = :stock")
    suspend fun updateWatchListItem(
        userId: String,
        name: String,
        note: String?,
        stock: String,
        updatedAt: Long
    ): Int

    @Query("SELECT EXISTS(SELECT 1 FROM watchlist WHERE userId = :userId AND stock = :stock LIMIT 1)")
    suspend fun existsForUser(userId: String, stock: String): Boolean

    @Query("DELETE FROM watchlist WHERE userId = :userId AND stock = :stock")
    suspend fun deleteWatchListItem(userId: String, stock: String): Int

    @Query("SELECT * FROM watchlist WHERE userId = :userId ORDER BY updatedAt DESC, createdAt DESC")
    fun getAllForUser(userId: String): LiveData<List<WatchList>>

    @Query("SELECT * FROM watchlist WHERE userId = :userId AND stock = :stock LIMIT 1")
    fun getWatchListItem(userId: String, stock: String): LiveData<WatchList?>
}
