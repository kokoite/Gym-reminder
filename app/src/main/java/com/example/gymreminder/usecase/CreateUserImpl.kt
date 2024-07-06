package com.example.gymreminder.usecase

import com.example.gymreminder.data.User
import com.example.gymreminder.data.UserDao
import com.example.gymreminder.repository.UserRepository
import kotlinx.coroutines.coroutineScope

class CreateUserImpl(private val repository: UserRepository): CreateUser {
    override suspend fun createUser(user: User) {
        coroutineScope {
            repository.createUserLocally(user)
        }
    }

}