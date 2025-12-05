package com.example.loginsignup.data.db.view

import androidx.room.DatabaseView

// This class represents a database view that combines data from the watchlist, stocks, and notes tables.
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
    val id: Long, // The ID of the watchlist item.
    val userId: Int, // The ID of the user who owns the watchlist item.
    val stockId: Long, // The ID of the stock in the watchlist item.
    val name: String?, // The name of the watchlist item.
    val noteId: Long?,      // The ID of the note associated with the watchlist item.
    val content: String?,   // The content of the note associated with the watchlist item.
    val timestamp: Long?, // The timestamp of the note associated with the watchlist item.
    val createdAt: Long, // The timestamp when the watchlist item was created.
    val updatedAt: Long, // The timestamp when the watchlist item was last updated.
    val ticker: String?, // The ticker symbol of the stock in the watchlist item.
    val mediaUris: String? // A string of media URIs associated with the note.
)


