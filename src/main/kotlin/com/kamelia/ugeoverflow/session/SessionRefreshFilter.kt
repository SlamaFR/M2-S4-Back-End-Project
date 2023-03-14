package com.kamelia.ugeoverflow.session

import com.kamelia.ugeoverflow.core.InvalidRequestException
import com.kamelia.ugeoverflow.util.toUUIDFromBase64OrNull
import com.kamelia.ugeoverflow.util.toUUIDOrNull
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
        val userId = request.getHeader("User-Id").toUUIDOrNull()
            ?: throw InvalidRequestException.badRequest("Invalid credentials")

        val token = request.getHeader("Refresh-Token").toUUIDFromBase64OrNull()
            ?: throw InvalidRequestException.badRequest("Invalid credentials")

        val newtokens = sessionManager.verifyRefresh(userId, token)
            ?: throw InvalidRequestException.badRequest("Invalid credentials")

        val auth = UsernamePasswordAuthenticationToken(
            null,
            newtokens,
            listOf(SimpleGrantedAuthority(Roles.USER))
        )

        auth.details = WebAuthenticationDetailsSource().buildDetails(request)
        SecurityContextHolder.getContext().authentication = auth


        filterChain.doFilter(request, response)
    }


    override fun shouldNotFilter(request: HttpServletRequest): Boolean = request.servletPath != Routes.REFRESH_ROUTE

}
