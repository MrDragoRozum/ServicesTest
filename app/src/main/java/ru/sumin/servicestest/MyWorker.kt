package ru.sumin.servicestest

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf

class MyWorker(
    context: Context,
    private val parameters: WorkerParameters
) : Worker(context, parameters) {
    override fun doWork(): Result {
        log("doWork()")
        val page = parameters.inputData.getInt(PAGE, 0)
        for (step in 0 until 5) {
            Thread.sleep(500)
            log("doWork steps: $step $page")
        }
        return Result.success()
    }

    private fun log(message: String) {
        Log.d("TAG_WORKER", message)
    }

    companion object {
        const val PAGE = "page"
        const val NAME_WORK = "name work"

        fun makeRequest(page: Int) =
            OneTimeWorkRequestBuilder<MyWorker>()
                .setInputData(workDataOf(PAGE to page))
                .setConstraints(makeConstraints())
                .build()

        private fun makeConstraints() =
            Constraints.Builder().setRequiredNetworkType(NetworkType.UNMETERED).build()
    }
}