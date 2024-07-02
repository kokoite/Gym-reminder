package com.example.gymreminder.data

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Date

const val TAG = "GymApp"
@Database(entities = [User::class], version = 1)
abstract class UserDatabase: RoomDatabase() {

    abstract fun getUserDao(): UserDao



    companion object {
        private var instance: UserDatabase? = null

        val migration1_2 =  object : Migration(1,2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE user")
            }
        }

        @Synchronized
        fun getInstance(context: Context): UserDatabase {
            if (instance != null) {
                return instance!!
            }
            val instance = Room.databaseBuilder(
                context.applicationContext,
                UserDatabase::class.java, "user_database"
            )
                .fallbackToDestructiveMigration()
                .addCallback(roomCallback)
                .build()
            this.instance = instance
            return instance
        }

        private val roomCallback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                Log.d(TAG, "Db created success")
                /* TODO :- If anything need to be done as database gets
                    created we can do that here
                */
            }
        }
    }
}