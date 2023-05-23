package dk.cygni.carlsmoviesearchservice.domain.events

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import java.time.LocalDateTime

abstract class Event {
    @Id
    lateinit var id: ObjectId
    var created: LocalDateTime = LocalDateTime.now()
}