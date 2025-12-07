package com.example.loginsignup

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat

// This class provides a service for sending price notifications.
class PriceNotificationService(
    private val context: Context // The context of the application.
){
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager // The notification manager for sending notifications.
    // This function sends a price notification.
    fun sendPriceNotification(price: Double, parent: String, ticker: String, alertText: String)
    {

        Log.d("PriceNotificationService", "Sending notif: $ticker @ $price, text=$alertText")
        val activityIntent = Intent(context, MainActivity::class.java) // The intent to launch when the notification is clicked.
        val activityPendingIntent = PendingIntent.getActivity( // The pending intent for the activity.
            context,
            1,
            activityIntent,
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        )
        val notification = NotificationCompat.Builder(context, PRICE_CHANNEL_ID) // Creates a new notification builder.
            .setSmallIcon(R.drawable.outline_price_change_24) // Sets the small icon for the notification.
            .setContentTitle("Price Change Alert") // Sets the title of the notification.
            .setContentText("Price for $ticker in your $parent has changed to $price. $alertText") // Sets the text of the notification.
           // .setStyle(NotificationCompat.BigTextStyle())
            .setContentIntent(activityPendingIntent) // Sets the pending intent for the notification.
            .build() // Builds the notification.

        notificationManager.notify(1, notification) // Sends the notification.
    }
    companion object // A companion object to hold constants.
    {
        const val PRICE_CHANNEL_ID = "price_channel" // The ID of the price notification channel.

    }
}