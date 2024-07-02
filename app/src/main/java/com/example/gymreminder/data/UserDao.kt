package com.example.gymreminder.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
@Dao
interface UserDao {

    @Insert
    suspend fun createUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("select * from user")
     suspend fun getAllUsers(): List<User>

    @Query("select Count(*) from user")
    suspend fun getAllUserCount(): Int

    @Query("select * from user order by userId ASC LIMIT :pageSize offset :offset")
    suspend fun getPaginatedUsers(pageSize: Int, offset: Int): List<User>

    @Query("select * from user where userId = :userId")
    suspend fun getUser(userId: Int): List<User>
}