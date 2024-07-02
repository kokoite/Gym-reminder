package com.example.gymreminder.playground

class Person<out T> (val name: String, val age: T) {
    val address = listOf<String>()

    fun getNamme(): String {
        return ""
    }

    fun getT(): T {
        return age
    }

}