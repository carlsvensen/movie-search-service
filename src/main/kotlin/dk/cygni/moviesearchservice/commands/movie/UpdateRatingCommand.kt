package dk.cygni.moviesearchservice.commands.movie

import dk.cygni.moviesearchservice.domain.events.movie.MovieUpdateRatingEvent

data class UpdateRatingCommand(
    val tconst: String,
    val averageRating: Double,
    val numOfVotes: Long
)

fun UpdateRatingCommand.toRatingEvent(): MovieUpdateRatingEvent =
    MovieUpdateRatingEvent(tconst, averageRating, numOfVotes)
