package com.example.loginsignup.data.models

import com.google.gson.annotations.SerializedName

data class TickersResp (
    @SerializedName("results")
    val res: List<TickerItem> = emptyList(),
    @SerializedName("next_url")
    val next_url: String? = null,
    @SerializedName("count")
    val count: Int,
)

data class TickerItem(
    @SerializedName("ticker") val ticker: String,
    @SerializedName("name") val name: String,
    @SerializedName("market") val market: String?,
    @SerializedName("locale") val locale: String?,
    @SerializedName("primary_exchange") val primaryExchange: String?,
    @SerializedName("type") val type: String?,
    @SerializedName("active") val active: Boolean?,
    @SerializedName("currency_name") val currencyName: String?,

)

data class LastQuote(
    @SerializedName("c")
    val price: Double,
    @SerializedName("d")
    val change: Double?,
    @SerializedName("dp")
    val percentChange: Double?,
    @SerializedName("h")
    val hPrice: Double?,
    @SerializedName("l")
    val lPrice: Double?,
    @SerializedName("o")
    val oPrice: Double?,
    @SerializedName("pc")
    val pcPrice: Double?,
)

data class Profile(
    @SerializedName("country")
    val country: String?,
    @SerializedName("currency")
    val currency: String?,
    @SerializedName("exchange")
    val exchange: String?,
    @SerializedName("finnhubIndustry")
    val finnhubIndustry: String?,
    @SerializedName("ipo")
    val ipo: String?,
    @SerializedName("logo")
    val logo: String?,
    @SerializedName("marketCapitalization")
    val marketCapitalization: Double?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("phone")
    val phone: String?,
    @SerializedName("shareOutstanding")
    val shareOutstanding: Double?,
    @SerializedName("ticker")
    val ticker: String?,
    @SerializedName("weburl")
    val weburl: String?
)
{}

