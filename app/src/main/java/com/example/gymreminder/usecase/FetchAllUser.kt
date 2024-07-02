package com.example.gymreminder.usecase

import com.example.gymreminder.data.UserFilter
import com.example.gymreminder.data.UserSummary

interface FetchAllUser {
    suspend fun fetchAll(): List<UserSummary>
    suspend fun filterUserBasisOnExpiry(day: Int): List<UserSummary>
    suspend fun filterUserBasisOnName(name: String): List<UserSummary>
}