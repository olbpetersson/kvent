package io.github.olbpetersson.kvent

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val myExample = Example(id = "myId", initialState = IntState(0))

    for (i in (1..10)) {
        myExample.send(AddOneCommand(1))
    }
    println("sleeping")
    delay(5000)
    myExample.replay()
    delay(5000)
}
