package com.example.loginsignup.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.loginsignup.data.db.entity.Note
import androidx.room.Query
import androidx.room.Transaction
import com.example.loginsignup.data.db.entity.NoteMedia
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNoteMedia(media: NoteMedia)

    @Query("DELETE FROM note_media WHERE noteId = :id AND userId = :userId")
    suspend fun deleteNoteMediaById(id: Long, userId: Int)


    @Query("UPDATE notes SET content = :content WHERE watchlistId = :watchListId AND userId = :userId")
    suspend fun update(watchListId: Long, content: String?, userId: Int): Int

    @Delete
    suspend fun delete(note: Note)

    @Query("SELECT * FROM notes WHERE watchlistId = :watchlistId AND userId = :userId")
    suspend fun getNotesForWatchlist(watchlistId: Long, userId: Int): Note
}
