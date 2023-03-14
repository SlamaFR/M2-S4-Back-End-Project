package com.kamelia.ugeoverflow.session

import com.kamelia.ugeoverflow.core.InvalidRequestException
import com.kamelia.ugeoverflow.core.toUUIDFromBase64OrNull
import com.kamelia.ugeoverflow.core.toUUIDOrNull
import com.kamelia.ugeoverflow.utils.Roles
import com.kamelia.ugeoverflow.utils.Routes
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class SessionRefreshFilter(
    private val sessionManager: SessionManager,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        try {
            auth(request)
        } catch (e: InvalidRequestException) {
            response.status = e.statusCode
            val writer = response.writer
            writer.write(e.message ?: "Invalid request")
            writer.flush()
        }

        filterChain.doFilter(request, response)
    }

    private fun auth(request: HttpServletRequest) {
        println("aaaa")
        val userId = request.getHeader("User-Id").toUUIDOrNull()
            ?: throw InvalidRequestException.badRequest("Invalid credentials")

        println("bbbb")
        val token = request.getHeader("Refresh-Token").toUUIDFromBase64OrNull()
            ?: throw InvalidRequestException.badRequest("Invalid credentials")

        println("cccc")
        val newTokens = sessionManager.verifyRefresh(userId, token)
            ?: throw InvalidRequestException.badRequest("Invalid credentials")

        println("dddd")
        val auth = UsernamePasswordAuthenticationToken(
            null,
            newTokens,
            listOf(SimpleGrantedAuthority(Roles.USER))
        )

        auth.details = WebAuthenticationDetailsSource().buildDetails(request)
        SecurityContextHolder.getContext().authentication = auth
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean = request.servletPath != Routes.User.REFRESH

}
