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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.util.Date


class HomeViewModel(private val fetchAllUserUseCase: FetchAllUser,
                    private val filterUserUseCase: FilterUsers
                    ): ViewModel() {
    private var _mutableListOfUser = MutableLiveData<List<UserSummary>>()
    val listOfUser: LiveData<List<UserSummary>> get() = _mutableListOfUser
    private var filterName: Job? = null
    private var filterExpiry: Job? = null
    private var filterActive: Job? = null
    private var filterActiveWithExpiry: Job? = null

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
            is UserFilter.ActiveFilter -> handleActiveFiltering(filterType.isActive)
            is UserFilter.ActiveWithExpiry -> handleActiveWithExpiry()
        }
    }

    private fun handleActiveFiltering(isActive: Boolean) {
        filterActive?.cancel()
        filterActive = viewModelScope.launch {
            try {
                val users = filterUserUseCase.filterUserBasedOnActive(isActive)
                _mutableListOfUser.postValue(users)
            } catch (error: Exception) {
                Log.d(TAG, "handleActiveFiltering: Something went wrong ${error.localizedMessage}")
            }
        }
    }

    private fun handleActiveWithExpiry() {
        filterActiveWithExpiry?.cancel()
        filterActiveWithExpiry = viewModelScope.launch {
            try {
                val users = filterUserUseCase.filterActiveUserButExpiryPassed()
                _mutableListOfUser.postValue(users)
            } catch (error: Exception) {
                Log.d(TAG, "handleActiveWithExpiry: Something went wrong ${error.localizedMessage}")
            }
        }
    }

    private fun handleNameFiltering(name: String) {
        filterName?.cancel()
        filterName = viewModelScope.launch {
           try {
               delay(300)
               if(filterName?.isActive == true) {
                   val users = filterUserUseCase.filterUserBasedOn(name)
                   _mutableListOfUser.postValue(users)
               }
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
        fetchAllUser()
    }
}