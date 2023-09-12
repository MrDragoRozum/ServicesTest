package ru.sumin.servicestest

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService

class MyJobIntentService : JobIntentService() {

    override fun onCreate() {
        super.onCreate()
        log("onCreate()")
    }

    override fun onHandleWork(intent: Intent) {
        log("onHandleWork()")
        val page = intent.getIntExtra(PAGE, 0)
        for (step in 0 until 5) {
            Thread.sleep(500)
            log("onStartCommand steps: $step $page")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        log("onDestroy()")
    }

    private fun log(message: String) {
        Log.d("TAG_JOB_INTENT_SERVICE", message)
    }

    companion object {

        fun newEnqueue(context: Context, page: Int) {
            enqueueWork(
                context,
                MyJobIntentService::class.java,
                JOB_ID,
                newInstance(context, page)
            )
        }
        private fun newInstance(context: Context, page: Int) =
            Intent(context, MyJobIntentService::class.java).apply {
                putExtra(PAGE, page)
            }

        private const val PAGE = "page"
        private const val JOB_ID = 111
    }
}