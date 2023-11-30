package ru.sumin.servicestest

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.app.job.JobWorkItem
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import ru.sumin.servicestest.databinding.ActivityMainBinding
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var page = 0

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = (service as? MyForegroundService.LocalBind) ?: return
            val foregroundService = binder.getForegroundService()
            foregroundService.progressListener = {
                binding.progressBarLoading.progress = it
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d("onServiceDisconnected", "Disconnected: ${name?.className}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.simpleService.setOnClickListener {
            stopService(MyForegroundService.newInstance(this))
            startService(MyService.newInstance(this, 100))
        }
        binding.foregroundService.setOnClickListener {
            ContextCompat.startForegroundService(this, MyForegroundService.newInstance(this))
        }
        binding.intentService.setOnClickListener {
            ContextCompat.startForegroundService(this, MyIntentService.newInstance(this))
        }
        binding.jobScheduler.setOnClickListener {
            val componentName = ComponentName(this, MyJobService::class.java)

            val jobInfo = JobInfo.Builder(MyJobService.JOB_ID, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .build()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val scheduler = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
                scheduler.enqueue(jobInfo, JobWorkItem(MyJobService.newInstance(page++)))
            } else {
                startService(MyIntentService2.newInstance(this, page++))
            }
        }
        binding.jobIntentService.setOnClickListener {
            MyJobIntentService.newEnqueue(this, page++)
        }
        binding.workManager.setOnClickListener {
            WorkManager.getInstance(applicationContext)
                .enqueueUniqueWork(
                    MyWorker.NAME_WORK,
                    ExistingWorkPolicy.APPEND,
                    MyWorker.makeRequest(page++)
                )
        }
        binding.alarmManager.setOnClickListener {
            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            val intent = AlarmReceiver.newInstance(this)
            val pendingIntent = PendingIntent.getBroadcast(
                this,
                -1,
                intent,
                0
            )
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.SECOND, 30)
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }

    override fun onStart() {
        super.onStart()
        bindService(
            MyForegroundService.newInstance(this),
            serviceConnection,
            0
        )
    }

    override fun onStop() {
        super.onStop()
        unbindService(serviceConnection)
    }
}
