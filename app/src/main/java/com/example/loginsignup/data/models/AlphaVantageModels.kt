package com.example.loginsignup.data.models

import com.google.gson.annotations.SerializedName

data class ApiResponse (
    @SerializedName("Meta Data")
    val meta: MetaData?,

    @SerializedName("Time Series (5min)")
    val timeSeries5m: Map<String, CandleDto>?
)

data class MetaData(
    @SerializedName("1. Information") val information: String?,
    @SerializedName("2. Symbol")       val symbol: String?,
    @SerializedName("3. Last Refreshed") val lastRefreshed: String?,
    @SerializedName("4. Interval")     val interval: String?,
    @SerializedName("5. Output Size")  val outputSize: String?,
    @SerializedName("6. Time Zone")    val timeZone: String
)


data class CandleDto(
    @SerializedName("1. open")  val open: String?,
    @SerializedName("2. high")  val high: String?,
    @SerializedName("3. low")   val low: String?,
    @SerializedName("4. close") val close: String?,
    @SerializedName("5. volume") val volume: String?
)