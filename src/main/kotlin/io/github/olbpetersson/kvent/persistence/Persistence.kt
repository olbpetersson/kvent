package io.github.olbpetersson.kvent.persistence

import io.github.olbpetersson.kvent.model.AggregateId
import io.github.olbpetersson.kvent.model.Event
import java.util.Queue

interface Persistence {
    suspend fun persistForId(e: Event, id: AggregateId)
    suspend fun getEventsForId(id: AggregateId): Queue<Event>?
}
