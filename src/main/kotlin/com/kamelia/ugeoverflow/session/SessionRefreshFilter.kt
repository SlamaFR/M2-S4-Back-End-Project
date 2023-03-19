package com.kamelia.ugeoverflow.session

import com.kamelia.ugeoverflow.core.InvalidRequestException
import com.kamelia.ugeoverflow.core.toUUIDFromBase64OrNull
import com.kamelia.ugeoverflow.core.toUUIDOrNull
import com.kamelia.ugeoverflow.utils.Cookies
import com.kamelia.ugeoverflow.utils.Roles
import com.kamelia.ugeoverflow.utils.Routes
import com.kamelia.ugeoverflow.utils.getCookieOrNull
import com.kamelia.ugeoverflow.utils.removeCookie
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
            if (request.servletPath.startsWith("/api")) {
                response.status = e.statusCode
                val writer = response.writer
                writer.write(e.message ?: "Invalid request")
                writer.flush()
            } else {
                response.removeCookie(Cookies.USER_ID)
                response.removeCookie(Cookies.ACCESS_TOKEN)
                response.removeCookie(Cookies.REFRESH_TOKEN, Routes.Auth.ROOT)
                response.sendRedirect("/auth?error=Session+expired:+Please+log+in")
            }
            return
        }

        filterChain.doFilter(request, response)
    }

    private fun auth(request: HttpServletRequest) {
        val userId = (request.getHeader("User-Id") ?: request.getCookieOrNull(Cookies.USER_ID)?.value)
            ?.toUUIDOrNull()
            ?: throw InvalidRequestException.badRequest("Invalid credentials")

        val token = (request.getHeader("Refresh-Token") ?: request.getCookieOrNull(Cookies.REFRESH_TOKEN)?.value)
            ?.toUUIDFromBase64OrNull()
            ?: throw InvalidRequestException.badRequest("Invalid credentials")

        val newTokens = sessionManager.verifyRefresh(userId, token)
            ?: throw InvalidRequestException.badRequest("Invalid credentials")

        val auth = UsernamePasswordAuthenticationToken(
            null,
            newTokens,
            listOf(SimpleGrantedAuthority(Roles.USER))
        )

        auth.details = WebAuthenticationDetailsSource().buildDetails(request)
        SecurityContextHolder.getContext().authentication = auth
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean =
        request.servletPath != Routes.Api.User.REFRESH && request.servletPath != Routes.Auth.REFRESH

}
