package ru.sumin.servicestest

import android.app.IntentService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel

class MyIntentService : IntentService(NAME) {

    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        log("onCreate()")
        setIntentRedelivery(false)
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotificationCompat())
    }

    private fun createNotificationChannel() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                EXTRA_CHANNEL_ID,
                EXTRA_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }

    }

    override fun onHandleIntent(p0: Intent?) {
        log("onHandleIntent()")
        for (step in 0 until 3) {
            Thread.sleep(5000)
            log("onStartCommand steps: $step")
        }
    }

    private fun createNotificationCompat() = NotificationCompat
        .Builder(this, EXTRA_CHANNEL_ID)
        .setContentTitle("Сервис")
        .setContentText("Сервис (IntentService) работает в фоновом режиме!")
        .setSmallIcon(R.drawable.notification_bg)
        .build()

    override fun onDestroy() {
        super.onDestroy()
        log("onDestroy()")
        scope.cancel()
    }

    private fun log(message: String) {
        Log.d("TAG_FOREGROUND_SERVICE", message)
    }

    companion object {
        fun newInstance(context: Context) = Intent(context, MyIntentService::class.java)
        private const val EXTRA_CHANNEL_ID = "channel_id"
        private const val EXTRA_CHANNEL_NAME = "Тест"
        private const val NOTIFICATION_ID = 1
        private const val NAME = "MyIntentService"
    }
}