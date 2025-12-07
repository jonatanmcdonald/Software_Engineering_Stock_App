package com.example.loginsignup.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Dao
import androidx.room.Query
import com.example.loginsignup.data.db.entity.WatchList
import com.example.loginsignup.data.db.view.WatchListWithSymbol
import kotlinx.coroutines.flow.Flow


// This interface defines the Data Access Object (DAO) for the WatchList entity.
@Dao
interface WatchListDao {
    // CREATE
    // This function inserts a watchlist item into the database, ignoring any conflicts.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addWatchListItem(item: WatchList): Long

    // This function updates a watchlist item in the database.
    @Query("""
        UPDATE watchlist
        SET name = :name,
            stockId = :stockId,
            updatedAt = :updatedAt
        WHERE id = :itemId AND userId = :userId
    """)
    suspend fun updateWatchListItem(
        userId: Int,
        itemId: Long,
        name: String,
        stockId: Long,
        updatedAt: Long
    ): Int

    // EXISTS
    // This function checks if a watchlist item exists for a specific user and stock.
    @Query("SELECT EXISTS(SELECT 1 FROM watchlist WHERE userId = :userId AND stockId = :stockId LIMIT 1)")
    suspend fun existsForUser(userId: Int, stockId: Long): Boolean

    // DELETE
    // This function deletes a watchlist item from the database.
    @Query("DELETE FROM watchlist WHERE id = :itemId")
    suspend fun deleteWatchListItem(itemId: Long): Int

    // READS (raw table rows)
    // This function retrieves all watchlist items for a specific user.
    @Query("SELECT * FROM watchlist WHERE userId = :userId ORDER BY updatedAt DESC, createdAt DESC")
    fun getAllForUser(userId: Int): LiveData<List<WatchList>>

    // This function retrieves a specific watchlist item for a user and stock.
    @Query("SELECT * FROM watchlist WHERE userId = :userId AND stockId = :stockId LIMIT 1")
    fun getWatchListItem(userId: Int, stockId: Long): LiveData<WatchList?>

    // READS (JOINed view that already includes symbol) — use these in your UI!
    // This function retrieves all watchlist items for a specific user, with the stock symbol included.
    @Query("""
        SELECT * FROM WatchListWithSymbol
        WHERE userId = :userId
        ORDER BY updatedAt DESC, createdAt DESC
    """)
    fun getAllForUserWithSymbol(userId: Int): LiveData<List<WatchListWithSymbol>>

    // This function retrieves a specific watchlist item for a user and stock, with the stock symbol included.
    @Query("""
        SELECT * FROM WatchListWithSymbol
        WHERE userId = :userId AND stockId = :stockId
        LIMIT 1
    """)
    fun getWatchListItemWithSymbol(userId: Int, stockId: Long): LiveData<WatchListWithSymbol?>

    /*@Query("""
          SELECT w.id, w.userId, w.stockId, w.name, w.note, w.createdAt, w.updatedAt, s.ticker
          FROM watchlist w JOIN stocks s ON s.id = w.stockId
          WHERE w.userId = :userId
          ORDER BY w.updatedAt DESC, w.createdAt DESC
        """)*/
    // This function observes all watchlist items for a specific user, with associated note and media information.
    @Query("""
    SELECT 
        w.id        AS id,
        w.userId    AS userId,
        w.stockId   AS stockId,
        w.name      AS name,
        n.id        AS noteId,
        n.content   AS content,
        n.timestamp AS timestamp,
        s.ticker    AS ticker,
        w.createdAt AS createdAt,
        w.updatedAt AS updatedAt,
        m.mediaUris AS mediaUris          
    FROM watchlist w 
    JOIN stocks s ON s.id = w.stockId
    LEFT JOIN notes n ON n.watchlistId = w.id
    LEFT JOIN (
        SELECT
            noteId,
            GROUP_CONCAT(uri, '|') AS mediaUris
        FROM note_media
        GROUP BY noteId
    ) m ON m.noteId = n.id
    WHERE w.userId = :userId
    ORDER BY w.updatedAt DESC, w.createdAt DESC
""")
    fun observeAllForUser(userId: Int): Flow<List<WatchListWithSymbol>>


}
