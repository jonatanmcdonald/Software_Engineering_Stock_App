package com.example.loginsignup.data.api

import com.example.loginsignup.data.models.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("query")

    suspend fun searchSymbols(
        @Query("function") function: String = "SYMBOL_SEARCH",
        @Query("keywords") keywords: String,
        @Query("apikey") apiKey: String
    ): SearchResponse
}