package io.github.olbpetersson.kvent.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import io.github.olbpetersson.kvent.persistence.Persistence
import java.util.concurrent.atomic.AtomicBoolean

typealias AggregateId = String

abstract class Aggregate<T>(
    private val id: AggregateId,
    initialState: T,
    private val persistence: Persistence
) {
    private val eventChannel: SendChannel<Event>
    var state: T = initialState
        private set


    private val replayInAction = AtomicBoolean(false)

    init {
        eventChannel = GlobalScope.handleEvent(initialState)
    }

    suspend fun send(command: Command) {
        while (replayInAction.get()) {
            delay(10)
        }
        GlobalScope.launch {
            val event = routeCommand(command)
            eventChannel.send(event)
        }
    }

    abstract fun onStateChange(newState: T, oldState: T)

    abstract fun routeCommand(command: Command): Event

    abstract fun routeEvent(event: Event): T

    private fun CoroutineScope.handleEvent(initialState: T) = actor<Event> {
        for (event in channel) {
            if (event is ResetStateEvent) {
                state = initialState
            } else {
                val oldState = state
                onEvent(event)
                persistEvent(event)
                state = routeEvent(event)
                onStateChange(state, oldState)
            }
        }
    }

    private suspend fun persistEvent(event: Event) {
        persistence.persistForId(event, id)
    }

    suspend fun replay() {
        replayInAction.set(true)
        val events = persistence.getEventsForId(id)
        eventChannel.send(ResetStateEvent())

        events?.forEach { eventChannel.send(it) }
        replayInAction.set(false)

    }

    open fun onEvent(event: Event) {
        // NO-OP if not chosen
    }
}

private class ResetStateEvent : Event()
