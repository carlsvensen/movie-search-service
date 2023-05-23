package dk.cygni.carlsmoviesearchservice.domain.events.movie

import dk.cygni.carlsmoviesearchservice.domain.events.Event
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "movie")
open class MovieEvent(
    @Indexed open var tconst: String
) : Event()