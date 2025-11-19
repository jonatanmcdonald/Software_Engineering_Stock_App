package com.example.loginsignup.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alerts")
data class Alert(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val symbol: String,
    val userId: Int,
    val minPrice: Double,
    val maxPrice: Double,
    val currentPrice: Double,
    val createdAt: Long = System.currentTimeMillis(),
    val isTriggered: Boolean = false,
    val triggerParentID: Long,
    val runCondition: String
)

