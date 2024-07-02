package com.example.gymreminder.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "user")
data class User(
                @PrimaryKey(autoGenerate = true)val userId: Long,
                val name: String,
                val phoneNumber: String,
                val weight: Int,
                val joiningDate: String,
                val expiryDate: String,
                val photo: String,
                val address: String,
                val existingProblems: String,
                val paymentDone: Boolean,
                val gender: String
)


data class UserSummary(
                     val userId: Long,
                     val name: String,
                     val phone: String,
                     val joiningDate: String,
                     val expiryDate: String,
                     val photo: String
) {

    fun equals(userSummary: UserSummary): Boolean {
        return userId == userSummary.userId &&
                name == userSummary.name &&
                userSummary.phone == phone &&
                userSummary.joiningDate == joiningDate &&
                userSummary.expiryDate == expiryDate &&
                userSummary.photo == photo
    }
}

enum class Gender(val description: String) {

    MALE("Male"),
    FEMALE("Female"),
    OTHERS("Others");
}


sealed class UserFilter {
    class ExpireFilter(days: Int): UserFilter()
    class NameFilter(name: String): UserFilter()
}





