package ru.sumin.servicestest

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import ru.sumin.servicestest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.simpleService.setOnClickListener {
            startService(MyService.newInstance(this, 100))
        }
        binding.foregroundService.setOnClickListener {
            notification()
        }
    }

    private fun notification() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                EXTRA_CHANNEL_ID,
                EXTRA_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val notificationCompat = NotificationCompat.Builder(this, EXTRA_CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_bg)
            .setContentText("Сервис работает")
            .setContentTitle("Сервис")
            .build()
        notificationManager.notify(1, notificationCompat)
    }

    companion object {
        private const val EXTRA_CHANNEL_ID = "channel_id"
        private const val EXTRA_CHANNEL_NAME = "Тест"
    }
}
