package eagle.dev.houseofculture.event.util.validator

import eagle.dev.houseofculture.event.model.Event
import eagle.dev.houseofculture.exceptions.ForbiddenException
import eagle.dev.houseofculture.user.model.User
import eagle.dev.houseofculture.user.model.enumeration.UserRole


class InstructorValidator {
    companion object {
        fun validate(user: User, event: Event) =
            validateIfUserIsInstructorOfEventOrHasRightRole(user, event)

        fun validate(user: User) =
            ifUserIsInstructor(user)

        private fun validateIfUserIsInstructorOfEventOrHasRightRole(user: User, event: Event) {
            if (event.instructor!!.id != user.id && !(user.role.getAllRoles().contains(UserRole.EMPLOYEE))) {
                throw ForbiddenException("User with id ${user.id} is not instructor of event with id ${event.id}.")
            }
        }

        private fun ifUserIsInstructor(user: User) {
            if (!user.role.getAllRoles().contains(UserRole.INSTRUCTOR)) {
                throw ForbiddenException("User with id ${user.id} is not instructor.")
            }
        }
    }
}