package com.example.gymreminder.usecase

import com.example.gymreminder.data.User

interface EditUser {
    suspend fun updateUser(user: User)
}