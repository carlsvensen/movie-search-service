package dk.cygni.carlsmoviesearchservice.controller

import dk.cygni.carlsmoviesearchservice.aggregate.UserAggregate
import dk.cygni.carlsmoviesearchservice.commands.CreateUserCommand
import dk.cygni.carlsmoviesearchservice.domain.Movie
import dk.cygni.carlsmoviesearchservice.domain.User
import dk.cygni.carlsmoviesearchservice.queries.MovieByTconstQuery
import dk.cygni.carlsmoviesearchservice.queries.MovieByTextQuery
import dk.cygni.carlsmoviesearchservice.repository.elasticsearch.UserRepository
import dk.cygni.carlsmoviesearchservice.service.FileReaderService
import dk.cygni.carlsmoviesearchservice.service.SearchService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/")
class MovieSearchController(
    private val userAggregate: UserAggregate,
    private val fileReaderService: FileReaderService,
    private val searchService: SearchService,
    private val userRepository: UserRepository
) {
    @PostMapping("/readfile/movie")
    fun readMovieFile() =
        fileReaderService.readMovieFile()

    @PostMapping("/readfile/rating")
    fun readRatingFile() =
        fileReaderService.readRatingFile()

    @PutMapping("/user")
    fun putNewUser(@RequestParam username: String) =
        userAggregate.handleCreateUserCommand(CreateUserCommand(username))

    @GetMapping("/user")
    fun getUser(@RequestParam userid: Long): User =
        userRepository.findUserByUserid(userid)
            .orElseThrow { IllegalArgumentException("No user with userid $userid exists.") }

    @GetMapping("/movie/title")
    fun getMovieBySearchStringOnTitle(@RequestParam userid: Long, @RequestParam searchString: String): List<Movie> =
        searchService.searchForMovieByTitle(userid, MovieByTextQuery(searchString))

    @GetMapping("/movie")
    fun getMovieBySearchStringOnAnything(@RequestParam userid: Long, @RequestParam searchString: String): List<Movie> =
        searchService.searchForMovieOnEveryting(userid, MovieByTextQuery(searchString))

    @GetMapping("/movie/{tconst}")
    fun getMovieByTconst(@RequestParam userid: Long, @PathVariable tconst: String): Movie =
        searchService.searchForMovieByTconst(userid, MovieByTconstQuery(tconst))

    @GetMapping("/movie/suggestion")
    fun getSuggestions(@RequestParam userid: Long): List<Movie> =
        searchService.getSuggestion(userid)
}