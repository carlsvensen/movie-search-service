package dk.cygni.carlsmoviesearchservice.repository.mongodb

import dk.cygni.carlsmoviesearchservice.domain.events.user.UserEvent
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface UserWriteRepository: MongoRepository<UserEvent, ObjectId>