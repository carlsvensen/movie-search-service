package dk.cygni.carlsmoviesearchservice.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import dk.cygni.carlsmoviesearchservice.domain.events.user.UserCreatedEvent
import dk.cygni.carlsmoviesearchservice.domain.events.user.UserSearchEvent
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
}

fun User.applyUserCreatedEvent(event: UserCreatedEvent) {
    userid = event.userid
    username = event.username
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
            //.map { it.first } // TODO: Kanskje dette fungerer ???
            .toMap().keys // FIXME: Burde ikke være nødvendig med flere toList
            .toList()

    if (favoriteGenres.size > 3) {
        favoriteGenres = favoriteGenres.subList(0, 3)
    }
}
