package ru.sumin.servicestest

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java) as NotificationManager

        val notification = NotificationCompat.Builder(context, EXTRA_CHANNEL_ID)
            .setContentTitle("Сервис")
            .setContentText("Сервис работает в фоновом режиме!")
            .setSmallIcon(R.drawable.notification_bg)
            .build()

        createNotificationChannel(notificationManager)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                EXTRA_CHANNEL_ID,
                EXTRA_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }

    }

    companion object {
        fun newInstance(context: Context) = Intent(context, AlarmReceiver::class.java)
        private const val EXTRA_CHANNEL_ID = "channel_id"
        private const val EXTRA_CHANNEL_NAME = "Тест"
        private const val NOTIFICATION_ID = 1
    }
}