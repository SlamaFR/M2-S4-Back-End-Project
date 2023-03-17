package com.kamelia.ugeoverflow

import com.kamelia.ugeoverflow.core.MvcController
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@MvcController
@Controller
class ErrorMvcController : ErrorController {

    @GetMapping("/error")
    fun handleError(): String  {
        return "error/error"
    }
}