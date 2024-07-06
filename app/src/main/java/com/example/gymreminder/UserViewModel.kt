package com.example.gymreminder

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymreminder.data.User
import com.example.gymreminder.data.UserDao
import com.example.gymreminder.repository.UserRepository
import com.example.gymreminder.usecase.CreateUser
import com.example.gymreminder.usecase.DeleteUser
import com.example.gymreminder.usecase.EditUser
import com.example.gymreminder.usecase.FetchUser
import com.example.gymreminder.utility.convertToStringToMillis
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel(private val createUser: CreateUser,
                    private val updateUser: EditUser,
                    private val deleteUser: DeleteUser,
                    private val fetchUser: FetchUser
    ) : ViewModel() {

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
                            createUser.createUser(user)
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
        createUserJob?.cancel()
        if(!shouldUpdateCreateUser(user)) {
            _createUserLiveData.value = UIState.Error("Some fields are incomplete or invalid")
            return
        }
        _createUserLiveData.value = UIState.Loading
        createUserJob = viewModelScope.launch {
            try {
                createUser.createUser(user)
                _createUserLiveData.value = UIState.Success<Boolean>(true)
            } catch (error: Error) {
                _createUserLiveData.value =
                    UIState.Error("Something went wrong. Unable to create user")
            }
        }
    }

    fun fetchUserDetail(userId: String) {
        fetchUserJob?.cancel()
        _fetchUserLiveData.value = UIState.Loading
        fetchUserJob = viewModelScope.launch {
            try {
                val user = fetchUser.fetchDetail(userId)
                _fetchUserLiveData.value = UIState.Success<User>(user)
            } catch (error: Error) {
                _fetchUserLiveData.value =
                    UIState.Error("Something went wrong. Unable to fetch user")
            }
        }
    }

    fun updateUserDetail(user: User) {
        if(!shouldUpdateCreateUser(user)) {
            _updateUserLiveData.value = UIState.Error("Some fields are incomplete or invalid")
            return
        }
        updateUserJob?.cancel()
        _updateUserLiveData.value = UIState.Loading
        updateUserJob = viewModelScope.launch {
            try {
                updateUser.updateUser(user)
                _updateUserLiveData.value = UIState.Success(user)
            } catch (error: Error) {
                _updateUserLiveData.value =
                    UIState.Error("Something went wrong ${error.localizedMessage}")
            }
        }
    }

    fun deleteUser(userId: String) {
        deleteUserJob?.cancel()
        _deleteUserLiveData.value = UIState.Loading
        deleteUserJob = viewModelScope.launch {
            try {
                deleteUser.delete(userId)
                _deleteUserLiveData.value = UIState.Success(false)
            } catch (error: Error) {
                _deleteUserLiveData.value =
                    UIState.Error("Something went wrong ${error.localizedMessage}")
            }
        }
    }

    private fun shouldUpdateCreateUser(user: User): Boolean {
        if(user.name.isEmpty()
            || user.phoneNumber.length < 10
            || user.photo.isEmpty()
            || user.address.isEmpty()
            || user.existingProblems.isEmpty()
            || user.expiryDate.isEmpty()
            || user.gender.isEmpty()
            || user.weight > 200
            || convertToStringToMillis(user.joiningDate) > convertToStringToMillis(user.expiryDate)) {
            return  false
        }
        return  true
    }
}