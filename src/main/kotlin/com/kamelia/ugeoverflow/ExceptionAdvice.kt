package com.kamelia.ugeoverflow

import com.kamelia.ugeoverflow.util.InvalidRequestException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler


@ControllerAdvice
class RestResponseEntityExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(value = [InvalidRequestException::class])
    fun handleForbidden(ex: InvalidRequestException): ResponseEntity<Any> =
        ResponseEntity.status(ex.statusCode).body(ex.message)

}