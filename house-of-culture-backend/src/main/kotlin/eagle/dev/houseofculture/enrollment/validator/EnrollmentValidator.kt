package eagle.dev.houseofculture.enrollment.validator

import eagle.dev.houseofculture.event.model.Event
import eagle.dev.houseofculture.event.model.SingleEvent
import eagle.dev.houseofculture.exceptions.ConflictException
import eagle.dev.houseofculture.exceptions.ForbiddenException
import eagle.dev.houseofculture.exceptions.UnprocessableEntityException
import eagle.dev.houseofculture.openapi.model.EnrollmentAvailabilityStatusTs
import eagle.dev.houseofculture.user.model.Client
import eagle.dev.houseofculture.user.model.User
import java.time.LocalDate

class EnrollmentValidator {
    companion object {
        fun hasUserPermissionToEnroll(loggedInUser: User, userIdToEnroll: Long): Client {
            if (loggedInUser !is Client) {
                throw ForbiddenException("Child cannot enroll in event.")
            }

            if (loggedInUser.id != userIdToEnroll
                && !loggedInUser.children.map { it.id }.contains(userIdToEnroll)
            ) {
                throw ForbiddenException("User can enroll only itself or its child.")
            }

            return loggedInUser
        }

        fun validateUserAgeWithStatus(event: Event, user: User): EnrollmentAvailabilityStatusTs {
            val userAge = user.userAgeInYears

            if(event.minimumAge != null && event.minimumAge!! > userAge)
                return EnrollmentAvailabilityStatusTs.TOO_YOUNG

            if(event.maximumAge != null && event.maximumAge!! < userAge)
                return EnrollmentAvailabilityStatusTs.TOO_OLD

            return EnrollmentAvailabilityStatusTs.AVAILABLE
        }

        fun validateUserAge(event: Event, user: User) {
            if(validateUserAgeWithStatus(event, user) != EnrollmentAvailabilityStatusTs.AVAILABLE)
                throw ConflictException(
                    "User (id: ${user.id} with age ${user.userAgeInYears} doesn't meet event (id: ${event.id}) age restrictions"
                )
        }

        fun canEnrollToSelectedSingleEvents(singleEvents: List<SingleEvent>, numberToEnroll: Int?) {
            if (singleEvents.isEmpty() || (numberToEnroll != null && numberToEnroll > singleEvents.size))
                throw UnprocessableEntityException(
                    "Cannot enroll in more single events than is available for given user " +
                            "(number to enroll: ${numberToEnroll}, single events: ${singleEvents.size})"
                )

            if(singleEvents.any { it.availablePlaces == 0 })
                throw ConflictException("No available places in one of single events.")
        }

        fun hasUserPermissionToPay(user: User) {
            if (user !is Client) {
                throw ForbiddenException("Child cannot pay for enrollment.")
            }
        }

        fun hasUserPermissionToPay(loggedInUser: User, usersIdsToPayFor: List<Long>) {
            hasUserPermissionToPay(loggedInUser)

            val loggedInUserChildrenIds = (loggedInUser as Client).children.map { it.id!! }

            if (usersIdsToPayFor.any {
                it != loggedInUser.id!! && !loggedInUserChildrenIds.contains(it)
            })
                throw ForbiddenException("User can only pay for himself and his children.")
        }

        fun validatePeriodicPaymentRange(start: LocalDate, end: LocalDate) {
            if(start.isAfter(end)) throw UnprocessableEntityException("Start date cannot by after end date")
        }
    }
}