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
import com.example.gymreminder.usecase.FilterUsers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date


const val milisecondInDay = 1000* 3600 * 24L
class HomeViewModel(private val fetchAllUserUseCase: FetchAllUser,
                    private val filterUserUseCase: FilterUsers
                    ): ViewModel() {
    private var _mutableListOfUser = MutableLiveData<List<UserSummary>>()
    val listOfUser: LiveData<List<UserSummary>> get() = _mutableListOfUser
    private var filterName: Job? = null
    private var filterExpiry: Job? = null

    fun fetchAllUser() {
        viewModelScope.launch(Dispatchers.IO) {
            val users = fetchAllUserUseCase.fetchAll()
            _mutableListOfUser.postValue(users)
        }
    }

    fun filterUser(filterType: UserFilter) {

        when(filterType) {
            is UserFilter.ExpireFilter -> handleExpiryFiltering(filterType.days)
            is UserFilter.NameFilter -> handleNameFiltering(filterType.name)
        }
    }

    private fun handleNameFiltering(name: String) {
        filterName?.cancel()
        filterName = viewModelScope.launch {
           try {
               val users = filterUserUseCase.filterUserBasedOn(name)
               _mutableListOfUser.postValue(users)
           } catch (error: Error) {
               Log.d(TAG, "handleNameFiltering: Something went wrong")
           }
        }
    }

    private fun handleExpiryFiltering(days: Int) {
        filterExpiry?.cancel()
        filterExpiry = viewModelScope.launch {
            try {
                val users = filterUserUseCase.filterUserBasedOn(days)
                _mutableListOfUser.postValue(users)
            } catch (error: Error) {
                Log.d(TAG, "Error occured in expiry")
            }
        }
    }

    fun clearAllFilter() {
        // TODO -: Show unfiltered cached response
    }
}