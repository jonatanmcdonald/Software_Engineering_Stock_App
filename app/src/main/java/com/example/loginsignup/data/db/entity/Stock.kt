package com.example.loginsignup.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// This class represents a stock in the database.
@Entity(tableName = "stocks", indices = [Index(value = ["ticker"], unique = true), Index(value = ["name"])])
class Stock (
    @PrimaryKey(autoGenerate = true) val id: Long = 0L, // The unique ID of the stock.
    val ticker: String, // The ticker symbol of the stock.
    val name: String, // The name of the stock.
    val market: String?, // The market where the stock is traded.
    val locale: String?, // The locale of the stock.
    val primaryExchange: String?, // The primary exchange where the stock is traded.
    val type: String?, // The type of the stock.
    val currencyName: String?, // The name of the currency in which the stock is traded.
)