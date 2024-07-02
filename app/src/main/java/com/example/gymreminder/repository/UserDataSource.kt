package com.example.gymreminder.repository

import com.example.gymreminder.data.User

interface UserDataSource {
    fun fetchAllUsers(): List<User>
    fun fetchUser(userId: String)
    fun editUser()
    fun deleteUser(userId: String)
}


