package dk.cygni.carlsmoviesearchservice.repository.mongodb

import dk.cygni.carlsmoviesearchservice.domain.events.movie.MovieEvent
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface MovieReadRepository: MongoRepository<MovieEvent, ObjectId> {

    @Query("{ 'tconst' : ?0 }")
    fun findByTconst(tconst: String): List<MovieEvent>

}