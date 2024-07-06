package com.example.gymreminder

sealed class UIState<out T> {
    data object Loading: UIState<Nothing>()
    data class Success<T>(val result: T): UIState<T>()
    data class Error(val message: String): UIState<Nothing>()
}

enum class UserActions {
    CREATE_USER,
    EDIT_USER,
    VIEW_USER
}