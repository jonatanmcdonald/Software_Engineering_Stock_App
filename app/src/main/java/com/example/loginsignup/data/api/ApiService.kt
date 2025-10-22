package com.example.loginsignup.data.api

import com.example.loginsignup.data.models.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("query")

    suspend fun searchSymbols(
        @Query("function") function: String = "TIME_SERIES_INTRADAY",
        @Query("symbol") symbol: String,
        @Query("interval") interval: String = "5min",
        @Query("apikey") apiKey: String
    ): ApiResponse
}