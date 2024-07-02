package com.example.gymreminder.usecase

import com.example.gymreminder.data.User
import com.example.gymreminder.data.UserDao
import kotlinx.coroutines.coroutineScope

class CreateUserImpl(private val dao: UserDao): CreateUser {
    override suspend fun createUser(user: User) {
        coroutineScope {
            dao.createUser(user)
        }
    }

}