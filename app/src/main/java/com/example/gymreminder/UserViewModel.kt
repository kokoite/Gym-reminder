package com.example.gymreminder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymreminder.data.User
import com.example.gymreminder.data.UserDao
import kotlinx.coroutines.launch

class UserViewModel(private val dao: UserDao): ViewModel() {

    val createUserLiveData: LiveData<UIState<Boolean>> get() = _createUserLiveData
    val fetchUserLiveData: LiveData<UIState<User>> get() = _fetchUserLiveData
    val updateUserLiveData: LiveData<UIState<User>> get() = _updateUserLiveData
    val deleteUserLiveData: LiveData<UIState<Boolean>> get() = _deleteUserLiveData
    private var _createUserLiveData: MutableLiveData<UIState<Boolean>> = MutableLiveData()
    private var _fetchUserLiveData: MutableLiveData<UIState<User>> = MutableLiveData()
    private val _updateUserLiveData: MutableLiveData<UIState<User>> = MutableLiveData()
    private val _deleteUserLiveData: MutableLiveData<UIState<Boolean>> = MutableLiveData()


    fun createUser(user: User) {
        _createUserLiveData.value = UIState.Loading
        viewModelScope.launch {
            try {
                dao.createUser(user)
                _createUserLiveData.value = UIState.Success<Boolean>(true)
            } catch (error: Error) {
                _createUserLiveData.value = UIState.Error("Something went wrong. Unable to create user")
            }
        }
    }

    fun fetchUserDetail(userId: Int)  {
        _fetchUserLiveData.value = UIState.Loading
        viewModelScope.launch {
            try {
                val user = dao.getUser(userId)
                _fetchUserLiveData.value =  UIState.Success<User>(user.first())
            } catch (error: Error) {
                _fetchUserLiveData.value = UIState.Error("Something went wrong. Unable to fetch user")
            }
        }
    }

    fun updateUserDetail(user: User) {
        _updateUserLiveData.value = UIState.Loading
        viewModelScope.launch {
            try {
                dao.updateUser(user)
                _updateUserLiveData.value = UIState.Success(user)
            } catch (error: Error) {
                _updateUserLiveData.value = UIState.Error("Something went wrong ${error.localizedMessage}")
            }
        }
    }

    fun deleteUser(user: User) {
        _deleteUserLiveData.value = UIState.Loading
        viewModelScope.launch {
            try {
                dao.deleteUser(user)
                _deleteUserLiveData.value = UIState.Success(false)
            } catch (error: Error) {
                _deleteUserLiveData.value = UIState.Error("Something went wrong ${error.localizedMessage}")
            }
        }
    }
}