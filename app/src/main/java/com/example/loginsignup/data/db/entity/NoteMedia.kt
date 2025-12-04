package com.example.loginsignup.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "note_media",
foreignKeys = [
    ForeignKey(
        entity = Note::class,
        parentColumns = ["id"],
        childColumns = ["noteId"],
        onDelete = ForeignKey.CASCADE
    ),

    ForeignKey(
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )
],
    indices = [Index("noteId") ]
)
data class NoteMedia (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val userId: Int,
    val noteId: Long,
    val type: String,
    val uri: String,
    val mimeType: String? = null,
    val sortOrder: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
