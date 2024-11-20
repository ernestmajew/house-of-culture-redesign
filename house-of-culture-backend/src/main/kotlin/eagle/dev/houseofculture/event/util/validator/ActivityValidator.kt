package eagle.dev.houseofculture.event.util.validator

import eagle.dev.houseofculture.event.model.SingleEvent
import eagle.dev.houseofculture.exceptions.ConflictException
import eagle.dev.houseofculture.exceptions.UnprocessableEntityException
import eagle.dev.houseofculture.openapi.model.CreateActivityRequestTs
import eagle.dev.houseofculture.openapi.model.EditActivityRequestTs
import eagle.dev.houseofculture.user.model.Child
import eagle.dev.houseofculture.user.model.Client
import eagle.dev.houseofculture.user.model.User
import eagle.dev.houseofculture.user.model.enumeration.UserRole

class ActivityValidator {
    companion object {
        fun validate(request: CreateActivityRequestTs) =
            request.let {
                validateOccurrences(it)
                validateMinMaxAge(it.minimumAge, it.maximumAge)
            }

        fun validate(request: EditActivityRequestTs) =
            request.let {
                validateOccurrences(it)
                validateMinMaxAge(it.minimumAge, it.maximumAge)
            }

        private fun validateOccurrences(request: CreateActivityRequestTs) {
            if(request.occurences.any { it.startTime.isAfter(it.endTime) })
                throw UnprocessableEntityException("Start time must be before end time.")
        }

        private fun validateOccurrences(request: EditActivityRequestTs) {
            if(request.occurences.any { it.startTime.isAfter(it.endTime) })
                throw UnprocessableEntityException("Start time must be before end time.")
        }

        private fun validateMinMaxAge(minimumAge: Int?, maximumAge: Int?) {
            if (minimumAge != null && maximumAge != null && minimumAge > maximumAge)
                throw UnprocessableEntityException("Minimum age cannot be bigger than maximum age.")
        }

        fun validateCollisions(instructorEvents: List<SingleEvent>, singleEvents: List<SingleEvent>) {
            checkCollisions(singleEvents, "New occurrences include time collisions.")

            val allEvents = instructorEvents.plus(singleEvents)
            checkCollisions(allEvents, "Occurrences include instructor activities time collisions.")
        }

        fun validateInstructor(instructor: User): Client {
            if (instructor is Child)
                throw ConflictException("Child cannot be an instructor!")

            if (!instructor.role.getAllRoles().contains(UserRole.INSTRUCTOR))
                throw ConflictException("User don't have instructor role!")

            return instructor as Client
        }

        // list containing <startTime, endTime> of single events
        private fun checkCollisions(events: List<SingleEvent>, errorMessage: String) {
            val sorted = events.map { it.starts to it.ends }.sortedBy { it.first }

            for (i in 0 until sorted.size - 1) {
                val currentEndTime = sorted[i].second
                val nextStartTime = sorted[i + 1].first

                if (currentEndTime.isAfter(nextStartTime) || currentEndTime == nextStartTime) {
                    throw UnprocessableEntityException(errorMessage)
                }
            }
        }
    }
}