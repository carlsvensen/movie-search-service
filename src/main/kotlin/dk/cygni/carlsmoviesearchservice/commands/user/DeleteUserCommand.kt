package dk.cygni.carlsmoviesearchservice.commands.user

import dk.cygni.carlsmoviesearchservice.domain.events.user.UserDeletedEvent

data class DeleteUserCommand(
    val userid: Long
)

fun DeleteUserCommand.toUserDeletedEvent(): UserDeletedEvent = UserDeletedEvent(userid)
