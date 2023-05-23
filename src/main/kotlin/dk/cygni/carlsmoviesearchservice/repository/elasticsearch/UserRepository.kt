package dk.cygni.carlsmoviesearchservice.repository.elasticsearch

import dk.cygni.carlsmoviesearchservice.domain.User
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : ElasticsearchRepository<User, Long> {

    fun findUserByUserid(userid: Long): Optional<User>
}