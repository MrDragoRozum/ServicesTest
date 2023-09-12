package ru.sumin.servicestest

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.os.Build
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MyJobService : JobService() {

    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        log("onCreate()")
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        log("onStartJob()")
        scope.launch {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                var workItem = params?.dequeueWork()
                while (workItem != null) {
                    val page = workItem.intent.getIntExtra(PAGE, 0)
                    for (step in 0 until 5) {
                        delay(500)
                        log("onStartCommand steps: $step $page")
                    }
                    params?.completeWork(workItem)
                    workItem = params?.dequeueWork()
                }
                jobFinished(params, false)
            }
        }
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        log("onStopJob()")
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        log("onDestroy()")
        scope.cancel()
    }

    private fun log(message: String) {
        Log.d("TAG_JOB_SERVICE", message)
    }

    companion object {
        const val JOB_ID = 123
        private const val PAGE = "page"
        fun newInstance(page: Int) = Intent().apply {
            putExtra(PAGE, page)
        }
    }
}