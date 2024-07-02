package com.example.gymreminder.usecase

import com.example.gymreminder.data.User

interface CreateUser {
    suspend fun createUser(user: User)
}