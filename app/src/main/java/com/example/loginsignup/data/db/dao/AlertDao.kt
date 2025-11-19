package com.example.loginsignup.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.loginsignup.data.db.entity.Alert

@Dao
interface AlertDao {

    @Insert
    suspend fun insertAlert(alert: Alert): Long

    @Update
    suspend fun updateAlert(alert: Alert)

    @Delete
    suspend fun deleteAlert(alert: Alert)

    @Query("SELECT * FROM alerts WHERE symbol = :symbol")
    suspend fun getAlertsBySymbol(symbol: String): Alert

    @Query("""
        SELECT * FROM alerts
        WHERE isTriggered = 0
        AND triggerParentId = :triggerParentId
        AND userId = :userId
        AND symbol = :symbol
    """)
    suspend fun getTriggeredAlerts(userId: Int, symbol: String, triggerParentId: Long): Alert

    @Query("UPDATE alerts SET currentPrice = :newPrice WHERE id = :alertId")
    suspend fun updateAlertPrice(alertId: Long, newPrice: Double)
}
