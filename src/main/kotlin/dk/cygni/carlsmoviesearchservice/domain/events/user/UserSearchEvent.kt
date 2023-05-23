package dk.cygni.carlsmoviesearchservice.domain.events.user

data class UserSearchEvent(
    val userId: Long,
    val searchString: String,
    val genreResult: Map<String, Int> = mutableMapOf()
) : UserEvent(userId)