package com.example.loginsignup.data.api

import com.example.loginsignup.data.models.LastQuote
import com.example.loginsignup.data.models.NewsItem
import com.example.loginsignup.data.models.Profile
import com.example.loginsignup.data.models.newsResp
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

    @GET("news")
    suspend fun getNews(
        @Query("category") category: String = "general",
        @Query("token") token: String
    ): List<NewsItem>


    @GET("stock/profile2")
    suspend fun getProfile(
        @Query("symbol") symbol: String,
        @Query("token") token: String
    ): Profile

}