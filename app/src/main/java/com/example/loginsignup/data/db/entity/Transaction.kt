package com.example.loginsignup.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.Companion.CASCADE
        )
    ],
    indices = [Index(value = ["symbol"]), Index(value = ["userId"])])
class Transaction(
        @PrimaryKey(autoGenerate = true) val id: Long = 0L,
        val symbol: String,
        val userId: Int,
        val qty: Int,
        val price: Double,
        val side : String,
        val fees: Double,
        val timestamp: Long = System.currentTimeMillis()
)
