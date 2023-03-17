package com.kamelia.ugeoverflow.core

import com.kamelia.ugeoverflow.user.toDTO
import com.kamelia.ugeoverflow.utils.currentUserOrNull
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView

class UserInterceptor : HandlerInterceptor {

    override fun postHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        modelAndView: ModelAndView?
    ) {
        currentUserOrNull()?.let { modelAndView?.addObject("user", it.toDTO()) }
    }
}