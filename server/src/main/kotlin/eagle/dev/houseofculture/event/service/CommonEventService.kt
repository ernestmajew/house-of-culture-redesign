package eagle.dev.houseofculture.event.service

import eagle.dev.houseofculture.event.model.Event
import eagle.dev.houseofculture.event.repository.EventRepository
import eagle.dev.houseofculture.exceptions.ObjectNotFoundException
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrElse

@Service
class CommonEventService(
    private val eventRepository: EventRepository
) {
    fun getEvent(id: Long): Event =
        eventRepository
            .findById(id)
            .getOrElse { throw ObjectNotFoundException("There is no event with id $id") }
}