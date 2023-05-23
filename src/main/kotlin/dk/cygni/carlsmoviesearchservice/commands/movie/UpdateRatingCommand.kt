package dk.cygni.carlsmoviesearchservice.commands.movie

import dk.cygni.carlsmoviesearchservice.domain.events.movie.MovieUpdateRatingEvent

data class UpdateRatingCommand(
    val tconst: String,
    val averageRating: Double,
    val numOfVotes: Long
)

fun UpdateRatingCommand.toRatingEvent(): MovieUpdateRatingEvent =
    MovieUpdateRatingEvent(tconst, averageRating, numOfVotes)
