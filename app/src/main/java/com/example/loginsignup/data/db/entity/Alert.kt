package com.example.loginsignup.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// This class represents an alert in the database.
@Entity(tableName = "alerts",
        foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
],
    indices = [Index(value = ["triggerParent", "symbol", "userId"], unique = true), Index("userId")]
)
data class Alert(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0, // The unique ID of the alert.
    val symbol: String, // The stock symbol for the alert.
    val userId: Int, // The ID of the user who created the alert.
    val triggerPrice: Double, // The price at which the alert will be triggered.
    val createdAt: Long = System.currentTimeMillis(), // The timestamp when the alert was created.
    val isActive: Boolean = false, // Whether the alert is currently active.
    val triggerParent: String = "Portfolio", // The parent of the alert (e.g., "Portfolio" or "Watchlist").
    val runCondition: String // The condition for triggering the alert (e.g., "LESS_THAN", "GREATER_THAN").
)

