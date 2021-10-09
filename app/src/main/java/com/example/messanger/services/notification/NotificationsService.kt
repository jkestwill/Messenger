package com.example.messanger.services.notification


import android.app.*
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.messanger.App
import com.example.messanger.R
import com.example.messanger.repository.AuthRepository
import com.example.messanger.ui.screens.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import javax.inject.Inject

class NotificationsService : FirebaseMessagingService() {
    companion object{
        const val CHANNEL_ID="message_notification"
        const val CHANNEL_NAME="Messages"
        const val NOTIFICATION_ID=0
    }


    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }





    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.e("eeeee", "onMessageReceived: ")
        sendNotification(message)

    }

    override fun onMessageSent(p0: String) {
        super.onMessageSent(p0)
        Log.e("suka", "onMessageSent: ")
    }



    private fun sendNotification(message: RemoteMessage) {
        val intent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        Log.e("sisi", "sendNotification: ${message.messageId} ")
        val channel=NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
        val notification=Notification.Builder(this, CHANNEL_ID)
            .setContentTitle(message.notification!!.title)
            .setContentText(message.notification!!.body)
            .setSmallIcon(R.drawable.ic_message)
            .setContentIntent(intent)
            .setStyle(Notification.BigTextStyle())
            .setAutoCancel(true)
            .build()

        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
        notificationManager.notify(message.notification!!.tag, NOTIFICATION_ID,notification)


    }


    override fun onCreate() {
        super.onCreate()
    }


}