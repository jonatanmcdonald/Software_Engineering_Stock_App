package com.example.loginsignup.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "watchlist",
    indices = [Index(value = ["userId", "stock"], unique = true)]
)
data class WatchList(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,          // surrogate PK
    val userId: String,           // set from the logged-in user
    val name: String,
    val note: String? = null,
    val stock: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()

)