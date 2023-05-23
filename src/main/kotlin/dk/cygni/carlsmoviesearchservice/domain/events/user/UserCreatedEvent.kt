package dk.cygni.carlsmoviesearchservice.domain.events.user

data class UserCreatedEvent(
    val userId: Long,
    val username: String
) : UserEvent(userId)