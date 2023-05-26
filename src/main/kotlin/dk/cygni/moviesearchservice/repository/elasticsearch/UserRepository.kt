package dk.cygni.moviesearchservice.repository.elasticsearch

import dk.cygni.moviesearchservice.domain.User
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : ElasticsearchRepository<User, Long> {

    fun findUserByUserid(userid: Long): Optional<User>

    fun findUserByUsername(username: String): Optional<User>
}