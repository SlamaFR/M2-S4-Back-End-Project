package com.kamelia.ugeoverflow.core.auth

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class BearerTokenFilter(
    private val sessionManager: SessionManager,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val userId = request.getHeader("UserId")?.toLongOrNull()
        val token = request.getHeader("Authorization")?.removePrefix("Bearer ")
        if (token != null) {
            //sessionManager.verify(token)
        }
        filterChain.doFilter(request, response)
    }

}