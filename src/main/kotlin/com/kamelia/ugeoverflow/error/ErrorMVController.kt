package com.kamelia.ugeoverflow.error

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.*
import org.springframework.web.bind.annotation.RequestParam


@Controller
class ErrorMVController : ErrorController {

    @RequestMapping("/error", method = [GET, POST, DELETE, PUT, PATCH])
    fun renderErrorPage(
        @RequestParam("code", required = false) code: Int?,
        model: Model,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): String {
        val errorMsg = when (val errorCode = response.status) {
            400 -> "Http Error Code: 400. Bad Request"
            401 -> "Http Error Code: 401. Unauthorized"
            404 -> "Http Error Code: 404. Resource not found"
            500 -> "Http Error Code: 500. Internal Server Error"
            else -> "Http Error Code: ${code ?: errorCode}"
        }
        model.addAttribute("errorMsg", errorMsg)
        return "error/error"
    }
}
