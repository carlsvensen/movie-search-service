package dk.cygni.carlsmoviesearchservice.projection

import dk.cygni.carlsmoviesearchservice.domain.User
import dk.cygni.carlsmoviesearchservice.domain.applyUserCreatedEvent
import dk.cygni.carlsmoviesearchservice.domain.applyUserSearchEvent
import dk.cygni.carlsmoviesearchservice.domain.events.user.UserCreatedEvent
import dk.cygni.carlsmoviesearchservice.domain.events.user.UserEvent
import dk.cygni.carlsmoviesearchservice.domain.events.user.UserSearchEvent
import dk.cygni.carlsmoviesearchservice.domain.filterFavouriteGenres
import dk.cygni.carlsmoviesearchservice.queries.UserByIdQuery
import dk.cygni.carlsmoviesearchservice.repository.elasticsearch.UserRepository
import dk.cygni.carlsmoviesearchservice.repository.mongodb.UserReadRepository
import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Component

@Component
class UserProjector(
    private val userReadRepository: UserReadRepository,
    private val userRepository: UserRepository
) {

    @JmsListener(destination = "\${dk.cygni.carlsmoviesearchservice.queuename.user}")
    fun queueListener(userid: Long) {
        userRepository.save(project(UserByIdQuery(userid)))
    }

    fun project(userByIdQuery: UserByIdQuery): User {
        val allUserEvents: List<UserEvent> = userReadRepository.findByUserid(userByIdQuery.userid)

        // TODO: Må finnes en CreateUser i denne lista
        if (allUserEvents.isNotEmpty()) {
            val user = User()
            allUserEvents
                .sortedBy { it.created }
                .forEach { userEvent ->
                    when (userEvent) {
                        is UserCreatedEvent -> user.applyUserCreatedEvent(userEvent)
                        is UserSearchEvent -> user.applyUserSearchEvent(userEvent)
                    }
                }

            user.filterFavouriteGenres()

            return user

        } else {
            throw IllegalArgumentException("No user with userid ${userByIdQuery.userid} found")
        }
    }
}