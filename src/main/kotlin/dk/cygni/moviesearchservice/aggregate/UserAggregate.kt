package dk.cygni.moviesearchservice.aggregate

import dk.cygni.moviesearchservice.commands.user.*
import dk.cygni.moviesearchservice.domain.events.user.UserSearchEvent
import dk.cygni.moviesearchservice.repository.mongodb.UserReadRepository
import dk.cygni.moviesearchservice.repository.mongodb.UserWriteRepository
import dk.cygni.moviesearchservice.service.SequenceService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException

@Component
class UserAggregate(
    private val userWriteRepository: UserWriteRepository,
    private val userReadRepository: UserReadRepository,
    private val sequenceService: SequenceService
) {

    fun handleCreateUserCommand(createUserCommand: CreateUserCommand) {
        if (userReadRepository.findByUsername(createUserCommand.username).isNotEmpty()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "The username is already in use!")
        }
        userWriteRepository.insert(createUserCommand.toUserCreatedEvent(sequenceService.generateUserId()))
    }

    fun handleUpdateUserCommand(updateUserCommand: UpdateUserCommand) {
        if (userReadRepository.findByUserid(updateUserCommand.userid).isEmpty()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "No user with userid ${updateUserCommand.userid} exists!")
        }
        if (userReadRepository.findByUsername(updateUserCommand.newUsername).isNotEmpty()) {
            // TODO: Enable reuse of names that are no longer in use.
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "The username ${updateUserCommand.newUsername} is already in use!")
        }
        userWriteRepository.insert(updateUserCommand.toUserUpdatedEvent())
    }

    fun handleDeleteUserCommand(deleteUserCommand: DeleteUserCommand) {
        if (userReadRepository.findByUserid(deleteUserCommand.userid).isEmpty()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "No user with userid ${deleteUserCommand.userid} exists!")
        }
        // TODO: Delete for real ???
        userWriteRepository.insert(deleteUserCommand.toUserDeletedEvent())
    }

    fun handleUserSearchEvent(userSearchEvent: UserSearchEvent) {
        userWriteRepository.insert(userSearchEvent)
    }
}