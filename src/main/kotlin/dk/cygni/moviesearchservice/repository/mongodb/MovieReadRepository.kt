package dk.cygni.moviesearchservice.repository.mongodb

import dk.cygni.moviesearchservice.domain.events.movie.MovieEvent
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface MovieReadRepository: MongoRepository<MovieEvent, ObjectId> {

    @Query("{ 'tconst' : ?0 }")
    fun findByTconst(tconst: String): List<MovieEvent>

}