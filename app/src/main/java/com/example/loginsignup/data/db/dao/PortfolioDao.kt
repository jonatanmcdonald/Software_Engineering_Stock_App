package com.example.loginsignup.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import com.example.loginsignup.data.db.entity.Portfolio
import kotlinx.coroutines.flow.Flow

@Dao
interface PortfolioDao {
    @Query("SELECT * FROM portfolio where userId = :id")
     fun getUserPortfolio(id: Int): Flow<List<Portfolio>>

     @Query("SELECT * FROM portfolio WHERE id = :id")
     suspend fun getPortfolioById(id: Long): Portfolio?

     @Query("UPDATE portfolio SET qty = :newQty WHERE id = :id")
     suspend fun updateQty(id:Long, newQty: Double)

     @Delete
     suspend fun delete(portfolio: Portfolio)

}