package dk.cygni.carlsmoviesearchservice.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

const val SEQUENCE_NAME_USER = "user_sequence"

@Document(collection = "database_sequences")
class DatabaseSequence() {

    @Id
    var id: String? = null
    var seq: Long? = null
}