package com.example.loginsignup.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// This class represents a media item associated with a note in the database.
@Entity(tableName = "note_media", // The name of the table in the database.
foreignKeys = [ // The foreign keys for the table.
    ForeignKey( // A foreign key to the Note entity.
        entity = Note::class,
        parentColumns = ["id"],
        childColumns = ["noteId"],
        onDelete = ForeignKey.CASCADE
    ),

    ForeignKey( // A foreign key to the User entity.
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )
],
    indices = [Index("noteId") ] // The indices for the table.
)
data class NoteMedia (
    @PrimaryKey(autoGenerate = true) // The primary key for the table.
    val id: Long = 0L, // The unique ID of the media item.
    val userId: Int, // The ID of the user who owns the media item.
    val noteId: Long, // The ID of the note to which the media item is attached.
    val type: String, // The type of the media item (e.g., "image", "video").
    val uri: String, // The URI of the media item.
    val mimeType: String? = null, // The MIME type of the media item.
    val sortOrder: Int = 0, // The sort order of the media item.
    val createdAt: Long = System.currentTimeMillis() // The timestamp when the media item was created.
)
