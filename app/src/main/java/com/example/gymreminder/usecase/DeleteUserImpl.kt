package com.example.gymreminder.usecase

import com.example.gymreminder.repository.UserRepository

class DeleteUserImpl(private val repository: UserRepository): DeleteUser {

    override suspend fun delete(userId: String) {
        repository.deleteUserLocally(userId)
    }
}