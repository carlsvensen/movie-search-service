package dk.cygni.carlsmoviesearchservice.projection

import dk.cygni.carlsmoviesearchservice.domain.Movie
import dk.cygni.carlsmoviesearchservice.domain.applyRatingUpdate
import dk.cygni.carlsmoviesearchservice.domain.applyUpdate
import dk.cygni.carlsmoviesearchservice.domain.events.movie.MovieEvent
import dk.cygni.carlsmoviesearchservice.domain.events.movie.MovieCreatedEvent
import dk.cygni.carlsmoviesearchservice.domain.events.movie.MovieUpdateRatingEvent
import dk.cygni.carlsmoviesearchservice.queries.MovieByGenreQuery
import dk.cygni.carlsmoviesearchservice.queries.MovieByTconstQuery
import dk.cygni.carlsmoviesearchservice.queries.MovieByTextQuery
import dk.cygni.carlsmoviesearchservice.repository.elasticsearch.MovieRepository
import dk.cygni.carlsmoviesearchservice.repository.mongodb.MovieReadRepository
import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Component

@Component
class MovieProjector(
    private val movieReadRepository: MovieReadRepository,
    private val movieRepository: MovieRepository
) {

    @JmsListener(destination = "\${dk.cygni.carlsmoviesearchservice.queuename.movie}")
    fun queueListener(tconst: String) {
        movieRepository.save(project(MovieByTconstQuery(tconst)))
    }

    fun project(movieByTextQuery: MovieByTextQuery): List<Movie> {
        var allMovieEvents = movieReadRepository.findByWildcardText(movieByTextQuery.searchString)

        if (allMovieEvents.isNotEmpty()) {
            allMovieEvents =
                allMovieEvents
                    .map { movieEvent -> movieEvent.tconst }
                    .toSet()
                    .map { tconst -> movieReadRepository.findByTconst(tconst) }
                    .flatten()

            return assembleMovies(allMovieEvents)

        } else {
            throw IllegalArgumentException("No movies found with search string '${movieByTextQuery.searchString}'")
        }
    }

    fun project(movieByTconstQuery: MovieByTconstQuery): Movie {
        val allMovieEvents = movieReadRepository.findByTconst(movieByTconstQuery.tconst)

        if (allMovieEvents.isNotEmpty()) {
            return assembleMovies(allMovieEvents)[0]

        } else {
            throw IllegalArgumentException("No movie with tconst ${movieByTconstQuery.tconst}")
        }
    }

    fun project(movieByGenreQuery: MovieByGenreQuery): List<Movie> {
        val allMovieEvents = movieByGenreQuery.genre
            .map { genre -> movieReadRepository.findByGenre(genre, 1) }
            .map { event -> movieReadRepository.findByTconst(event.tconst) }
            .flatten()

        return assembleMovies(allMovieEvents)
    }

    private fun assembleMovies(movieEvents: List<MovieEvent>): List<Movie> {
        val eventsGroupedByTconst: Map<String, List<MovieEvent>> = movieEvents.groupBy { it.tconst }

        val allMovies = mutableListOf<Movie>()
        eventsGroupedByTconst.keys.forEach { tconst ->
            val movie = Movie()
            eventsGroupedByTconst[tconst]!!.sortedBy { it.created }
                .forEach { movieEvent ->
                    when (movieEvent) {
                        is MovieCreatedEvent -> movie.applyUpdate(movieEvent)
                        is MovieUpdateRatingEvent -> movie.applyRatingUpdate(movieEvent)
                    }
                }
            allMovies.add(movie)
        }

        return allMovies
    }
}