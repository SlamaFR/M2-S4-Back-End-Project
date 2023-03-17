package com.kamelia.ugeoverflow

import com.kamelia.ugeoverflow.core.InvalidRequestException
import com.kamelia.ugeoverflow.core.MvcController
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler


@ControllerAdvice(annotations = [RestController::class])
class RestResponseEntityExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(value = [InvalidRequestException::class])
    fun handleInvalidRequest(ex: InvalidRequestException): ResponseEntity<Any> =
        ResponseEntity.status(ex.statusCode).body(ex.message)

}

@ControllerAdvice(annotations = [MvcController::class])
class MVCResponseEntityExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(value = [InvalidRequestException::class])
    fun handleInvalidRequest(ex: InvalidRequestException): String = when(ex.statusCode) {
        401 -> "redirect:/auth?error=Invalid+credentials"
        403 -> "error/403"
        else -> "redirect:/"
    }

}
