package dk.cygni.carlsmoviesearchservice.aggregate

import dk.cygni.carlsmoviesearchservice.commands.UpdateRatingCommand
import dk.cygni.carlsmoviesearchservice.domain.events.movie.MovieEvent
import dk.cygni.carlsmoviesearchservice.domain.events.movie.MovieUpdateRatingEvent
import dk.cygni.carlsmoviesearchservice.repository.mongodb.MovieReadRepository
import dk.cygni.carlsmoviesearchservice.repository.mongodb.MovieWriteRepository
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*

@ExtendWith(MockitoExtension::class)
class MovieAggregateTest {

    @Mock
    lateinit var movieReadRepository: MovieReadRepository

    @Mock
    lateinit var movieWriteRepository: MovieWriteRepository

    @InjectMocks
    lateinit var movieAggregate: MovieAggregate

    val objectId: ObjectId = ObjectId()
    val tconst: String = "tconst"
    val averageRating: Double = 1.1
    val numOfVotes: Long = 200

    @Test
    fun `compare should be equal`() {
        val updateRatingCommand = UpdateRatingCommand(tconst, averageRating + 1, numOfVotes + 10)
        val movieEvent = MovieUpdateRatingEvent(tconst, averageRating, numOfVotes).apply { id = objectId }
        val movieEvent2 = MovieUpdateRatingEvent(tconst, averageRating + 1, numOfVotes + 10).apply { id = objectId }

        whenever(movieReadRepository.findByTconst(tconst)).thenReturn(listOf(movieEvent, movieEvent2))

        movieAggregate.handleUpdateRating(updateRatingCommand)

        verify(movieWriteRepository, never()).insert(any<MovieEvent>())
    }

    @Test
    fun `compare should not be equal`() {
        val updateRatingCommand = UpdateRatingCommand(tconst, averageRating - 1, numOfVotes - 10)
        val movieEvent = MovieUpdateRatingEvent(tconst, averageRating - 1, numOfVotes - 10).apply { id = objectId }
        val movieEvent2 = MovieUpdateRatingEvent(tconst, averageRating, numOfVotes).apply { id = objectId }

        whenever(movieReadRepository.findByTconst(tconst)).thenReturn(listOf(movieEvent, movieEvent2))

        movieAggregate.handleUpdateRating(updateRatingCommand)

        verify(movieWriteRepository, times(1)).insert(any<MovieEvent>())
    }
}