package com.example.gymreminder.repository

import com.example.gymreminder.data.User

interface UserDataSource {
    suspend fun fetchAllUsers(): List<User>
    suspend fun fetchUser(userId: String): User
    suspend fun editUser(user: User)
    suspend fun deleteUser(userId: String)
    suspend fun createUser(user: User)
    suspend fun fetchPaginatedUsers(pageSize: Int, offset: Int): List<User>
    suspend fun getUsersCount(): Int
}


