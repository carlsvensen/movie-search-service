package dk.cygni.carlsmoviesearchservice.commands.user

import dk.cygni.carlsmoviesearchservice.domain.events.user.UserCreatedEvent

data class CreateUserCommand(
    val username: String
)

fun CreateUserCommand.toUserCreatedEvent(userId: Long): UserCreatedEvent = UserCreatedEvent(userId, username)
