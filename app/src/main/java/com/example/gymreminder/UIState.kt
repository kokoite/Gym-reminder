package com.example.gymreminder

sealed class UIState<out T> {
    object Loading: UIState<Nothing>()
    data class Success<T>(val result: T): UIState<T>()
    data class Error(val messsage: String): UIState<Nothing>()
}


enum class UserActions {
    CREATE_USER,
    EDIT_USER,
    VIEW_USER
}