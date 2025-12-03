package com.example.loginsignup

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat

class PriceNotificationService(
    private val context: Context
){
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    fun sendPriceNotification(price: Double, parent: String, ticker: String, alertText: String)
    {

        Log.d("PriceNotificationService", "Sending notif: $ticker @ $price, text=$alertText")
        val activityIntent = Intent(context, MainActivity::class.java)
        val activityPendingIntent = PendingIntent.getActivity(
            context,
            1,
            activityIntent,
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        )
        val notification = NotificationCompat.Builder(context, PRICE_CHANNEL_ID)
            .setSmallIcon(R.drawable.outline_price_change_24)
            .setContentTitle("Price Change Alert")
            .setContentText("Price for $ticker in your $parent has changed to $price. $alertText")
           // .setStyle(NotificationCompat.BigTextStyle())
            .setContentIntent(activityPendingIntent)
            .build()

        notificationManager.notify(1, notification)
    }
    companion object
    {
        const val PRICE_CHANNEL_ID = "price_channel"

    }
}