package com.example.cs446_fit4me.chat

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.cs446_fit4me.R
import android.app.PendingIntent
import android.content.Intent
import android.util.Log

object ChatNotificationHelper {
    private const val CHANNEL_ID = "chat_messages"

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "Chat Messages", NotificationManager.IMPORTANCE_HIGH
            )
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
            Log.d("CHAT_DEBUG", "Notification channel created: $CHANNEL_ID")
        }
    }

    fun showChatNotification(context: Context, senderName: String, message: String, peerUserId: String) {
        try {
            Log.d("CHAT_DEBUG", "Preparing notification for $senderName: $message")

            val intent = Intent(context, com.example.cs446_fit4me.MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("open_chat_peer_id", peerUserId)
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                peerUserId.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.applogo_weights)
                .setContentTitle(senderName)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)

            NotificationManagerCompat.from(context).notify(System.currentTimeMillis().toInt(), builder.build())
            Log.d("CHAT_DEBUG", "Notification shown for $senderName")
        } catch (e: SecurityException) {
            Log.e("CHAT_DEBUG", "SecurityException while showing notification: ${e.message}")
            e.printStackTrace()
        } catch (e: Exception) {
            Log.e("CHAT_DEBUG", "Error while showing notification: ${e.message}")
            e.printStackTrace()
        }
    }
}
