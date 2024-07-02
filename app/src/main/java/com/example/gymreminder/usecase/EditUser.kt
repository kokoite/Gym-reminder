package com.example.gymreminder.usecase

import com.example.gymreminder.data.User

interface EditUser {
    fun updateUser(user: User): Boolean
}