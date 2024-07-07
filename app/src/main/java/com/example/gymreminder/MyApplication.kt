package com.example.gymreminder

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationManager
import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.gymreminder.data.UserDao
import com.example.gymreminder.data.UserDatabase
import com.example.gymreminder.repository.UserDataSource
import com.example.gymreminder.repository.UserDataSourceImpl
import com.example.gymreminder.repository.UserRemoteDataSource
import com.example.gymreminder.repository.UserRemoteDataSourceImpl
import com.example.gymreminder.repository.UserRepository
import com.example.gymreminder.repository.UserRepositoryImpl
import com.example.gymreminder.usecase.CreateUser
import com.example.gymreminder.usecase.CreateUserImpl
import com.example.gymreminder.usecase.DeleteUser
import com.example.gymreminder.usecase.DeleteUserImpl
import com.example.gymreminder.usecase.EditUser
import com.example.gymreminder.usecase.EditUserImpl
import com.example.gymreminder.usecase.FetchAllUser
import com.example.gymreminder.usecase.FetchAllUserImpl
import com.example.gymreminder.usecase.FetchUser
import com.example.gymreminder.usecase.FetchUsersImpl
import com.example.gymreminder.usecase.FilterUsers
import com.example.gymreminder.usecase.FilterUsersImpl
import java.time.Duration
import java.util.Calendar
import java.util.concurrent.TimeUnit

class MyApplication: Application() {

    companion object {
        const val TAG = "GymApp"
    }

    lateinit var filterUser: FilterUsers
    lateinit var createUser: CreateUser
    lateinit var deleteUser: DeleteUser
    lateinit var updateUser: EditUser
    lateinit var fetchUsers: FetchAllUser
    lateinit var fetchUser: FetchUser
    lateinit var notificationService: NotificationService
    lateinit var repository: UserRepository


    override fun onCreate() {
        super.onCreate()
        notificationService = NotificationService(this)
        notificationService.createAndVerifyChannel()
        val dao = UserDatabase.getInstance(this).getUserDao()
        val localDataSource = UserDataSourceImpl(dao)
        val remoteDataSource = UserRemoteDataSourceImpl()
        repository = UserRepositoryImpl(localDataSource, remoteDataSource)
        setupUseCases()
        scheduleWorkManager(this)
    }

    private fun setupUseCases() {
        filterUser = FilterUsersImpl(repository)
        createUser = CreateUserImpl(repository)
        updateUser = EditUserImpl(repository)
        fetchUsers = FetchAllUserImpl(repository)
        deleteUser = DeleteUserImpl(repository)
        fetchUser = FetchUsersImpl(repository)
    }

    @SuppressLint("InvalidPeriodicWorkRequestInterval")
    private fun scheduleWorkManager(context: Context) {
        // Calculate the initial delay to 4:00 AM
        val currentTime = Calendar.getInstance()
        val dueTime = Calendar.getInstance()

        dueTime.set(Calendar.HOUR_OF_DAY, 12)
        dueTime.set(Calendar.MINUTE, 32)
        dueTime.set(Calendar.SECOND, 0)

        if (dueTime.before(currentTime)) {
            dueTime.add(Calendar.HOUR_OF_DAY, 24)
        }

        val initialDelay = dueTime.timeInMillis - currentTime.timeInMillis

        val workerRequest = PeriodicWorkRequest.Builder(NotificationWorker::class.java, 30,TimeUnit.SECONDS).build()
        WorkManager.getInstance(this).enqueue(workerRequest)
    }
}