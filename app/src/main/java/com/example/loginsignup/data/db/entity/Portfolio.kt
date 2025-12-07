package com.example.loginsignup.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// This class represents a portfolio item in the database.
@Entity(tableName = "portfolio", // The name of the table in the database.
    foreignKeys = [ // The foreign keys for the table.
        ForeignKey( // A foreign key to the User entity.
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["symbol"]), Index(value = ["userId", "symbol"], unique = true)]) // The indices for the table.
class Portfolio (
        @PrimaryKey(autoGenerate = true) // The primary key for the table.
        val id: Long = 0L, // The unique ID of the portfolio item.
        val symbol: String, // The stock symbol of the portfolio item.
        val userId: Int, // The ID of the user who owns the portfolio item.
        val cost_basis: Double, // The cost basis of the portfolio item.
        val avg_cost: Double, // The average cost of the portfolio item.
        val realized_pnl: Double, // The realized profit and loss of the portfolio item.
        val qty: Double // The quantity of the portfolio item.
)
