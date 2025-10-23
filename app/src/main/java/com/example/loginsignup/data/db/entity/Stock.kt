package com.example.loginsignup.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "stocks", indices = [Index(value = ["ticker"], unique = true), Index(value = ["name"])])
class Stock (
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val ticker: String,
    val name: String,
    val market: String?,
    val locale: String?,
    val primaryExchange: String?,
    val type: String?,
    val currencyName: String?,
)