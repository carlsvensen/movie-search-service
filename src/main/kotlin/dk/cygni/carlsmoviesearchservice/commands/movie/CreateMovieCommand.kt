package dk.cygni.carlsmoviesearchservice.commands.movie

import dk.cygni.carlsmoviesearchservice.domain.events.movie.MovieCreatedEvent

data class CreateMovieCommand(
    val tconst: String,
    val titleType: String,
    val primaryTitle: String,
    val originalTitle: String,
    val isAdult: Boolean,
    val startYear: String,
    val endYear: String,
    val runtimeMinutes: Int?,
    val genres: List<String>
)

fun CreateMovieCommand.toMovieEvent(): MovieCreatedEvent =
    MovieCreatedEvent(tconst, titleType, primaryTitle, originalTitle, isAdult, startYear, endYear, runtimeMinutes, genres)
