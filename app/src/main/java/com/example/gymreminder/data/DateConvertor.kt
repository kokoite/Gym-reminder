package com.example.gymreminder.data

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.util.Date

class DateConverter {

    @TypeConverter
    fun convertToDate(value: Long?): Date? {
        return value?.let {
            Date(it)
        }
    }

    @TypeConverter
    fun convertToDate(value: Date?): Long? {
        return value?.time
    }
}