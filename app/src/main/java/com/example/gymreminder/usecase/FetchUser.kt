package com.example.gymreminder.usecase

import com.example.gymreminder.data.User

interface FetchUser {
    fun fetchDetail(userId: String): User
}