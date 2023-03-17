package com.kamelia.ugeoverflow

import jakarta.servlet.http.HttpServletResponse
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class ErrorMVController : ErrorController {

    @RequestMapping("/error")
    fun handleError(
        @RequestParam(value = "code", required = false) code: Int?,
        response: HttpServletResponse
    ): String = when (code ?: response.status) {
        401 -> "error/401"
        404 -> "error/404"
        500 -> "error/500"
        else -> "error/error"
    }
}