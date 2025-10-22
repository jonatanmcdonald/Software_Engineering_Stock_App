package com.example.loginsignup.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "watchlist",
    foreignKeys = [
        ForeignKey(
            entity = Stock::class,
            parentColumns = ["id"],
            childColumns = ["stockId"],
            onDelete = ForeignKey.Companion.CASCADE
        )
    ],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["stockId"]),
        // optional: enforce "one stock per user" uniqueness
        Index(value = ["userId", "stockId"], unique = true)
    ]
)


data class WatchList(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,          // surrogate PK
    val userId: String,           // set from the logged-in user
    val name: String,
    val note: String? = null,
    val stockId: Long,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()

)