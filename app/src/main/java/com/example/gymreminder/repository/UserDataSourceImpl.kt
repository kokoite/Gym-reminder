package com.example.gymreminder.repository

import androidx.lifecycle.LifecycleCoroutineScope
import com.example.gymreminder.data.User
import com.example.gymreminder.data.UserDao
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class UserDataSourceImpl(private val dao: UserDao, private val viewModelScope: LifecycleCoroutineScope): UserDataSource {
    override fun fetchAllUsers(): List<User>{
        return listOf()
    }

    override fun fetchUser(userId: String) {

    }

    override fun editUser() {

    }

    override fun deleteUser(userId: String) {

    }
}