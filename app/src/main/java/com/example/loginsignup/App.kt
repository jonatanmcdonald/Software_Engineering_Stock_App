package com.example.loginsignup


import android.app.Application
import com.example.loginsignup.utils.GlobalRateLimiter

class App : Application() {
    lateinit var rateLimiter: GlobalRateLimiter
        private set

    override fun onCreate() {
        super.onCreate()
        rateLimiter = GlobalRateLimiter(maxPerMinute = 60) // your real API cap
    }
}
