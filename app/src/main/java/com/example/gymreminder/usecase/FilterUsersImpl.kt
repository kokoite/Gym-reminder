package com.example.gymreminder.usecase

import com.example.gymreminder.Constants
import com.example.gymreminder.data.Subrange
import com.example.gymreminder.data.User
import com.example.gymreminder.data.UserDao
import com.example.gymreminder.data.UserSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.min

class FilterUsersImpl(val dao: UserDao): FilterUsers {
    private var cachedUserList: List<UserSummary> = listOf()
    // ByDefault we can set number of coroutines to 5. Can be increased based on some data insights
    override suspend fun filterUserBasedOn(name: String): List<UserSummary> {
        return coroutineScope {
            val subranges = createSubranges(5)
            subranges.map {
                async(Dispatchers.IO) {
                    val users = dao.getPaginatedUsers(it.end-it.start, it.start)
                    withContext(Dispatchers.Default) {
                        users.filter {
                            it.name.contains(name, ignoreCase = true)
                        }.map {
                            UserSummary(it.userId,it.name, it.phoneNumber, it.joiningDate, it.expiryDate, it.photo)
                        }
                    }
                }
            }.awaitAll().flatten()
        }
    }

    override suspend fun filterUserBasedOn(expiryDays: Int): List<UserSummary> {
        return listOf()
    }

    private suspend fun createSubranges(numberOfCoroutines: Int): List<Subrange> {
        var start = cachedUserList.size
        val length = coroutineScope {
            async {
                dao.getAllUserCount()
            }.await()
        }
        val quotient = (length+numberOfCoroutines-1)/numberOfCoroutines
        var subrange: MutableList<Subrange> = mutableListOf()
        repeat(numberOfCoroutines-1) {
            subrange.add(
                Subrange(start, it * quotient)
            )
            start = it*quotient+1
        }
        subrange.add(
            Subrange(start, length)
        )
        return subrange
    }
}