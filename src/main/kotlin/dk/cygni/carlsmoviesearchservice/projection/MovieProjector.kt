package dk.cygni.carlsmoviesearchservice.projection

import dk.cygni.carlsmoviesearchservice.config.MOVIE_QUEUE
import dk.cygni.carlsmoviesearchservice.domain.Movie
import dk.cygni.carlsmoviesearchservice.domain.applyRatingUpdate
import dk.cygni.carlsmoviesearchservice.domain.applyUpdate
import dk.cygni.carlsmoviesearchservice.domain.events.movie.MovieCreatedEvent
import dk.cygni.carlsmoviesearchservice.domain.events.movie.MovieUpdateRatingEvent
import dk.cygni.carlsmoviesearchservice.repository.elasticsearch.MovieRepository
import dk.cygni.carlsmoviesearchservice.repository.mongodb.MovieReadRepository
import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Component

@Component
class MovieProjector(
    private val movieReadRepository: MovieReadRepository,
    private val movieRepository: MovieRepository
) {

    @JmsListener(destination = MOVIE_QUEUE)
    fun queueListener(tconst: String) {
        movieRepository.save(project(tconst))
    }

    fun project(tconst: String): Movie =
        Movie().also {
            movieReadRepository.findByTconst(tconst)
                .ifEmpty { throw IllegalStateException("No movie with tconst $tconst found") }
                .sortedBy { it.created }
                .forEach { movieEvent ->
                    when (movieEvent) {
                        is MovieCreatedEvent -> it.applyUpdate(movieEvent)
                        is MovieUpdateRatingEvent -> it.applyRatingUpdate(movieEvent)
                    }
                }
        }

}