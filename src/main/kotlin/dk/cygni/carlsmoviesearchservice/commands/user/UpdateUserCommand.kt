package dk.cygni.carlsmoviesearchservice.commands.user

import dk.cygni.carlsmoviesearchservice.domain.events.user.UserUpdatedEvent

data class UpdateUserCommand(
    val userid: Long,
    val newUsername: String
)

fun UpdateUserCommand.toUserUpdatedEvent(): UserUpdatedEvent = UserUpdatedEvent(userid, newUsername)
