package dk.cygni.moviesearchservice.service

import dk.cygni.moviesearchservice.domain.DatabaseSequence
import dk.cygni.moviesearchservice.domain.SEQUENCE_NAME_USER
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Service

@Service
class SequenceService(private val mongoOperations: MongoOperations) {

    fun generateUserId(): Long {
        val counter: DatabaseSequence? = mongoOperations.findAndModify(
            Query.query(Criteria.where("_id").`is`(SEQUENCE_NAME_USER)),
            Update().inc("seq", 1),
            FindAndModifyOptions.options().returnNew(true).upsert(true),
            DatabaseSequence::class.java
        )

        return if (counter != null) counter.seq ?: 1 else 1
    }
}