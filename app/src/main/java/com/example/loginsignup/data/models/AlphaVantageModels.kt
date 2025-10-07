package com.example.loginsignup.data.models

import com.google.gson.annotations.SerializedName

data class SearchResponse (
    @SerializedName("bestMatches")
    val bestMatches: List<Stocks>?
)

data class Stocks (
    @SerializedName("1. symbol") val symbol: String,
    @SerializedName ("2. name") val name: String
)