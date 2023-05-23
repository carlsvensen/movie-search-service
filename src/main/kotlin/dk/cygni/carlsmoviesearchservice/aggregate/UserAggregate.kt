package dk.cygni.carlsmoviesearchservice.aggregate

import dk.cygni.carlsmoviesearchservice.commands.CreateUserCommand
import dk.cygni.carlsmoviesearchservice.domain.SEQUENCE_NAME_USER
import dk.cygni.carlsmoviesearchservice.domain.events.user.UserCreatedEvent
import dk.cygni.carlsmoviesearchservice.domain.events.user.UserSearchEvent
import dk.cygni.carlsmoviesearchservice.repository.mongodb.UserReadRepository
import dk.cygni.carlsmoviesearchservice.repository.mongodb.UserWriteRepository
import dk.cygni.carlsmoviesearchservice.service.SequenceService
import org.springframework.stereotype.Component

@Component
class UserAggregate(
    private val userWriteRepository: UserWriteRepository,
    private val userReadRepository: UserReadRepository,
    private val sequenceService: SequenceService
) {

    fun handleCreateUserCommand(createUserCommand: CreateUserCommand) {
        if (userReadRepository.findByUsername(createUserCommand.username).isNotEmpty()) {
            throw IllegalArgumentException("The username is already in use!")
        }

        userWriteRepository.insert(
            UserCreatedEvent(sequenceService.generateSequence(SEQUENCE_NAME_USER), createUserCommand.username)
        )
    }

    fun handleUserSearchEvent(userSearchEvent: UserSearchEvent) {
        userWriteRepository.insert(userSearchEvent)
    }
}