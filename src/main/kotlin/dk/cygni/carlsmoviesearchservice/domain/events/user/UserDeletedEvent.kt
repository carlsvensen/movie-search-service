package dk.cygni.carlsmoviesearchservice.domain.events.user

data class UserDeletedEvent(
    val userId: Long
) : UserEvent(userId)