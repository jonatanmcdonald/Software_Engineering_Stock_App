package com.example.loginsignup.data.models

import com.google.gson.annotations.SerializedName

// This data class represents the response from the tickers API endpoint.
data class TickersResp (
    @SerializedName("results") // The list of ticker items.
    val res: List<TickerItem> = emptyList(),
    @SerializedName("next_url") // The URL for the next page of results.
    val next_url: String? = null,
    @SerializedName("count") // The total number of results.
    val count: Int,
)

// This data class represents the response from the news API endpoint.
data class newsResp(
    val res: List<NewsItem> = emptyList() // The list of news items.
)

// This data class represents a single news item.
data class NewsItem(
    @SerializedName("category") val category: String, // The category of the news item.
    @SerializedName("datetime") val datetime: Long, // The date and time of the news item.
    @SerializedName("headline") val headline: String, // The headline of the news item.
    @SerializedName("id") val id: Int, // The ID of the news item.
    @SerializedName("image") val image: String, // The URL of the image for the news item.
    @SerializedName("related") val related: String, // Related stock symbols.
    @SerializedName("source") val source: String, // The source of the news item.
    @SerializedName("summary") val summary: String, // The summary of the news item.
    @SerializedName("url") val url: String // The URL of the news item.
)


// This data class represents a single ticker item.
data class TickerItem(
    @SerializedName("ticker") val ticker: String, // The stock ticker.
    @SerializedName("name") val name: String, // The name of the company.
    @SerializedName("market") val market: String?, // The market where the stock is traded.
    @SerializedName("locale") val locale: String?, // The locale of the company.
    @SerializedName("primary_exchange") val primaryExchange: String?, // The primary exchange where the stock is traded.
    @SerializedName("type") val type: String?, // The type of the stock.
    @SerializedName("active") val active: Boolean?, // Whether the stock is actively traded.
    @SerializedName("currency_name") val currencyName: String?, // The name of the currency in which the stock is traded.

)

// This data class represents the last quote for a stock.
data class LastQuote(
    @SerializedName("c") // The closing price.
    val price: Double,
    @SerializedName("d") // The change in price.
    val change: Double?,
    @SerializedName("dp") // The percent change in price.
    val percentChange: Double?,
    @SerializedName("h") // The high price of the day.
    val hPrice: Double?,
    @SerializedName("l") // The low price of the day.
    val lPrice: Double?,
    @SerializedName("o") // The opening price.
    val oPrice: Double?,
    @SerializedName("pc") // The previous closing price.
    val pcPrice: Double?,
)

// This data class represents the profile of a company.
data class Profile(
    @SerializedName("country") // The country where the company is located.
    val country: String?,
    @SerializedName("currency") // The currency in which the company's stock is traded.
    val currency: String?,
    @SerializedName("exchange") // The stock exchange where the company is listed.
    val exchange: String?,
    @SerializedName("finnhubIndustry") // The industry of the company.
    val finnhubIndustry: String?,
    @SerializedName("ipo") // The IPO date of the company.
    val ipo: String?,
    @SerializedName("logo") // The URL of the company's logo.
    val logo: String?,
    @SerializedName("marketCapitalization") // The market capitalization of the company.
    val marketCapitalization: Double?,
    @SerializedName("name") // The name of the company.
    val name: String?,
    @SerializedName("phone") // The phone number of the company.
    val phone: String?,
    @SerializedName("shareOutstanding") // The number of outstanding shares of the company.
    val shareOutstanding: Double?,
    @SerializedName("ticker") // The stock ticker of the company.
    val ticker: String?,
    @SerializedName("weburl") // The website URL of the company.
    val weburl: String?
)
{}

