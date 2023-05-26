package dk.cygni.moviesearchservice.projection

import dk.cygni.moviesearchservice.config.USER_QUEUE
import dk.cygni.moviesearchservice.domain.*
import dk.cygni.moviesearchservice.domain.events.user.*
import dk.cygni.moviesearchservice.repository.elasticsearch.UserRepository
import dk.cygni.moviesearchservice.repository.mongodb.UserReadRepository
import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Component
import java.lang.IllegalStateException

@Component
class UserProjector(
    private val userReadRepository: UserReadRepository,
    private val userRepository: UserRepository
) {

    @JmsListener(destination = USER_QUEUE)
    fun queueListener(userid: Long) {
        project(userid).run {
            when (this.deleted) {
                true -> userRepository.delete(this)
                false -> userRepository.save(this)
            }
        }
    }

    fun project(userid: Long): User =
        User().also {
            userReadRepository.findByUserid(userid)
                .ifEmpty { throw IllegalStateException("No user with userid $userid found") }
                .sortedBy { it.created }
                .forEach { userEvent ->
                    when (userEvent) {
                        is UserCreatedEvent -> it.applyUserCreatedEvent(userEvent)
                        is UserUpdatedEvent -> it.applyUserUpdatedEvent(userEvent)
                        is UserSearchEvent -> it.applyUserSearchEvent(userEvent)
                        is UserDeletedEvent -> it.applyUserDeletedEvent()
                    }
                }
            it.filterFavouriteGenres()
        }

}