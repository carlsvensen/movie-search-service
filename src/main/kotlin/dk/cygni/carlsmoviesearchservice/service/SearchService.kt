package dk.cygni.carlsmoviesearchservice.service

import dk.cygni.carlsmoviesearchservice.aggregate.UserAggregate
import dk.cygni.carlsmoviesearchservice.domain.Movie
import dk.cygni.carlsmoviesearchservice.domain.events.user.UserSearchEvent
import dk.cygni.carlsmoviesearchservice.queries.MovieByTconstQuery
import dk.cygni.carlsmoviesearchservice.queries.MovieByTextQuery
import dk.cygni.carlsmoviesearchservice.repository.elasticsearch.MovieRepository
import dk.cygni.carlsmoviesearchservice.repository.elasticsearch.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class SearchService(
    private val userAggregate: UserAggregate,
    private val movieRepository: MovieRepository,
    private val userRepository: UserRepository) {

    fun searchForMovieByTitle(userId: Long, movieByTextQuery: MovieByTextQuery): List<Movie> =
        movieRepository.findMovieByTitle(movieByTextQuery.searchString)
            .also { createSearchEvent(userId, movieByTextQuery.searchString, it) }

    fun searchForMovieOnEveryting(userId: Long, movieByTextQuery: MovieByTextQuery): List<Movie> =
        movieRepository.findMovieBySearchOnEverything(movieByTextQuery.searchString, PageRequest.of(0, 1000, Sort.unsorted()))
            .also { createSearchEvent(userId, movieByTextQuery.searchString, it) }

    fun searchForMovieByTconst(userId: Long, movieByTconstQuery: MovieByTconstQuery): Movie =
        movieRepository.findMovieByTconst(movieByTconstQuery.tconst)
            .also { createSearchEvent(userId, movieByTconstQuery.tconst, listOf(it)) }

    fun getSuggestion(userid: Long): List<Movie> {
        val user = userRepository.findUserByUserid(userid)
            //.orElseThrow { IllegalArgumentException("No user with userid $userid found") }
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "No user with userid $userid found") }

        if (user.favoriteGenres.isEmpty()) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "You have no search history! Make som searches before asking for suggestions.")
            //throw IllegalArgumentException("You have no search history! Make som searches before asking for suggestions.")
        }

        val result = mutableListOf<Movie>()
        for (i in user.favoriteGenres.indices) {
            result.add(movieRepository.findRandomMovieByGenreAndMinRating(user.favoriteGenres[i], 8))
        }

        return result
    }

    private fun createSearchEvent(userId: Long, searchString: String, searchResults: List<Movie>) {
        val searchedGenres = mutableMapOf<String, Int>() //TODO: Under her bør det være .map { }
        searchResults.forEach { searchResult -> increment(searchedGenres, searchResult.genres) }

        userAggregate.handleUserSearchEvent(UserSearchEvent(userId, searchString, searchedGenres))
    }

    private fun increment(map: MutableMap<String, Int>, keys: List<String>?) =
        keys?.forEach {
            map.putIfAbsent(it, 0)
            map[it] = map[it]!! + 1
        }
}