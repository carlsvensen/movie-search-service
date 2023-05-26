package dk.cygni.moviesearchservice.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import dk.cygni.moviesearchservice.domain.events.user.UserCreatedEvent
import dk.cygni.moviesearchservice.domain.events.user.UserSearchEvent
import dk.cygni.moviesearchservice.domain.events.user.UserUpdatedEvent
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.elasticsearch.annotations.Document

@Document(indexName = "users")
class User {
    @Id
    var userid: Long? = null
    var username: String? = null
    var favoriteGenres: List<String> = mutableListOf()
    var searchHistory: List<String> = mutableListOf()

    @JsonIgnore
    @Transient
    var allSearchedGenres: MutableMap<String, Int> = mutableMapOf()

    @JsonIgnore
    @Transient
    var deleted: Boolean = false
}

fun User.applyUserCreatedEvent(event: UserCreatedEvent) {
    userid = event.userid
    username = event.username
}

fun User.applyUserUpdatedEvent(event: UserUpdatedEvent) {
    username = event.username
}

fun User.applyUserDeletedEvent() {
    deleted = true
}

fun User.applyUserSearchEvent(event: UserSearchEvent) {
    searchHistory += event.searchString
    event.genreResult.forEach {
        when (val count = allSearchedGenres[it.key]) {
            null -> allSearchedGenres[it.key] = 1
            else -> allSearchedGenres[it.key] = count + it.value
        }
    }
}

fun User.filterFavouriteGenres() {
    favoriteGenres =
        allSearchedGenres
            .toList()
            .sortedByDescending { it.second }
            .map { it.first }

    if (favoriteGenres.size > 3) {
        favoriteGenres = favoriteGenres.subList(0, 3)
    }
}
