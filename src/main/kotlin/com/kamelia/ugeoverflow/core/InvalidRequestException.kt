package com.kamelia.ugeoverflow.core

import org.springframework.http.HttpStatus

class InvalidRequestException(val statusCode: Int, message: String) : RuntimeException(message) {

    init {
        require(statusCode in 400..599) { "Status code ($statusCode) must be in range 400..599" }
    }

    constructor(status: HttpStatus, message: String) : this(status.value(), message)

    companion object {

        fun forbidden(message: String) = InvalidRequestException(HttpStatus.BAD_REQUEST, message)

        fun notFound(message: String) = InvalidRequestException(HttpStatus.NOT_FOUND, message)

        fun badRequest(message: String) = InvalidRequestException(HttpStatus.BAD_REQUEST, message)

    }

}
