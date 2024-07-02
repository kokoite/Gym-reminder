package com.example.gymreminder.playground

import com.example.gymreminder.UserActions
import com.example.gymreminder.data.User


fun main() {
    println(UserActions.CREATE_USER.toString())

    val str = UserActions.CREATE_USER.toString()
    val enm = UserActions.valueOf(str)
    println(enm)
}