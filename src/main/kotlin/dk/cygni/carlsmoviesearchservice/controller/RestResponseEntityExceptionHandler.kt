package dk.cygni.carlsmoviesearchservice.controller

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class RestResponseEntityExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(value = [Exception::class])
    fun handleUnhandledException(
        ex: Exception, request: WebRequest
    ): ResponseEntity<Any?>? {
        logger.error { "An exception was thrown: ${ex.message}. Exception: $ex" }

        return handleExceptionInternal(
            ex, "Unknown error occurred", HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request
        )
    }
}