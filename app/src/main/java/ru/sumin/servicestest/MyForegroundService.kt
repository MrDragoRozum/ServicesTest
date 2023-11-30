package ru.sumin.servicestest

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
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

    private val notificationBuilder by lazy {
        notificationCompatBuilder()
    }

    private val notificationManager by lazy {
        getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    var progressListener: ((Int) -> Unit)? = null

    override fun onCreate() {
        super.onCreate()
        log("onCreate()")
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                EXTRA_CHANNEL_ID,
                EXTRA_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }

    }

    private fun notificationCompatBuilder() = NotificationCompat
        .Builder(this, EXTRA_CHANNEL_ID)
        .setContentTitle("Сервис")
        .setContentText("Сервис работает в фоновом режиме!")
        .setSmallIcon(R.drawable.notification_bg)
        .setProgress(MAX_PROGRESS, DEFAULT_START_PARAMETER, false)
        .setOnlyAlertOnce(true)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        log("onStartCommand()")
        scope.launch {
            for (progress in 0..100 step 5) {
                delay(500)
                log("onStartCommand steps: $progress")
                notificationManager.notify(NOTIFICATION_ID, notificationBuilder
                        .setProgress(MAX_PROGRESS, progress, false)
                        .build())
                progressListener?.invoke(progress)
            }
            stopSelf()
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder = LocalBind()

    inner class LocalBind : Binder() {
        fun getForegroundService() = this@MyForegroundService
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
        private const val MAX_PROGRESS = 100
        private const val DEFAULT_START_PARAMETER = 0
    }
}