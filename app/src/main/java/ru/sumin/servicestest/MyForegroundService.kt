package ru.sumin.servicestest

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MyForegroundService : Service() {

    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        log("onCreate()")
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotificationCompat())
    }

    private fun createNotificationChannel() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                EXTRA_CHANNEL_ID,
                EXTRA_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }

    }

    private fun createNotificationCompat() = NotificationCompat
        .Builder(this, EXTRA_CHANNEL_ID)
        .setContentTitle("Сервис")
        .setContentText("Сервис работает в фоновом режиме!")
        .setSmallIcon(R.drawable.notification_bg)
        .build()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        log("onStartCommand()")
        scope.launch {
            for (step in 0 until 100) {
                delay(500)
                log("onStartCommand steps: $step")
            }
        }
        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        super.onDestroy()
        log("onDestroy()")
        scope.cancel()
    }

    private fun log(message: String) {
        Log.d("TAG_FOREGROUND_SERVICE", message)
    }

    companion object {
        fun newInstance(context: Context) = Intent(context, MyForegroundService::class.java)
        private const val EXTRA_CHANNEL_ID = "channel_id"
        private const val EXTRA_CHANNEL_NAME = "Тест"
        private const val NOTIFICATION_ID = 1
    }
}