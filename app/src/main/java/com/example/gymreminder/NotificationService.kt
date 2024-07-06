package com.example.gymreminder

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

class NotificationService(private val context: Context) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val CHANNEL_ID = "user_expiry_notification"
        const val CHANNEL_NAME = "User Expiry"
        const val PLAN_EXPIRY_REQUEST_CODE = 10
        const val PAYMENT_PENDING_REQUEST_CODE = 20
    }

    fun createAndVerifyChannel() {
        val notifChannel = notificationManager.getNotificationChannel(CHANNEL_ID)
        if(notifChannel == null) {
            val channel = NotificationChannel(CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "Notification contains information about user expiry dates"
            notificationManager.createNotificationChannel(channel)
        }
    }


    fun showPlanExpiryNotification(count: Int) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, PLAN_EXPIRY_REQUEST_CODE, intent, PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Plan expiry")
            .setContentText("$count - users plan pending for renewal this week")
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.dumbbell)
            .build()
        notificationManager.notify(1, notification)
    }

    fun showActiveButExpiryPassedNotification(count: Int) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, PAYMENT_PENDING_REQUEST_CODE, intent, PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.dumbbell)
            .setContentTitle("Payment Pending")
            .setContentText("Payment remaining for $count users")
            .setContentIntent(pendingIntent)
            .build()
        notificationManager.notify(2, notification)
    }
}