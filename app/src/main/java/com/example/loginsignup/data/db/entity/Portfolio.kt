package com.example.loginsignup.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "portfolio",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["symbol"]), Index(value = ["userId", "symbol"], unique = true)])
class Portfolio (
        @PrimaryKey(autoGenerate = true)
        val id: Long = 0L,
        val symbol: String,
        val userId: Int,
        val cost_basis: Double,
        val avg_cost: Double,
        val realized_pnl: Double,
        val qty: Double
)
