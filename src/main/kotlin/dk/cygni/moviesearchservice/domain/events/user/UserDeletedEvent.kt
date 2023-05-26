package dk.cygni.moviesearchservice.domain.events.user

data class UserDeletedEvent(
    val userId: Long
) : UserEvent(userId)