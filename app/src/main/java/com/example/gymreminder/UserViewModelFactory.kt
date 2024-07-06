package com.example.gymreminder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.ViewModelFactoryDsl
import com.example.gymreminder.data.UserDao
import com.example.gymreminder.usecase.CreateUser
import com.example.gymreminder.usecase.DeleteUser
import com.example.gymreminder.usecase.EditUser
import com.example.gymreminder.usecase.FetchUser

class UserViewModelFactory(private val fetchUser: FetchUser,
                           private val editUser: EditUser,
                           private val createUser: CreateUser,
                           private val deleteUser: DeleteUser): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserViewModel(createUser, editUser, deleteUser, fetchUser) as T
    }
}