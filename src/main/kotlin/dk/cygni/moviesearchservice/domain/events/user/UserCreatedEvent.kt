package dk.cygni.moviesearchservice.domain.events.user

data class UserCreatedEvent(
    val userId: Long,
    val username: String
) : UserEvent(userId)