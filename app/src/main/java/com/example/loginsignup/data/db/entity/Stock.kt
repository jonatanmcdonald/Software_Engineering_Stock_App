package com.example.loginsignup.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "stocks", indices = [Index(value = ["symbol"], unique = true), Index(value = ["name"])])
class Stock (
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val symbol: String
)