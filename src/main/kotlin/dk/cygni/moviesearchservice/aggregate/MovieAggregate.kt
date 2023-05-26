package dk.cygni.moviesearchservice.aggregate

import dk.cygni.moviesearchservice.commands.movie.CreateMovieCommand
import dk.cygni.moviesearchservice.commands.movie.UpdateRatingCommand
import dk.cygni.moviesearchservice.commands.movie.toMovieEvent
import dk.cygni.moviesearchservice.commands.movie.toRatingEvent
import dk.cygni.moviesearchservice.domain.events.movie.MovieCreatedEvent
import dk.cygni.moviesearchservice.domain.events.movie.MovieUpdateRatingEvent
import dk.cygni.moviesearchservice.repository.mongodb.MovieReadRepository
import dk.cygni.moviesearchservice.repository.mongodb.MovieWriteRepository
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