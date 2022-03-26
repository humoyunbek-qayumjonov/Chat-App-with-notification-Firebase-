package com.example.chatapp.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.chatapp.MainActivity
import com.example.chatapp.R
import com.example.chatapp.RealActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService:FirebaseMessagingService() {
    @RequiresApi(Build.VERSION_CODES.O)

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")

        Log.d(TAG, "onMessageReceived: ${remoteMessage.notification?.title}")
        Log.d(TAG, "onMessageReceived: ${remoteMessage.notification?.body}")
        Log.d(TAG, "onMessageReceived: ${remoteMessage.data}")

        val intent = Intent(this,RealActivity::class.java)
        val pendingINtent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT)


            var builder = NotificationCompat.Builder(this,"1")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(remoteMessage.data["title"])
                .setContentText(remoteMessage.data["body"])
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingINtent)
                .setAutoCancel(true)

            val notificationManager:NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                val name = getString(R.string.app_name)
                val descriptiontext = getString(R.string.app_name)
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel("1",name,importance).apply {
                    description = descriptiontext
                }
                notificationManager.createNotificationChannel(channel)
            }

            notificationManager.notify(1,builder.build())


            Log.d(TAG, "Message Notification Body: ${remoteMessage.data["body"]}")


    }

}