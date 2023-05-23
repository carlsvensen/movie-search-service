package dk.cygni.carlsmoviesearchservice.repository.mongodb

import dk.cygni.carlsmoviesearchservice.domain.events.user.UserEvent
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserReadRepository: MongoRepository<UserEvent, ObjectId> {

    @Query("{ 'username' : ?0 }")
    fun findByUsername(username: String): List<UserEvent>

    @Query("{ 'userid' : ?0 }")
    fun findByUserid(userid: Long): List<UserEvent>
}