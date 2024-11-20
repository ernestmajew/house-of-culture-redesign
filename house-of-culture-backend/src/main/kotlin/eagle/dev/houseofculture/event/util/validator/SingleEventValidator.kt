package eagle.dev.houseofculture.event.util.validator

import eagle.dev.houseofculture.event.model.Event
import eagle.dev.houseofculture.event.model.SingleEvent
import eagle.dev.houseofculture.exceptions.UnprocessableEntityException
import eagle.dev.houseofculture.openapi.model.EditSingleEventRequestTs
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component
class SingleEventValidator {

    companion object {
        fun validate(request: EditSingleEventRequestTs) =
            request.let {
                validateTime(request)
            }

        private fun validateTime(request: EditSingleEventRequestTs) {
            if (request.startTime.isAfter(request.endTime))
                throw UnprocessableEntityException("Start time must be before end time.")

            if (OffsetDateTime.now().isAfter(request.startTime))
                throw UnprocessableEntityException("Start time must be before current time.")
        }

        fun checkCollisions(event: Event, updatedSingleEvent: SingleEvent, instructorSingleEvents: List<SingleEvent>) {
            val singleEvents = event.singleEvents.toMutableList()

            singleEvents.removeIf { it.id == updatedSingleEvent.id }

            singleEvents.add(updatedSingleEvent)

            ActivityValidator.validateCollisions(instructorSingleEvents, singleEvents)
        }
    }
}