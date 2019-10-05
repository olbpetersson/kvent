import kotlinx.coroutines.runBlocking
import se.olapetersson.AddOneCommand
import se.olapetersson.Example
import se.olapetersson.IntState

fun main() = runBlocking {
    val myExample = Example(_id = "myId", state = IntState(0))
    val communicationPath = myExample.create()

    for (i in (1..10)) {
        communicationPath.send(AddOneCommand(i))
    }

    myExample.replay()
}
