package com.example.loginsignup



import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager

import android.os.Build

import com.example.loginsignup.utils.GlobalRateLimiter

// This class is the main application class.
class App : Application() {
    lateinit var rateLimiter: GlobalRateLimiter // The global rate limiter for API calls.
        private set

    override fun onCreate() { // This function is called when the application is created.
        super.onCreate()
        createNotificationChannel() // Creates the notification channel for price notifications.
        rateLimiter = GlobalRateLimiter(maxPerMinute = 60) // Initializes the rate limiter.
    }


    // This function creates the notification channel for price notifications.
    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){ // Checks if the Android version is Oreo or higher.
            val channel = NotificationChannel( // Creates a new notification channel.
                PriceNotificationService.PRICE_CHANNEL_ID,
                "Price",
                NotificationManager.IMPORTANCE_HIGH,
            )
            channel.description = "Used for watchlist price notifications"

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager // Gets the notification manager.
            notificationManager.createNotificationChannel(channel) // Creates the notification channel.

        }
    }
}
