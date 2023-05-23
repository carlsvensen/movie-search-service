package dk.cygni.carlsmoviesearchservice.repository.mongodb

import dk.cygni.carlsmoviesearchservice.domain.events.movie.MovieEvent
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface MovieWriteRepository: MongoRepository<MovieEvent, ObjectId>