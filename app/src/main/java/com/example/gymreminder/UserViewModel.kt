package com.example.gymreminder

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymreminder.data.User
import com.example.gymreminder.data.UserDao
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class UserViewModel(private val dao: UserDao) : ViewModel() {

    val createUserLiveData: LiveData<UIState<Boolean>> get() = _createUserLiveData
    val fetchUserLiveData: LiveData<UIState<User>> get() = _fetchUserLiveData
    val updateUserLiveData: LiveData<UIState<User>> get() = _updateUserLiveData
    val deleteUserLiveData: LiveData<UIState<Boolean>> get() = _deleteUserLiveData
    private var _createUserLiveData: MutableLiveData<UIState<Boolean>> = MutableLiveData()
    private var _fetchUserLiveData: MutableLiveData<UIState<User>> = MutableLiveData()
    private val _updateUserLiveData: MutableLiveData<UIState<User>> = MutableLiveData()
    private val _deleteUserLiveData: MutableLiveData<UIState<Boolean>> = MutableLiveData()
    val batchInsertUserLiveData: MutableLiveData<Boolean> = MutableLiveData()

    private var fetchUserJob: Job? = null
    private var createUserJob: Job? = null
    private var updateUserJob: Job? = null
    private var deleteUserJob: Job? = null


    companion object {
        private const val TAG = "gymApp"
    }

    fun createMultipleUser(user: User) {
        viewModelScope.launch {
            launch {
                repeat(20) {
                    launch {
                        try {
                            dao.createUser(user)
                            Log.d(TAG, "createUser: success")
                        } catch (error: Error) {
                            Log.d(TAG, "createUser: Failed")
                        }
                    }
                }
            }.join()
            _createUserLiveData.value = UIState.Success<Boolean>(true)
        }
    }

    fun createUser(user: User) {
        Log.d(TAG, "createUser: Called")
        createUserJob?.cancel()
        _createUserLiveData.value = UIState.Loading
        createUserJob = viewModelScope.launch {
            try {
                dao.createUser(user)
                Log.d(TAG, "createUser: success")
                _createUserLiveData.value = UIState.Success<Boolean>(true)
            } catch (error: Error) {
                Log.d(TAG, "createUser: Failed")
                _createUserLiveData.value =
                    UIState.Error("Something went wrong. Unable to create user")
            }
        }
    }

    fun fetchUserDetail(userId: Int) {
        fetchUserJob?.let {
            it.cancel()
        }
        _fetchUserLiveData.value = UIState.Loading
        fetchUserJob = viewModelScope.launch {
            try {
                val user = dao.getUser(userId)
                _fetchUserLiveData.value = UIState.Success<User>(user.first())
            } catch (error: Error) {
                _fetchUserLiveData.value =
                    UIState.Error("Something went wrong. Unable to fetch user")
            }
        }
    }

    fun updateUserDetail(user: User) {
        updateUserJob?.let {
            it.cancel()
        }
        _updateUserLiveData.value = UIState.Loading
        updateUserJob = viewModelScope.launch {
            try {
                dao.updateUser(user)
                _updateUserLiveData.value = UIState.Success(user)
            } catch (error: Error) {
                _updateUserLiveData.value =
                    UIState.Error("Something went wrong ${error.localizedMessage}")
            }
        }
    }

    fun deleteUser(user: User) {
        deleteUserJob?.let {
            it.cancel()
        }
        _deleteUserLiveData.value = UIState.Loading
        deleteUserJob = viewModelScope.launch {
            try {
                dao.deleteUser(user)
                _deleteUserLiveData.value = UIState.Success(false)
            } catch (error: Error) {
                _deleteUserLiveData.value =
                    UIState.Error("Something went wrong ${error.localizedMessage}")
            }
        }
    }
}