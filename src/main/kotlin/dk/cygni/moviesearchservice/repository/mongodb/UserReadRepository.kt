package dk.cygni.moviesearchservice.repository.mongodb

import dk.cygni.moviesearchservice.domain.events.user.UserEvent
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserReadRepository: MongoRepository<UserEvent, ObjectId> {

    @Query("{ 'username' : ?0 }")
    fun findByUsername(username: String): List<UserEvent>

    @Query("{ 'userid' : ?0 }")
    fun findByUserid(userid: Long): List<UserEvent>
}