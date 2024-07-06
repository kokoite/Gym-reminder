package com.example.gymreminder.usecase

import com.example.gymreminder.data.UserSummary

interface FilterUsers {

    // TODO :- Need to implement
    suspend fun filterUserBasedOn(name: String): List<UserSummary>
    suspend fun filterUserBasedOn(expiryDays: Int): List<UserSummary>
    suspend fun filterUserBasedOnInactive(): List<UserSummary>
    suspend fun filterActiveUserButExpiryPassed(): List<UserSummary>
}