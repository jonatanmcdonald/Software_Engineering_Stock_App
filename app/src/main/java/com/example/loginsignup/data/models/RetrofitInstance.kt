package com.example.loginsignup.data.models
// Make sure this is imported
import com.example.loginsignup.BuildConfig
import com.example.loginsignup.data.api.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


private const val BASE_URL = "https://finnhub.io/api/v1/"

private const val API_KEY = BuildConfig.FINN_API_KEY

object RetrofitInstance {
    val api: ApiService by lazy {
        // 1. --- CREATE THE LOGGING INTERCEPTOR ---
        // This is the key to debugging. It will log all network activity.
       //val logging = HttpLoggingInterceptor()
        //logging.setLevel(HttpLoggingInterceptor.Level.BODY) // Use .BODY to see the full request and response

        // 2. --- CREATE THE OkHttp CLIENT AND ADD THE INTERCEPTOR ---
        //val client = OkHttpClient.Builder()
            //.addInterceptor(logging) // Add the logger as an interceptor
            //.build()


        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
           // .client(client)
            .build()
            .create(ApiService::class.java)
    }

    fun getApiKey(): String  = API_KEY
}