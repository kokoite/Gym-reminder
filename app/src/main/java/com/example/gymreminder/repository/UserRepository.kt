package com.example.gymreminder.repository

import com.example.gymreminder.data.User

interface UserRepository {

    suspend fun fetchAllUsersLocally(): List<User>
    fun fetchAllUsersRemotely()

    suspend fun fetchUserLocally(userId: String): User
    fun fetchUserRemotely()

    suspend fun editUserLocally(user: User)
    fun editUserRemotely()

    suspend fun deleteUserLocally(userId: String)
    fun deleteUserRemotely()

    suspend fun createUserLocally(user: User)
    fun createUserRemotely()

    suspend fun fetchPaginatedUsersLocally(pageSize: Int, offset: Int): List<User>

    suspend fun getAllUserCountLocally(): Int

}