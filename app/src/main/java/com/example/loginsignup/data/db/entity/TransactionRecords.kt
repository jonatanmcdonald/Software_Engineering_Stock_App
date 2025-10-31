package com.example.loginsignup.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
class TransactionRecords (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 1,
    val userId: Int = 1,
    val ticker: String,
    val type: String,  //Buy or Sell
    val shares: Double,
    val price: Double,
    val date: Long
    )




