package com.example.gymreminder

import android.app.Application

class MyApplication: Application() {

    companion object {
        const val TAG = "GymApp"
    }

    override fun onCreate() {
        super.onCreate()
        val notificationService = NotificationService(this)
        notificationService.createAndVerifyChannel()
    }
}