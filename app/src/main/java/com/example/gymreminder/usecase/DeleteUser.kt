package com.example.gymreminder.usecase

interface DeleteUser {
    suspend fun delete(userId: String)
}