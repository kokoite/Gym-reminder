package com.example.gymreminder.repository

interface UserRemoteDataSource {
    fun fetchAllUsers()
    fun fetchUser(userId: String)
    fun editUser()
    fun deleteUser(userId: String)
}