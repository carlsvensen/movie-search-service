package dk.cygni.moviesearchservice.domain

import dk.cygni.moviesearchservice.domain.events.movie.MovieCreatedEvent
import dk.cygni.moviesearchservice.domain.events.movie.MovieUpdateRatingEvent
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document

@Document(indexName = "movies")
data class Movie(
    @Id
    var tconst: String? = null,
    var titleType: String? = null,
    var primaryTitle: String? = null,
    var originalTitle: String? = null,
    var isAdult: Boolean = false,
    var startYear: String? = null,
    var endYear: String? = null,
    var runtimeMinutes: Int? = null,
    var genres: List<String>? = null,
    var averageRating: Double? = null,
    var numOfVotes: Long? = null
)

fun Movie.applyUpdate(event: MovieCreatedEvent) {
    tconst = event.tconst
    titleType = event.titleType
    primaryTitle = event.primaryTitle
    originalTitle = event.originalTitle
    isAdult = event.isAdult
    startYear = event.startYear
    endYear = event.endYear
    runtimeMinutes = event.runtimeMinutes
    genres = event.genres
}

fun Movie.applyRatingUpdate(event: MovieUpdateRatingEvent) {
    averageRating = event.averageRating
    numOfVotes = event.numOfVotes
}