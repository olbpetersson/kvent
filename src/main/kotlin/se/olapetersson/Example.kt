package se.olapetersson

data class OneAddedEvent(val value: Int) : Event()

data class AddOneCommand(val value: Int) : Command()

data class IntState(val value: Int) : State()

class Example(override val _id: String = "example", override var state: IntState) : Aggregate() {
    override fun routeEvent(event: Event): IntState {
        return when (event) {
            is OneAddedEvent -> handleOneAddedEvent(event)
            else -> throw Exception()
        }
    }

    private fun handleOneAddedEvent(event: OneAddedEvent): IntState {
        return state.copy(value = state.value + event.value)
    }

    override fun onStateChange(state: State) {
        println("new state is $state")
    }

    override fun routeCommand(command: Command): Event {
        return when (command) {
            is AddOneCommand -> emitOneAddedEvent(command)
            else -> throw Exception()
        }
    }

    override fun onEvent(event: Any) {
        println("$event received in aggregate")
    }

    private fun emitOneAddedEvent(command: AddOneCommand): OneAddedEvent {
        require(command.value > 0) { "value must be positive" }
        return OneAddedEvent(command.value)
    }
}
