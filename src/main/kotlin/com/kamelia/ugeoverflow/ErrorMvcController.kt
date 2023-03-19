package com.kamelia.ugeoverflow

import com.kamelia.ugeoverflow.core.MvcController
import com.kamelia.ugeoverflow.utils.Routes
import jakarta.servlet.http.HttpServletResponse
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@MvcController
@Controller
class ErrorMvcController : ErrorController {

    @RequestMapping(Routes.Error.ROOT)
    fun handleError(
        @RequestParam(value = "code", required = false) code: Int?,
        response: HttpServletResponse,
    ): String = when (code ?: response.status) {
        401 -> "error/401"
        404, 405 -> "error/404"
        else -> "error/error"
    }
}