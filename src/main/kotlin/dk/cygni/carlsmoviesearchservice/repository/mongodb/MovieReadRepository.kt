package dk.cygni.carlsmoviesearchservice.repository.mongodb

import dk.cygni.carlsmoviesearchservice.domain.events.movie.MovieEvent
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.Aggregation
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface MovieReadRepository: MongoRepository<MovieEvent, ObjectId> {

    @Query("{ 'tconst' : ?0 }")
    fun findByTconst(tconst: String): List<MovieEvent>

    @Query("{'originalTitle' : {\$regex:  ?0}}")
    fun findByWildcardText(tconst: String): List<MovieEvent>

   // @Query("{'genre' :  {\$regex: ?0}}")
    @Aggregation(pipeline = [
        "{'\$match': {'genres': {\$regex: ?0}}}",
        //"{'\$match': {'averageRating': {\$ne: null}}}",
        //"{'\$match': {'averageRating': {\$gt: 1}}}",
        "{ \$sample: { size: ?1 } }"])
    fun findByGenre(genre: String, size: Int): MovieEvent
}