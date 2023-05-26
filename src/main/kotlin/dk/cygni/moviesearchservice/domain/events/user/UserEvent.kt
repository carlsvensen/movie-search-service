package dk.cygni.moviesearchservice.domain.events.user

import dk.cygni.moviesearchservice.domain.events.Event
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "user")
abstract class UserEvent(
    @Indexed var userid: Long
) : Event()