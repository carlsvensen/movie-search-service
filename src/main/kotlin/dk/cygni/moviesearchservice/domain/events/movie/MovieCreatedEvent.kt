package dk.cygni.moviesearchservice.domain.events.movie

data class MovieCreatedEvent(
    val tConst: String,
    val titleType: String,
    val primaryTitle: String,
    val originalTitle: String,
    val isAdult: Boolean,
    val startYear: String,
    val endYear: String?,
    val runtimeMinutes: Int?,
    val genres: List<String>
) : MovieEvent(tConst)