package dk.cygni.carlsmoviesearchservice.domain.events.user

data class UserUpdatedEvent (
    val userId: Long,
    val username: String
) : UserEvent(userId)