package com.example.gymreminder

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.gymreminder.MyApplication.Companion.TAG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotificationWorker(val context: Context, private val params: WorkerParameters) :
    CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val app = applicationContext as MyApplication
        val notificationService = app.notificationService

        val filterUsers = app.filterUser
        CoroutineScope(Dispatchers.IO).launch {
            launch {
                val count = filterUsers.filterActiveUserButExpiryPassed().count()
                if (count > 0) {
                    Log.d(TAG, "doWork: $count")
                    withContext(Dispatchers.Main) {
                        notificationService.showActiveButExpiryPassedNotification(count)
                    }
                }
            }
            launch {
                val count = filterUsers.filterUserBasedOn(7).count()
                if (count > 0) {
                    Log.d(TAG, "doWork: $count")
                    withContext(Dispatchers.Main) {
                        notificationService.showPlanExpiryNotification(count)
                    }
                }
            }
        }.join()
        Log.d(TAG, "doWork: succeed")
        return Result.success()
    }
}