package com.example.loginsignup.data.models

import com.example.loginsignup.data.db.entity.PriceToday
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val MARKET_ZONE = ZoneId.of("US/Eastern")
private val FEED_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.US)

fun slotFromEasternString(ts: String): Int {
    val ldt = LocalDateTime.parse(ts, FEED_FMT)           // ts is in Eastern per meta
    val minutes = ldt.atZone(MARKET_ZONE).toLocalTime().toSecondOfDay() / 60
    return minutes / 5
}

fun ApiResponse.toTodayRows(
    stockId: Long,
): List<PriceToday> {
    val series = timeSeries5m ?: return emptyList()

    return series.entries
        .asSequence()
        .map { (ts, c) ->
            PriceToday(
                stockId = stockId,
                slot5m = slotFromEasternString(ts),
                open = c.open?.toDoubleOrNull(),
                high = c.high?.toDoubleOrNull(),
                low = c.low?.toDoubleOrNull(),
                close = c.close?.toDoubleOrNull(),
                volume = c.volume?.toLongOrNull()
            )
        }
        .sortedBy { it.slot5m }
        .toList()
}