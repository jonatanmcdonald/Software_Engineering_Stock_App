package com.example.loginsignup.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import com.example.loginsignup.data.db.entity.Note
import androidx.room.Query

@Dao
interface NoteDao {
    @Insert
    suspend fun insert(note: Note): Long

    @Query("UPDATE notes SET content = :content WHERE watchlistId = :watchListId AND userId = :userId")
    suspend fun update(watchListId: Long, content: String?, userId: Int): Int

    @Delete
    suspend fun delete(note: Note)

    @Query("SELECT * FROM notes WHERE watchlistId = :watchlistId AND userId = :userId")
    suspend fun getNotesForWatchlist(watchlistId: Long, userId: Int): Note
}
