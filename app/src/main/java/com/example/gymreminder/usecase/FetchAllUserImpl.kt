package com.example.gymreminder.usecase

import com.example.gymreminder.data.User
import com.example.gymreminder.data.UserDao
import com.example.gymreminder.data.UserFilter
import com.example.gymreminder.data.UserSummary
import com.example.gymreminder.repository.UserRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class FetchAllUserImpl(private val repository: UserRepository): FetchAllUser {

    private var response: List<UserSummary>? = null

    override suspend fun fetchAll(): List<UserSummary> {
        response?.let {
            return it
        }
        var userSummary = listOf<UserSummary>()
        coroutineScope {
            val users = async {
                repository.fetchAllUsersLocally()
            }.await()
            userSummary = users.map {
                UserSummary(
                    it.userId,
                    it.name,
                    it.phoneNumber,
                    it.joiningDate,
                    it.expiryDate,
                    it.photo
                )
            }
            return@coroutineScope
        }
        response = userSummary
        return userSummary
    }

    override suspend fun fetchMoreUsers(): List<UserSummary> {
        // TODO :- Using pagination here
        return listOf()
    }
}