package dk.cygni.moviesearchservice.commands.user

import dk.cygni.moviesearchservice.domain.events.user.UserCreatedEvent

data class CreateUserCommand(
    val username: String
)

fun CreateUserCommand.toUserCreatedEvent(userId: Long): UserCreatedEvent = UserCreatedEvent(userId, username)
