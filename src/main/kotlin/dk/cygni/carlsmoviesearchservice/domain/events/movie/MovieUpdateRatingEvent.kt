package dk.cygni.carlsmoviesearchservice.domain.events.movie

data class MovieUpdateRatingEvent(
    var tConst: String,
    val averageRating: Double,
    val numOfVotes: Long
) : MovieEvent(tConst)