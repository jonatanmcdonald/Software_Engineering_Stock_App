package com.example.loginsignup.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// This class represents a watchlist item in the database.
@Entity(
    tableName = "watchlist", // The name of the table in the database.
    foreignKeys = [ // The foreign keys for the table.
        ForeignKey( // A foreign key to the Stock entity.
            entity = Stock::class,
            parentColumns = ["id"],
            childColumns = ["stockId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey( // A foreign key to the User entity.
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [ // The indices for the table.
        Index(value = ["userId"]),
        Index(value = ["stockId"]),
        // optional: enforce "one stock per user" uniqueness
        Index(value = ["userId", "stockId"], unique = true)
    ]
)


data class WatchList(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,          // The unique ID of the watchlist item.
    val userId: Int,           // The ID of the user who owns the watchlist item.
    val name: String = "", // The name of the watchlist item.
    val stockId: Long, // The ID of the stock in the watchlist item.
    val createdAt: Long = System.currentTimeMillis(), // The timestamp when the watchlist item was created.
    val updatedAt: Long = System.currentTimeMillis() // The timestamp when the watchlist item was last updated.
)