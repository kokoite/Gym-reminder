package com.example.gymreminder.usecase

import com.example.gymreminder.data.User
import com.example.gymreminder.repository.UserRepository

class FetchUsersImpl(private val repository: UserRepository): FetchUser {

    override suspend fun fetchDetail(userId: String): User {
        return repository.fetchUserLocally(userId)
    }
}