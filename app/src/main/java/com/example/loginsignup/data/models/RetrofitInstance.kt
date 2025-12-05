package com.example.loginsignup.data.models
// Make sure this is imported
import com.example.loginsignup.BuildConfig
import com.example.loginsignup.data.api.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


private const val BASE_URL = "https://finnhub.io/api/v1/"

private const val API_KEY = BuildConfig.FINN_API_KEY

// This object provides a singleton instance of the Retrofit API service.
object RetrofitInstance {
    // A lazy-initialized property that creates the Retrofit API service.
    val api: ApiService by lazy {
        // 1. --- CREATE THE LOGGING INTERCEPTOR ---
        // This is the key to debugging. It will log all network activity.
       //val logging = HttpLoggingInterceptor()
        //logging.setLevel(HttpLogging.Interceptor.Level.BODY) // Use .BODY to see the full request and response

        // 2. --- CREATE THE OkHttp CLIENT AND ADD THE INTERCEPTOR ---
        //val client = OkHttpClient.Builder()
            //.addInterceptor(logging) // Add the logger as an interceptor
            //.build()


        Retrofit.Builder() // Creates a new Retrofit builder.
            .baseUrl(BASE_URL) // Sets the base URL for the API.
            .addConverterFactory(GsonConverterFactory.create()) // Adds a converter factory for serializing and deserializing data.
           // .client(client)
            .build() // Builds the Retrofit instance.
            .create(ApiService::class.java) // Creates an implementation of the API service.
    }

    // This function returns the API key.
    fun getApiKey(): String  = API_KEY
}