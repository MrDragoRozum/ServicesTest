package ru.sumin.servicestest

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.app.job.JobWorkItem
import android.content.ComponentName
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import ru.sumin.servicestest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var page = 0

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
    }
}
