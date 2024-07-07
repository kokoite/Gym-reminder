package com.example.gymreminder.playground

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

suspend fun main() {
    CoroutineScope(Dispatchers.IO).launch {
        println("before")
        delay(100)
        println("after")
    }.join()


    println("without")
}





suspend fun getNumber(): Int {
    var number: Int = 10
    coroutineScope {

        launch {
            delay(100)
//            number += 2
            println("with launch")
        }
        println("without launch")
    }
    return number
}