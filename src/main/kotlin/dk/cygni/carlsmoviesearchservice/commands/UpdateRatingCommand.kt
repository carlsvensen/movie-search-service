package dk.cygni.carlsmoviesearchservice.commands

import dk.cygni.carlsmoviesearchservice.domain.events.movie.MovieUpdateRatingEvent

data class UpdateRatingCommand(
    val tconst: String,
    val averageRating: Double,
    val numOfVotes: Long
)

fun UpdateRatingCommand.toRatingEvent(): MovieUpdateRatingEvent {
    return MovieUpdateRatingEvent(tconst, averageRating, numOfVotes)
}