package dk.cygni.carlsmoviesearchservice.domain.events.movie

import dk.cygni.carlsmoviesearchservice.domain.events.Event
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "movie")
abstract class MovieEvent(
    @Indexed var tconst: String
) : Event()