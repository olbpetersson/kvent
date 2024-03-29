package io.github.olbpetersson.kvent

import io.github.olbpetersson.kvent.model.Aggregate
import io.github.olbpetersson.kvent.model.AggregateId
import io.github.olbpetersson.kvent.model.Command
import io.github.olbpetersson.kvent.model.Event
import io.github.olbpetersson.kvent.persistence.InMemoryPersistence
import io.github.olbpetersson.kvent.persistence.Persistence

data class OneAddedEvent(val value: Int) : Event()

data class AddOneCommand(val value: Int) : Command()

data class IntState(val value: Int)

class Example(
    id: AggregateId = "example",
    initialState: IntState,
    persistence: Persistence = InMemoryPersistence()
) : Aggregate<IntState>(id, initialState, persistence) {

    override fun routeEvent(event: Event): IntState {
        return when (event) {
            is OneAddedEvent -> handleOneAddedEvent(event)
            else -> throw Exception()
        }
    }

    override fun onStateChange(newState: IntState, oldState: IntState) {
        println("new newState is $newState")
    }

    override fun routeCommand(command: Command): Event {
        println("got a command $command")
        return when (command) {
            is AddOneCommand -> emitOneAddedEvent(command)
            else -> throw Exception()
        }
    }

    override fun onEvent(event: Event) {
        println("$event received in aggregate")
    }

    private fun handleOneAddedEvent(event: OneAddedEvent): IntState {
        return state.copy(value = state.value + event.value)
    }

    private fun emitOneAddedEvent(command: AddOneCommand): OneAddedEvent {
        require(command.value > 0) { "value must be positive" }
        return OneAddedEvent(command.value)
    }
}
