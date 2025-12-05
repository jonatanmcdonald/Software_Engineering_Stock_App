package com.example.loginsignup.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

import com.example.loginsignup.data.db.entity.Alert

// This interface defines the Data Access Object (DAO) for the Alert entity.
@Dao
interface AlertDao {

    // This function inserts an alert into the database.
    @Insert
    suspend fun insertAlert(alert: Alert): Long

    // This function updates an existing alert in the database.
    @Query("Update alerts SET triggerPrice = :triggerPrice, runCondition = :runCondition WHERE triggerParent = :triggerParent AND userId = :userId AND symbol = :symbol")
    suspend fun updateAlert(triggerParent: String, userId: Int, symbol: String, triggerPrice: Double, runCondition: String): Int

    // This function deletes an alert from the database.
    @Delete
    suspend fun deleteAlert(alert: Alert)

    // This function retrieves an alert from the database by its symbol.
    @Query("SELECT * FROM alerts WHERE symbol = :symbol")
    suspend fun getAlertsBySymbol(symbol: String): Alert

    // This function toggles the active state of an alert.
    @Query("UPDATE alerts SET isActive = :isActive WHERE triggerParent = :parent AND userId = :userId AND symbol = :symbol")
    suspend fun toggleAlertActive(parent: String, userId: Int, symbol: String, isActive: Boolean)

    // This function retrieves a specific alert from the database.
    @Query("""
        SELECT * FROM alerts
        WHERE triggerParent = :triggerParent
        AND userId = :userId
        AND symbol = :symbol
    """)
    suspend fun getAlerts(userId: Int, symbol: String, triggerParent: String): Alert?

    // This function updates the trigger price of an alert.
    @Query("UPDATE alerts SET triggerPrice = :newPrice WHERE id = :alertId")
    suspend fun updateAlertPrice(alertId: Long, newPrice: Double)
}
