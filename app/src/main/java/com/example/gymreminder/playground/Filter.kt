package com.example.gymreminder.playground

fun main () {
    val num = listOf(1,2,3,4,5,6)
    val n = num.filter {
        it%2 == 0
    }
    println(n)
}