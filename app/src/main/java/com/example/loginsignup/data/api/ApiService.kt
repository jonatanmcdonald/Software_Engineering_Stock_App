package com.example.loginsignup.data.api

import com.example.loginsignup.data.models.LastQuote
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    /*
    @GET("/v3/reference/tickers")

    suspend fun getAllUsStocks(
        @Query("market") market: String = "stocks",
        @Query("active") active: Boolean = true,
        @Query("order") order: String = "asc",
        @Query("limit") limit: Int = 1000,
        @Query("sort") sort: String = "ticker",
        @Query("apikey") apiKey: String
    ): TickersResp

    @GET
    suspend fun pageByNextUrl(
        @Url nextUrl: String,      // full next_url from previous response
        @Query("apiKey") apiKey: String
    ): TickersResp
    */

    @GET("quote")
    suspend fun getLastQuote(
        @Query("symbol") symbol: String,
        @Query("token") token: String
    ): LastQuote

}