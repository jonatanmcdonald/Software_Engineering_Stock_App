package com.example.loginsignup.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import com.example.loginsignup.data.db.entity.Portfolio
import kotlinx.coroutines.flow.Flow

// This interface defines the Data Access Object (DAO) for the Portfolio entity.
@Dao
interface PortfolioDao {
    // This function retrieves the portfolio for a specific user.
    @Query("SELECT * FROM portfolio where userId = :id")
     fun getUserPortfolio(id: Int): Flow<List<Portfolio>>

    // This function retrieves a portfolio item by its ID.
     @Query("SELECT * FROM portfolio WHERE id = :id")
     suspend fun getPortfolioById(id: Long): Portfolio?

    // This function updates the quantity of a portfolio item.
     @Query("UPDATE portfolio SET qty = :newQty WHERE id = :id")
     suspend fun updateQty(id:Long, newQty: Double)

    // This function deletes a portfolio item from the database.
     @Delete
     suspend fun delete(portfolio: Portfolio)

}