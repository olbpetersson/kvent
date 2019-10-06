package se.olapetersson.kvent.persistence

import se.olapetersson.kvent.model.AggregateId
import se.olapetersson.kvent.model.Event
import java.util.Queue

interface Persistence {
    suspend fun persistForId(e: Event, id: AggregateId)
    suspend fun getEventsForId(id: AggregateId): Queue<Event>?
}
