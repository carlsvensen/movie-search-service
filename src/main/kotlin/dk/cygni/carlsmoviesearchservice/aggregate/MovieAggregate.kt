package dk.cygni.carlsmoviesearchservice.aggregate

import dk.cygni.carlsmoviesearchservice.commands.movie.CreateMovieCommand
import dk.cygni.carlsmoviesearchservice.commands.movie.UpdateRatingCommand
import dk.cygni.carlsmoviesearchservice.commands.movie.toMovieEvent
import dk.cygni.carlsmoviesearchservice.commands.movie.toRatingEvent
import dk.cygni.carlsmoviesearchservice.domain.events.movie.MovieCreatedEvent
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

    fun handleCreateMovieCommand(createMovieCommand: CreateMovieCommand) {
        val existingMovieCreatedEvents =
            movieReadRepository.findByTconst(createMovieCommand.tconst)
                .filterIsInstance<MovieCreatedEvent>()
        val newMovieEvent = createMovieCommand.toMovieEvent()

        when {
            existingMovieCreatedEvents.isEmpty() ->
                movieWriteRepository.insert(newMovieEvent)

            else ->
                throw IllegalStateException("Duplicate createMovieCommand for tconst ${createMovieCommand.tconst}")
        }
    }

    fun handleUpdateRatingCommand(updateRatingCommand: UpdateRatingCommand) {
        val existingMovieUpdateRatingEvent =
            movieReadRepository.findByTconst(updateRatingCommand.tconst)
                .filterIsInstance<MovieUpdateRatingEvent>()
        val newRatingEvent = updateRatingCommand.toRatingEvent()

        when {
            existingMovieUpdateRatingEvent.isEmpty() ->
                movieWriteRepository.insert(newRatingEvent)

            existingMovieUpdateRatingEvent.maxByOrNull { it.created } != newRatingEvent ->
                movieWriteRepository.insert(newRatingEvent)

            else ->
                logger.debug { "No rating changes for ${newRatingEvent.tconst}" }
        }
    }
}