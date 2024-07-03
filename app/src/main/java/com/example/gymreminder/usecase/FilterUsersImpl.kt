package com.example.gymreminder.usecase

import android.util.Log
import com.example.gymreminder.data.Subrange
import com.example.gymreminder.data.UserDao
import com.example.gymreminder.data.UserSummary
import com.example.gymreminder.milisecondInDay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.lang.IllegalArgumentException
import java.text.SimpleDateFormat
import java.util.Locale

class FilterUsersImpl(val dao: UserDao): FilterUsers {

    companion object {
        const val TAG = "GymApp"
    }
    private var cachedUserList: List<UserSummary> = listOf()
    private var nameFilteringScope: CoroutineScope? = null
    // ByDefault we can set number of coroutines to 5. Can be increased based on some data insights
    override suspend fun filterUserBasedOn(name: String): List<UserSummary> {

        return coroutineScope {
            val subranges = createSubranges(5)
            Log.d(TAG, "filterUserBasedOn: $subranges")
            subranges.map {
                async(Dispatchers.IO) {
                    val users = dao.getPaginatedUsers(it.end-it.start, it.start)
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
        }

    }

    override suspend fun filterUserBasedOn(expiryDays: Int): List<UserSummary> {
        return coroutineScope {
            val subranges = createSubranges(5)
            Log.d(TAG, "filterUserBasedOn: $subranges")
            subranges.map {
                async(Dispatchers.IO) {
                    val users = dao.getPaginatedUsers(it.end-it.start, it.start)
                    Log.d(TAG, "paginated: ${users}")
                    withContext(Dispatchers.Default) {
                        users.filter {
                            val expiryTime = convertToStringToMillis(it.expiryDate)
                            val currentTime = System.currentTimeMillis()
                            (expiryTime <= currentTime + expiryDays *  milisecondInDay)

                        }.map {
                            UserSummary(it.userId,it.name, it.phoneNumber, it.joiningDate, it.expiryDate, it.photo)
                        }
                    }
                }
            }.awaitAll().flatten()
        }
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

    private fun convertToStringToMillis(curDate: String): Long {
        val dateFormat = SimpleDateFormat("yyyy-mm-dd", Locale.getDefault())
        var timeInMilis: Long = 0
        try {
            timeInMilis = dateFormat.parse(curDate)?.time ?: throw IllegalArgumentException("Invalid format")
        } catch (error: Error) {
            Log.d(TAG, "convertToStringToMilis: Date format is wrong")
        }
        return timeInMilis
    }
}