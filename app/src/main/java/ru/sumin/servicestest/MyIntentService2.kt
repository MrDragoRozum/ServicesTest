package ru.sumin.servicestest

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.util.Log

class MyIntentService2 : IntentService(NAME) {

    override fun onCreate() {
        super.onCreate()
        log("onCreate()")
        setIntentRedelivery(true)
    }


    override fun onHandleIntent(intent: Intent?) {
        log("onHandleIntent()")
        val page = intent?.getIntExtra(PAGE, 0) ?: 0
        for (step in 0 until 5) {
            Thread.sleep(5000)
            log("onStartCommand steps: $step $page")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        log("onDestroy()")
    }

    private fun log(message: String) {
        Log.d("TAG_FOREGROUND_SERVICE", message)
    }

    companion object {
        fun newInstance(context: Context, page: Int) =
            Intent(context, MyIntentService2::class.java).apply {
                putExtra(PAGE, page)
            }
        private const val PAGE = "page"
        private const val NAME = "MyIntentService"
    }
}