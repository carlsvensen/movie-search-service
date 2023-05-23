package dk.cygni.carlsmoviesearchservice.repository.elasticsearch

import dk.cygni.carlsmoviesearchservice.domain.Movie
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.elasticsearch.annotations.Query
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface MovieRepository : ElasticsearchRepository<Movie, String> {

    fun findMovieByTconst(tconst: String): Optional<Movie>

    @Query("{" +
            "   \"match\": {" +
            "       \"primaryTitle\": {" +
            "           \"query\": \"?0\"," +
            "           \"operator\": \"and\"" +
            "       }" +
            "   }" +
            "}")
    fun findMovieByTitle(searchString: String): List<Movie>

    @Query("{" +
            "   \"bool\": {" +
            "       \"must\": {" +
            "           \"multi_match\": {" +
            "               \"query\": \"?0\"," +
            "               \"operator\": \"or\"," +
            "               \"type\": \"most_fields\"" +
            "           }" +
            "       }," +
            "       \"should\": {" +
            "           \"multi_match\": {" +
            "               \"query\": \"?0\"," +
            "               \"operator\": \"and\"" +
            "           }" +
            "       }" +
            "   }" +
            "}")
    fun findMovieBySearchOnEverything(searchString: String, pageable: Pageable): List<Movie>

    @Query("{" +
            "    \"function_score\": {" +
            "      \"query\": {" +
            "        \"bool\": {" +
            "          \"filter\": [" +
            "            {" +
            "              \"match\": {" +
            "                \"genres\": \"?0\"" +
            "              }" +
            "            }," +
            "            {" +
            "            \"range\": {" +
            "              \"averageRating\": {" +
            "                \"gte\": ?1" +
            "              }" +
            "            }" +
            "            }" +
            "          ]" +
            "        }" +
            "      }, " +
            "      \"random_score\": {}, " +
            "      \"boost_mode\": \"replace\"" +
            "    }" +
            "  }")
    fun findRandomMovieByGenreAndMinRating(genre: String, averageRating: Int): Movie

}