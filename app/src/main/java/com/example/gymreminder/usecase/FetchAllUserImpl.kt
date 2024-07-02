package com.example.gymreminder.usecase

import com.example.gymreminder.data.User
import com.example.gymreminder.data.UserDao
import com.example.gymreminder.data.UserFilter
import com.example.gymreminder.data.UserSummary
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class FetchAllUserImpl(private val dao: UserDao): FetchAllUser {

    var response: List<UserSummary>? = null

    override suspend fun fetchAll(): List<UserSummary> {
        var userSummary = listOf<UserSummary>()
        coroutineScope {
            val users = async {
                dao.getAllUsers()
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

    override suspend fun filterUserBasisOnExpiry(day: Int): List<UserSummary> {
        var filterList: List<UserSummary> = listOf()
        coroutineScope {
            if(response == null) {
                launch {
                    fetchAll()
                }.join()
            }
            response?.let {
                filterList = it
            }
        }

        return filterList
    }

    override suspend fun filterUserBasisOnName(name: String): List<UserSummary> {
        var filterList: List<UserSummary> = listOf()
        coroutineScope {
            if(response == null) {

            }
        }

        return filterList
    }


}