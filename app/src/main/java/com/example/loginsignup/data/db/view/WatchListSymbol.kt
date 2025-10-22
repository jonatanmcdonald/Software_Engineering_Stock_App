package com.example.loginsignup.data.db.view

import androidx.room.DatabaseView

@DatabaseView("""
  SELECT
    w.id, w.userId, w.stockId, w.name, w.note, w.createdAt, w.updatedAt,
    s.symbol
  FROM watchlist w
  JOIN stocks s ON s.id = w.stockId
""")
data class WatchListWithSymbol(
    val id: Long,
    val userId: String,
    val stockId: Long,
    val name: String,
    val note: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val symbol: String
)
