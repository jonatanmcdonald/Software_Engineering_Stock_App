package com.example.loginsignup.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "prices_today",
    primaryKeys = ["stockId", "slot5m"],
    foreignKeys = [
        ForeignKey(
            entity = Stock::class,
            parentColumns = ["id"],
            childColumns = ["stockId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("stockId"),
        Index("slot5m")
    ]
)
data class PriceToday(
    val stockId: Long,      // FK → stocks.id
    val slot5m: Int,        // 0..287 in market tz
    val open: Double?, val high: Double?, val low: Double?,
    val close: Double?, val volume: Long?
)
