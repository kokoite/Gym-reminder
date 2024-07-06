package com.example.gymreminder

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationWorker(val context: Context, private val params: WorkerParameters): Worker(context, params) {
    override fun doWork(): Result {
        val app = applicationContext as MyApplication
        val notificationService = app.notificationService
        val filterUsers = app.filterUser
        CoroutineScope(Dispatchers.IO). launch {
            launch {
                val count = filterUsers.filterActiveUserButExpiryPassed().count()
                if(count > 0) {
                    notificationService.showActiveButExpiryPassedNotification(count)
                }
            }

            launch {
                val count = filterUsers.filterUserBasedOn(7).count()
                if(count > 0) {
                    notificationService.showPlanExpiryNotification(count)
                }
            }
        }
        return Result.success()
    }
}