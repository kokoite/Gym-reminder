package com.example.gymreminder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gymreminder.data.UserDao
import com.example.gymreminder.usecase.CreateUser
import com.example.gymreminder.usecase.FetchAllUser

class HomeViewModelFactory(val fetchUser: FetchAllUser, val createUser: CreateUser): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(fetchUser, createUser) as T
    }
}