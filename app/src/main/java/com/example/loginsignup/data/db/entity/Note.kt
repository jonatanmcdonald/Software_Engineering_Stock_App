package com.example.loginsignup.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


// This class represents a note in the database.
@Entity(tableName = "notes", // The name of the table in the database.
    foreignKeys = [ // The foreign keys for the table.
        ForeignKey( // A foreign key to the WatchList entity.
            entity = WatchList::class,
            parentColumns = ["id"],
            childColumns = ["watchlistId"],
            onDelete = ForeignKey.CASCADE
        ),

        ForeignKey( // A foreign key to the User entity.
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["watchlistId", "userId"]), Index("userId")] // The indices for the table.
)
class Note(
    @PrimaryKey(autoGenerate = true) // The primary key for the table.
    val id: Long = 0L, // The unique ID of the note.
    val userId: Int, // The ID of the user who created the note.
    val watchlistId: Long, // The ID of the watchlist item to which the note is attached.
    val content: String? = null, // The content of the note.
    val timestamp: Long = System.currentTimeMillis() // The timestamp when the note was created.
)
