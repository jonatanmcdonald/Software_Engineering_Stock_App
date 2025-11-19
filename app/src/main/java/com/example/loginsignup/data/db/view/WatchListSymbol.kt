package com.example.loginsignup.data.db.view

import androidx.room.DatabaseView

@DatabaseView("""
  SELECT
    w.id,
    w.userId,
    w.stockId,
    w.name,
    w.createdAt,
    w.updatedAt,
    s.ticker AS ticker,
    n.id        AS noteId,
    n.imageUrl  AS imageUrl,
    n.content   AS content,
    n.timestamp AS timestamp
  FROM watchlist w
  JOIN stocks s ON s.id = w.stockId
  LEFT JOIN notes n ON n.watchlistId = w.id
""")
data class WatchListWithSymbol(
    val id: Long,
    val userId: Int,
    val stockId: Long,
    val name: String?,
    val noteId: Long?,      // nullable
    val imageUrl: String?,  // nullable
    val content: String?,   // nullable
    val timestamp: Long?,   // 🔴 make this nullable if note is optional
    val createdAt: Long,
    val updatedAt: Long,
    val ticker: String?
)


