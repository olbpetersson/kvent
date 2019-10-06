package io.github.olbpetersson.kvent.persistence

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import io.github.olbpetersson.kvent.model.AggregateId
import io.github.olbpetersson.kvent.model.Event
import java.util.LinkedList

class InMemoryPersistence : Persistence {

    private val lock = Mutex()
    private val tables = mutableMapOf<AggregateId, LinkedList<Event>>()
    override suspend fun persistForId(e: Event, id: AggregateId) {
        lock.withLock {
            tables.getOrDefault(id, LinkedList())
                .add(e)
        }
    }

    override suspend fun getEventsForId(id: AggregateId): LinkedList<Event>? {
        return lock.withLock {
            tables[id]
        }
    }

}
