package dk.cygni.carlsmoviesearchservice.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.FindAndModifyOptions.options
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.Criteria.where


import java.util.*

const val SEQUENCE_NAME_USER = "user_sequence"

@Document(collection = "database_sequences")
class DatabaseSequence() {

    @Id
    var id: String? = null
    var seq: Long? = null
}