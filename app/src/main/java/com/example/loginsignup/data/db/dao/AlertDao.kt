package com.example.loginsignup.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

import com.example.loginsignup.data.db.entity.Alert

@Dao
interface AlertDao {

    @Insert
    suspend fun insertAlert(alert: Alert): Long

    @Query("Update alerts SET triggerPrice = :triggerPrice, runCondition = :runCondition WHERE triggerParent = :triggerParent AND userId = :userId AND symbol = :symbol")
    suspend fun updateAlert(triggerParent: String, userId: Int, symbol: String, triggerPrice: Double, runCondition: String): Int

    @Delete
    suspend fun deleteAlert(alert: Alert)

    @Query("SELECT * FROM alerts WHERE symbol = :symbol")
    suspend fun getAlertsBySymbol(symbol: String): Alert

    @Query("UPDATE alerts SET isActive = :isActive WHERE triggerParent = :parent AND userId = :userId AND symbol = :symbol")
    suspend fun toggleAlertActive(parent: String, userId: Int, symbol: String, isActive: Boolean)

    @Query("""
        SELECT * FROM alerts
        WHERE triggerParent = :triggerParent
        AND userId = :userId
        AND symbol = :symbol
    """)
    suspend fun getAlerts(userId: Int, symbol: String, triggerParent: String): Alert?

    @Query("UPDATE alerts SET triggerPrice = :newPrice WHERE id = :alertId")
    suspend fun updateAlertPrice(alertId: Long, newPrice: Double)
}
