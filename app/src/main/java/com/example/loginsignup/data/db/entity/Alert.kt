package com.example.loginsignup.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "alerts",
        foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
],
    indices = [Index(value = ["triggerParent", "symbol", "userId"], unique = true)]
)
data class Alert(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val symbol: String,
    val userId: Int,
    val triggerPrice: Double,
    val createdAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = false,
    val triggerParent: String = "Portfolio",
    val runCondition: String
)

