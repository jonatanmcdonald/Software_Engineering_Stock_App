package com.example.loginsignup.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// This class represents a transaction record in the database.
@Entity(tableName = "transactions")
class TransactionRecords (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 1, // The unique ID of the transaction record.
    val userId: Int = 1, // The ID of the user who made the transaction.
    val ticker: String, // The stock ticker of the transaction.
    val type: String,  // The type of the transaction (e.g., "Buy" or "Sell").
    val shares: Double, // The number of shares in the transaction.
    val price: Double, // The price per share in the transaction.
    val date: Long // The date of the transaction.
    )




