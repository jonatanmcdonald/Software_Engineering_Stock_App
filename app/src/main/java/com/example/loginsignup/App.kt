package com.example.loginsignup



import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager

import android.os.Build

import com.example.loginsignup.utils.GlobalRateLimiter

class App : Application() {
    lateinit var rateLimiter: GlobalRateLimiter
        private set

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        rateLimiter = GlobalRateLimiter(maxPerMinute = 60) // your real API cap
    }


    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                PriceNotificationService.PRICE_CHANNEL_ID,
                "Price",
                NotificationManager.IMPORTANCE_HIGH,
            )
            channel.description = "Used for watchlist price notifications"

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

        }
    }
}
