package dk.cygni.moviesearchservice.commands.user

import dk.cygni.moviesearchservice.domain.events.user.UserUpdatedEvent

data class UpdateUserCommand(
    val userid: Long,
    val newUsername: String
)

fun UpdateUserCommand.toUserUpdatedEvent(): UserUpdatedEvent = UserUpdatedEvent(userid, newUsername)
