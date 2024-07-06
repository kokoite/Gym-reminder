package com.example.gymreminder

import android.app.Application
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

class MyApplication: Application() {

    companion object {
        const val TAG = "GymApp"
    }

    // UseCases
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
        scheduleWorkManager()
    }

    private fun scheduleWorkManager() {

    }

    private fun setupUseCases() {
        filterUser = FilterUsersImpl(repository)
        createUser = CreateUserImpl(repository)
        updateUser = EditUserImpl(repository)
        fetchUsers = FetchAllUserImpl(repository)
        deleteUser = DeleteUserImpl(repository)
        fetchUser = FetchUsersImpl(repository)
    }
}