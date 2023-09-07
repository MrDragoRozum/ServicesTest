package ru.sumin.servicestest

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
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
            for (step in 0 until 100) {
                delay(500)
                log("onStartCommand steps: $step")
                jobFinished(params, true)
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
        Log.d("TAG_SERVICE", message)
    }

    companion object {
        fun newInstance(context: Context) = Intent(context, MyJobService::class.java)
    }
}