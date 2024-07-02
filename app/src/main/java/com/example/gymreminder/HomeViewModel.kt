package com.example.gymreminder

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymreminder.data.TAG
import com.example.gymreminder.data.User
import com.example.gymreminder.data.UserFilter
import com.example.gymreminder.data.UserSummary
import com.example.gymreminder.usecase.CreateUser
import com.example.gymreminder.usecase.DeleteUser
import com.example.gymreminder.usecase.EditUser
import com.example.gymreminder.usecase.FetchAllUser
import com.example.gymreminder.usecase.FetchUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date


const val milisecondInDay = 1000* 3600 * 24L
class HomeViewModel(val fetchAllUserUseCase: FetchAllUser,
                    val createUserUseCase: CreateUser
                    ): ViewModel() {
    private var _mutableListOfUser = MutableLiveData<List<UserSummary>>()
    val listOfUser: LiveData<List<UserSummary>> get() = _mutableListOfUser


    fun fetchAllUser() {
        viewModelScope.launch(Dispatchers.IO) {
            val users = fetchAllUserUseCase.fetchAll()
            withContext(Dispatchers.Main) {
                _mutableListOfUser.postValue(users)
            }
        }
    }

//    fun createUser() {
//        val user = User(0,"pranjal", "23904309", 50, System.currentTimeMillis(), System.currentTimeMillis() + 7* milisecondInDay, "phohto", "address", "existing problems", false, "male")
//        viewModelScope.launch(Dispatchers.IO) {
//           for (i in 1..10) {
//                launch {
//                    createUserUseCase.createUser(user)
//                }
//           }
//            Log.d(TAG, "createUser: success")
//        }
//        Log.d(TAG, "createUser: After coroutine")
//    }

    fun filterUser(filterType: UserFilter) {

        when(filterType) {
            is UserFilter.ExpireFilter -> println("")
            is UserFilter.NameFilter -> println("")

        }

    }

    fun searchUser() {

    }










}