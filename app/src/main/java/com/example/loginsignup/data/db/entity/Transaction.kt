package com.example.loginsignup.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// This class represents a transaction in the database.
@Entity(tableName = "transactions", // The name of the table in the database.
    foreignKeys = [ // The foreign keys for the table.
        ForeignKey( // A foreign key to the User entity.
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.Companion.CASCADE
        )
    ],
    indices = [Index(value = ["symbol"]), Index(value = ["userId"])]) // The indices for the table.
class Transaction(
        @PrimaryKey(autoGenerate = true) val id: Long = 0L, // The unique ID of the transaction.
        val symbol: String, // The stock symbol of the transaction.
        val userId: Int, // The ID of the user who made the transaction.
        val qty: Int, // The quantity of the transaction.
        val price: Double, // The price of the transaction.
        val side : String, // The side of the transaction (e.g., "BUY", "SELL").
        val fees: Double, // The fees associated with the transaction.
        val timestamp: Long = System.currentTimeMillis() // The timestamp when the transaction was made.
)
