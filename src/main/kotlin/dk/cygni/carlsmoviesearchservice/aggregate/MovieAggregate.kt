package dk.cygni.carlsmoviesearchservice.aggregate

import dk.cygni.carlsmoviesearchservice.commands.CreateMovieCommand
import dk.cygni.carlsmoviesearchservice.commands.UpdateRatingCommand
import dk.cygni.carlsmoviesearchservice.commands.toMovieEvent
import dk.cygni.carlsmoviesearchservice.commands.toRatingEvent
import dk.cygni.carlsmoviesearchservice.domain.events.movie.MovieUpdateRatingEvent
import dk.cygni.carlsmoviesearchservice.repository.mongodb.MovieReadRepository
import dk.cygni.carlsmoviesearchservice.repository.mongodb.MovieWriteRepository
import mu.KotlinLogging
import org.springframework.stereotype.Component

@Component
class MovieAggregate(
    val movieReadRepository: MovieReadRepository,
    val movieWriteRepository: MovieWriteRepository
) {
    private val logger = KotlinLogging.logger {}

    fun handleUpdateMovie(createMovieCommand: CreateMovieCommand) {
        val existingMovieEvents = movieReadRepository.findByTconst(createMovieCommand.tconst)
        val newMovieEvent = createMovieCommand.toMovieEvent()

        when {
            existingMovieEvents.isEmpty() -> movieWriteRepository.insert(newMovieEvent)
            existingMovieEvents.maxByOrNull { it.created }!! != newMovieEvent -> movieWriteRepository.insert(newMovieEvent)
            else -> logger.info { "No movie changes for ${newMovieEvent.tconst}" }
        }
    }

    fun handleUpdateRating(updateRatingCommand: UpdateRatingCommand) {
        val existingRatingEvents = movieReadRepository.findByTconst(updateRatingCommand.tconst)
        val newRatingEvent = updateRatingCommand.toRatingEvent()

        when {
            existingRatingEvents.isEmpty() -> movieWriteRepository.insert(newRatingEvent)
            existingRatingEvents.filterIsInstance<MovieUpdateRatingEvent>()
                .maxByOrNull { it.created }!! != newRatingEvent -> movieWriteRepository.insert(newRatingEvent)
            else -> logger.info { "No rating changes for ${newRatingEvent.tconst}" }
        }
    }
}