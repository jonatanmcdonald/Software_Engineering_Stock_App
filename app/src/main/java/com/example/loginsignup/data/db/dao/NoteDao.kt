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

// This interface defines the Data Access Object (DAO) for the Note entity.
@Dao
interface NoteDao {
    // This function inserts a note into the database.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note): Long

    // This function inserts a media item associated with a note into the database.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNoteMedia(media: NoteMedia)

    // This function deletes a media item associated with a note from the database.
    @Query("DELETE FROM note_media WHERE noteId = :id AND userId = :userId")
    suspend fun deleteNoteMediaById(id: Long, userId: Int)


    // This function updates the content of a note in the database.
    @Query("UPDATE notes SET content = :content WHERE watchlistId = :watchListId AND userId = :userId")
    suspend fun update(watchListId: Long, content: String?, userId: Int): Int

    // This function deletes a note from the database.
    @Delete
    suspend fun delete(note: Note)

    // This function retrieves the notes for a specific watchlist item.
    @Query("SELECT * FROM notes WHERE watchlistId = :watchlistId AND userId = :userId")
    suspend fun getNotesForWatchlist(watchlistId: Long, userId: Int): Note
}
