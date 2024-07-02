package com.example.gymreminder.playground

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

suspend fun main() {
    println("before coroutine scope")
    coroutineScope {
        println("before first coroutine")
        delay(200)
        println("after first coroutine")
    }
    println("between")
    coroutineScope {

        launch(Dispatchers.IO) {
            println("in launch")
            delay(100)
//            number += 2
            println("with launch")
        }.join()
        println("without launch")
    }

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