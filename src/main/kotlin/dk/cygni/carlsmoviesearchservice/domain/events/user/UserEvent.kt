package dk.cygni.carlsmoviesearchservice.domain.events.user

import dk.cygni.carlsmoviesearchservice.domain.events.Event
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "user")
abstract class UserEvent(
    @Indexed var userid: Long
) : Event()