package dk.cygni.moviesearchservice.repository.mongodb

import dk.cygni.moviesearchservice.domain.events.movie.MovieEvent
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface MovieWriteRepository: MongoRepository<MovieEvent, ObjectId>