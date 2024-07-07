package com.example.gymreminder.usecase

import android.util.Log
import com.example.gymreminder.data.Subrange
import com.example.gymreminder.data.UserDao
import com.example.gymreminder.data.UserSummary
import com.example.gymreminder.repository.UserRepository
import com.example.gymreminder.utility.convertToStringToMillis
import com.example.gymreminder.utility.milisecondInDay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.lang.IllegalArgumentException
import java.text.SimpleDateFormat
import java.util.Locale

class FilterUsersImpl(private val repository: UserRepository): FilterUsers {

    companion object {
        const val TAG = "GymApp"
    }

    // ByDefault we can set number of coroutines to 5. Can be increased based on some data insights
    override suspend fun filterUserBasedOn(name: String): List<UserSummary> {

        return CoroutineScope(Dispatchers.IO).async {
            val subranges = createSubranges(5)
            Log.d(TAG, "filterUserBasedOn: $subranges")
            subranges.map {
                async(Dispatchers.IO) {
                    val users = repository.fetchPaginatedUsersLocally(it.end-it.start, it.start)
                    Log.d(TAG, "paginated: ${users}")
                    withContext(Dispatchers.Default) {
                        users.filter {
                            it.name.contains(name, ignoreCase = true)
                        }.map {
                            UserSummary(it.userId,it.name, it.phoneNumber, it.joiningDate, it.expiryDate, it.photo)
                        }
                    }
                }
            }.awaitAll().flatten()
        }.await()
    }

    override suspend fun filterUserBasedOn(expiryDays: Int): List<UserSummary> {
        return CoroutineScope(Dispatchers.IO).async {
            val subranges = createSubranges(5)
            Log.d(TAG, "filterUserBasedOn: $subranges")
            subranges.map {
                async(Dispatchers.IO) {
                    val users = repository.fetchPaginatedUsersLocally(it.end-it.start, it.start)
                    Log.d(TAG, "paginated: ${users}")
                    withContext(Dispatchers.Default) {
                        users.filter {
                            val expiryTime = convertToStringToMillis(it.expiryDate)
                            val currentTime = System.currentTimeMillis()
                            val joiningTime = convertToStringToMillis(it.joiningDate)
                            ((expiryTime <= currentTime + ((expiryDays+1) *  milisecondInDay)) && (expiryTime > joiningTime) && (it.isActive))

                        }.map {
                            UserSummary(it.userId,it.name, it.phoneNumber, it.joiningDate, it.expiryDate, it.photo)
                        }
                    }
                }
            }.awaitAll().flatten()
        }.await()
    }

    override suspend fun filterUserBasedOnInactive(): List<UserSummary> {
        return CoroutineScope(Dispatchers.IO).async {
            val subrange = createSubranges(5)
            subrange.map {
                async {
                    val users = repository.fetchPaginatedUsersLocally(it.end-it.start,it.start)
                    withContext(Dispatchers.Default) {
                        users.filter { !it.isActive  }
                    }.map {
                        UserSummary(it.userId, it.name, it.phoneNumber, it.joiningDate, it.expiryDate, it.photo)
                    }
                }
            }.awaitAll().flatten()
        }.await()
    }

    override suspend fun filterActiveUserButExpiryPassed(): List<UserSummary> {
        return CoroutineScope(Dispatchers.IO).async {

            val subranges = createSubranges(5)
            subranges.map {
                async {
                    val users = repository.fetchPaginatedUsersLocally(it.end -it.start, it.start)
                    withContext(Dispatchers.Default) {
                        users.filter {
                            val currentTime = System.currentTimeMillis()
                            val expiryTime = convertToStringToMillis(it.expiryDate)
                            expiryTime < currentTime && isActive
                        }
                    }.map {
                        UserSummary(it.userId, it.name, it.phoneNumber, it.joiningDate, it.expiryDate, it.photo)
                    }
                }
            }.awaitAll().flatten()
        }.await()
    }

    private suspend fun createSubranges(numberOfCoroutines: Int): List<Subrange> {
        var start = 0
        val length = coroutineScope {
            async {
                repository.getAllUserCountLocally()
            }.await()
        }
        val quotient = (length+numberOfCoroutines-1)/numberOfCoroutines
        var subrange: MutableList<Subrange> = mutableListOf()
        repeat(numberOfCoroutines-1) {
            subrange.add(
                Subrange(start, it * quotient)
            )
            start = it*quotient
        }
        subrange.add(
            Subrange(start, length)
        )
        return subrange
    }
}