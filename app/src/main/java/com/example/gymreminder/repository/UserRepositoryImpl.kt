package com.example.gymreminder.repository

import com.example.gymreminder.data.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class UserRepositoryImpl(private val userDataSource: UserDataSource, private val userRemoteDataSource: UserRemoteDataSource): UserRepository {
    override suspend fun fetchAllUsersLocally(): List<User> {
        return userDataSource.fetchAllUsers()
    }

    override fun fetchAllUsersRemotely() {
        userRemoteDataSource.fetchAllUsers()
    }

    override suspend fun fetchUserLocally(userId: String): User {
        return userDataSource.fetchUser(userId)
    }

    override fun fetchUserRemotely() {
        userRemoteDataSource.fetchUser("")
    }

    override suspend fun editUserLocally(user: User) {
        userDataSource.editUser(user)
    }

    override fun editUserRemotely() {
        userRemoteDataSource.editUser()
    }

    override suspend fun deleteUserLocally(userId: String) {
        userDataSource.deleteUser(userId)
    }

    override fun deleteUserRemotely() {
        userRemoteDataSource.deleteUser("")
    }

    override suspend fun createUserLocally(user: User) {
        userDataSource.createUser(user)
    }

    override fun createUserRemotely() {

    }

    override suspend fun fetchPaginatedUsersLocally(pageSize: Int, offset: Int): List<User> {
        return CoroutineScope(Dispatchers.IO).async {
            userDataSource.fetchPaginatedUsers(pageSize, offset)
        }.await()
    }

    override suspend fun getAllUserCountLocally(): Int {
        return CoroutineScope(Dispatchers.IO).async {
            userDataSource.getUsersCount()
        }.await()
    }
}