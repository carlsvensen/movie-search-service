package dk.cygni.moviesearchservice.service

import dk.cygni.moviesearchservice.aggregate.UserAggregate
import dk.cygni.moviesearchservice.domain.Movie
import dk.cygni.moviesearchservice.domain.User
import dk.cygni.moviesearchservice.domain.events.user.UserSearchEvent
import dk.cygni.moviesearchservice.queries.MovieByTconstQuery
import dk.cygni.moviesearchservice.queries.MovieByTextQuery
import dk.cygni.moviesearchservice.queries.MovieSuggestionQuery
import dk.cygni.moviesearchservice.queries.UserByUsernameQuery
import dk.cygni.moviesearchservice.repository.elasticsearch.MovieRepository
import dk.cygni.moviesearchservice.repository.elasticsearch.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class SearchService(
    private val userAggregate: UserAggregate,
    private val movieRepository: MovieRepository,
    private val userRepository: UserRepository
) {

    fun listAllUsers(): List<User> = userRepository.findAll().toList()

    fun searchForUser(userByUsernameQuery: UserByUsernameQuery): User =
        userRepository.findUserByUsername(userByUsernameQuery.username)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "No user with username ${userByUsernameQuery.username} exists.") }

    fun searchForMovieByTitle(movieByTextQuery: MovieByTextQuery): List<Movie> =
        movieRepository.findMovieByTitle(movieByTextQuery.searchString)
            .ifEmpty { throw ResponseStatusException(HttpStatus.NOT_FOUND, "No titles match ${movieByTextQuery.searchString}")}
            .also { createSearchEvent(movieByTextQuery.userid, movieByTextQuery.searchString, it) }

    fun searchForMovieOnEveryting(movieByTextQuery: MovieByTextQuery): List<Movie> =
        movieRepository.findMovieBySearchOnEverything(movieByTextQuery.searchString, PageRequest.of(0, 1000, Sort.unsorted()))
            .ifEmpty { throw ResponseStatusException(HttpStatus.NOT_FOUND, "No data match ${movieByTextQuery.searchString}") }
            .also { createSearchEvent(movieByTextQuery.userid, movieByTextQuery.searchString, it) }

    fun searchForMovieByTconst(movieByTconstQuery: MovieByTconstQuery): Movie =
        movieRepository.findMovieByTconst(movieByTconstQuery.tconst)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "No title found for tconst ${movieByTconstQuery.tconst}")}
            .also { createSearchEvent(movieByTconstQuery.userid, movieByTconstQuery.tconst, listOf(it)) }

    fun getSuggestion(movieSuggestionQuery: MovieSuggestionQuery): List<Movie> {
        val user = userRepository.findUserByUserid(movieSuggestionQuery.userid)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "No user with userid ${movieSuggestionQuery.userid} found") }

        if (user.favoriteGenres.isEmpty()) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "You have no search history! Make som searches before asking for suggestions.")
        }

        return mutableListOf<Movie>().also {
            for (i in user.favoriteGenres.indices) {
                it.add(
                    movieRepository.findRandomMovieByGenreAndMinRating(user.favoriteGenres[i], 8)
                        .orElse(movieRepository.findRandomMovieByGenreAndMinRating(user.favoriteGenres[i], 5)
                            .orElse(null)))
            }
        }
    }

    private fun createSearchEvent(userId: Long, searchString: String, searchResults: List<Movie>) {
        userAggregate.handleUserSearchEvent(
            UserSearchEvent(userId, searchString, createSearhedGenresResultMap(searchResults)))
    }

    private fun createSearhedGenresResultMap(searchResults: List<Movie>): Map<String, Int> {
        val searchedGenres = mutableMapOf<String, Int>()
        searchResults.forEach { searchResult -> addOrIncrement(searchedGenres, searchResult.genres) }

        return searchedGenres
    }

    private fun addOrIncrement(targetMap: MutableMap<String, Int>, keys: List<String>?) {
        keys?.forEach {
            targetMap.putIfAbsent(it, 0)
            targetMap[it] = targetMap[it]!! + 1
        }
    }
}