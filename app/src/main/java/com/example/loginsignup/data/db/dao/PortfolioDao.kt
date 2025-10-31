package com.example.loginsignup.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.loginsignup.data.db.entity.Portfolio
import kotlinx.coroutines.flow.Flow

@Dao
interface PortfolioDao {
    @Query("SELECT * FROM portfolio where userId = :id")
     fun getUserPortfolio(id: Int): Flow<List<Portfolio>>
}