package com.example.gymreminder.repository

interface UserRepository {

    fun fetchAllUsersLocally()

    fun fetchAllUsersRemotely()

    fun fetchUserLocally()
    fun fetchUserRemotely()

    fun editUserLocally()
    fun editUserRemotely()

    fun deleteUserLocally()
    fun deleteUserRemotely()

}