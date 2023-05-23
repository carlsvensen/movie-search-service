package dk.cygni.carlsmoviesearchservice.controller

import dk.cygni.carlsmoviesearchservice.aggregate.UserAggregate
import dk.cygni.carlsmoviesearchservice.commands.user.CreateUserCommand
import dk.cygni.carlsmoviesearchservice.commands.user.DeleteUserCommand
import dk.cygni.carlsmoviesearchservice.commands.user.UpdateUserCommand
import dk.cygni.carlsmoviesearchservice.domain.Movie
import dk.cygni.carlsmoviesearchservice.domain.User
import dk.cygni.carlsmoviesearchservice.queries.MovieByTconstQuery
import dk.cygni.carlsmoviesearchservice.queries.MovieByTextQuery
import dk.cygni.carlsmoviesearchservice.queries.MovieSuggestionQuery
import dk.cygni.carlsmoviesearchservice.queries.UserByUsernameQuery
import dk.cygni.carlsmoviesearchservice.service.FileReaderService
import dk.cygni.carlsmoviesearchservice.service.SearchService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/")
class MovieSearchController(
    private val userAggregate: UserAggregate,
    private val fileReaderService: FileReaderService,
    private val searchService: SearchService
) {
    @PostMapping("/readfile/movie")
    fun readMovieFile(@RequestParam filename: String) =
        fileReaderService.readMovieFile(filename)

    @PostMapping("/readfile/rating")
    fun readRatingFile(@RequestParam filename: String) =
        fileReaderService.readRatingFile(filename)

    @PutMapping("/user")
    fun putNewUser(@RequestBody createUserCommand: CreateUserCommand) =
        userAggregate.handleCreateUserCommand(createUserCommand)

    @PostMapping("/user")
    fun putNewUser(@RequestBody createUserCommand: UpdateUserCommand) =
        userAggregate.handleUpdateUserCommand(createUserCommand)

    @GetMapping("/user")
    fun getUser(@RequestBody userByUsernameQuery: UserByUsernameQuery): User =
        searchService.searchForUser(userByUsernameQuery)

    @DeleteMapping("/user")
    fun deleteUser(@RequestBody deleteUserCommand: DeleteUserCommand) =
        userAggregate.handleDeleteUserCommand(deleteUserCommand)

    @GetMapping("/movie")
    fun getMovieBySearchStringOnAnything(@RequestBody movieByTextQuery: MovieByTextQuery): List<Movie> =
        searchService.searchForMovieOnEveryting(movieByTextQuery)

    @GetMapping("/movie/title")
    fun getMovieBySearchStringOnTitle(@RequestBody movieByTextQuery: MovieByTextQuery): List<Movie> =
        searchService.searchForMovieByTitle(movieByTextQuery)

    @GetMapping("/movie/tconst")
    fun getMovieByTconst(@RequestBody movieByTconstQuery: MovieByTconstQuery): Movie =
        searchService.searchForMovieByTconst(movieByTconstQuery)

    @GetMapping("/movie/suggestion")
    fun getSuggestions(@RequestBody movieSuggestionQuery: MovieSuggestionQuery): List<Movie> =
        searchService.getSuggestion(movieSuggestionQuery)
}
