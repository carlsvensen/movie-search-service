package dk.cygni.moviesearchservice.domain.events.movie

data class MovieUpdateRatingEvent(
    val tConst: String,
    val averageRating: Double,
    val numOfVotes: Long
) : MovieEvent(tConst)