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
    n.content   AS content,
    n.timestamp AS timestamp
  FROM watchlist w
  JOIN stocks s ON s.id = w.stockId
  LEFT JOIN notes n ON n.watchlistId = w.id
  LEFT JOIN 
  (
    SELECT noteId,
    GROUP_CONCAT(uri, '|') AS mediaUris
    FROM note_media
    GROUP BY noteId
  ) m on m.noteId = n.id
""")
data class WatchListWithSymbol(
    val id: Long,
    val userId: Int,
    val stockId: Long,
    val name: String?,
    val noteId: Long?,      // nullable
    val content: String?,   // nullable
    val timestamp: Long?,
    val createdAt: Long,
    val updatedAt: Long,
    val ticker: String?,
    val mediaUris: String?
)


