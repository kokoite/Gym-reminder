package com.example.gymreminder.usecase

import com.example.gymreminder.data.UserFilter
import com.example.gymreminder.data.UserSummary

interface FetchAllUser {
    suspend fun fetchAll(): List<UserSummary>
    suspend fun fetchMoreUsers(): List<UserSummary>
}