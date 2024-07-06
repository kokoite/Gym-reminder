package com.example.gymreminder.repository

import androidx.lifecycle.LifecycleCoroutineScope
import com.example.gymreminder.data.User
import com.example.gymreminder.data.UserDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserDataSourceImpl(private val dao: UserDao): UserDataSource {
    override suspend fun fetchAllUsers(): List<User>{
        return CoroutineScope(Dispatchers.IO).async {
            dao.getAllUsers()
        }.await()
    }

    override suspend fun fetchUser(userId: String): User {
        return CoroutineScope(Dispatchers.IO).async {
            val id = userId.toInt()
            dao.getUser(id).first()
        }.await()
    }

    override suspend fun editUser(user: User) {
        return  CoroutineScope(Dispatchers.IO).async {
            dao.updateUser(user)
        }.await()
    }

    override suspend fun deleteUser(userId: String) {
        return CoroutineScope(Dispatchers.IO) .async {
            val id = userId.toInt()
            dao.deleteUser(id)
        }.await()
    }

    override suspend fun createUser(user: User) {
        return CoroutineScope(Dispatchers.IO).async {
            dao.createUser(user)
        }.await()
    }

    override suspend fun fetchPaginatedUsers(pageSize: Int, offset: Int): List<User> {
        return CoroutineScope(Dispatchers.IO).async {
            dao.getPaginatedUsers(pageSize, offset)
        }.await()
    }

    override suspend fun getUsersCount(): Int {
        return CoroutineScope(Dispatchers.IO).async {
            dao.getAllUserCount()
        }.await()
    }
}