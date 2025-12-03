package com.example.loginsignup.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(tableName = "notes",
    foreignKeys = [
        ForeignKey(
            entity = WatchList::class,
            parentColumns = ["id"],
            childColumns = ["watchlistId"],
            onDelete = ForeignKey.CASCADE
        ),

        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["watchlistId", "userId"])]
)
class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val userId: Int,
    val watchlistId: Long,
    val imageUrl: String? = null,
    val title: String? = null,
    val content: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
