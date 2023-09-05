package ru.sumin.servicestest

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MyService : Service() {

    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        log("onCreate()")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        log("onStartCommand()")
        scope.launch {
            for(step in 0 until 1000) {
                delay(500)
                log("onStartCommand steps: $step")
            }
        }
        return super.onStartCommand(intent, flags, startId)
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
        Log.d("TAG_SERVICE", message)
    }

    companion object {
        fun newInstance(context: Context) = Intent(context, MyService::class.java)
    }
}