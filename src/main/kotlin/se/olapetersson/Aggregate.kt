package se.olapetersson

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ActorScope
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import java.util.concurrent.ConcurrentLinkedQueue

abstract class Aggregate {
    abstract val _id: String
    abstract var state: IntState
    private lateinit var commandActor: ActorScope<Command>
    private lateinit var eventChannel: SendChannel<Event>

    // Imitates the database at this point
    var database = ConcurrentLinkedQueue<Event>()

    // TODO: Change this GlobalScope
    fun create() = GlobalScope.actor<Command> {
        eventChannel = handleEvent()
        println("setup the actor")
        for (command in channel) {
            val event = routeCommand(command)
            eventChannel.send(event)
        }
    }

    abstract fun onStateChange(state: State)

    // Should return an event
    abstract fun routeCommand(command: Command): Event

    abstract fun routeEvent(event: Event): IntState

    private fun CoroutineScope.handleEvent() = actor<Event> {
        for (event in channel) {
            onEvent(event)
            persistEvent(event)
            state = routeEvent(event)
            onStateChange(state)
        }
    }

    private fun persistEvent(event: Event) {
        database.add(event)
    }

    suspend fun replay() {
        state = IntState(0)
        var databaseCopy = database
        database = ConcurrentLinkedQueue()
        databaseCopy.forEach { eventChannel.send(it) }

    }

    open fun onEvent(event: Any) {
        // NO-OP if not chosen
    }
}

abstract class State
abstract class Event
abstract class Command
