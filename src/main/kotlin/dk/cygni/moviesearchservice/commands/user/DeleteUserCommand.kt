package dk.cygni.moviesearchservice.commands.user

import dk.cygni.moviesearchservice.domain.events.user.UserDeletedEvent

data class DeleteUserCommand(
    val userid: Long
)

fun DeleteUserCommand.toUserDeletedEvent(): UserDeletedEvent = UserDeletedEvent(userid)
