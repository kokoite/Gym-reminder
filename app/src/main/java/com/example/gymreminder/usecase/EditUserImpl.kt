package com.example.gymreminder.usecase

import com.example.gymreminder.data.User
import com.example.gymreminder.repository.UserRepository

class EditUserImpl(private val repository: UserRepository): EditUser {

    override suspend fun updateUser(user: User) {
        repository.editUserLocally(user)
    }
}