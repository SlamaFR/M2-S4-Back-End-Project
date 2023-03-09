package com.kamelia.ugeoverflow.core.auth

import com.kamelia.ugeoverflow.util.InvalidRequestException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@Component
class BearerTokenFilter(
    private val sessionManager: SessionManager,
) : OncePerRequestFilter() {

    @Value("\${ugeoverflow.admin.username}")
    private lateinit var adminName: String

    @OptIn(ExperimentalContracts::class)
    private fun checkAuthHeaders(userId: UUID?, base64Token: String?): Boolean {
        contract { returns(true) implies (userId != null && base64Token != null) }

        if (userId == null) {
            if (base64Token != null) {
                throw InvalidRequestException.badRequest("User-Id header is required when Authorization header is present")
            } else {
                return false
            }
        }

        if (base64Token == null) {
            throw InvalidRequestException.badRequest("Authorization header is required when User-Id header is present")
        }

        return true
    }

    private fun getAuth(request: HttpServletRequest): UsernamePasswordAuthenticationToken? {
        val userId = request.getHeader("User-Id")
            ?.let(UUID::fromString)

        val base64Token = request.getHeader("Authorization")
            ?.removePrefix("Bearer ")

        if (!checkAuthHeaders(userId, base64Token)) return null

        val token = try {
            UUID.fromString(Base64.getDecoder().decode(base64Token).toString(Charsets.UTF_8))
        } catch (_: IllegalArgumentException) {
            throw InvalidRequestException.badRequest("Invalid token")
        }
        val user = sessionManager.verify(userId, token) ?: return null

        val roles = if (user.username == adminName) listOf("ROLE_USER", "ROLE_ADMIN") else listOf("ROLE_USER")
        return UsernamePasswordAuthenticationToken(
            user,
            null,
            roles.map(::SimpleGrantedAuthority)
        )
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val auth = getAuth(request)
            ?: UsernamePasswordAuthenticationToken(null, null)

        auth.details = WebAuthenticationDetailsSource().buildDetails(request)

        SecurityContextHolder.getContext().authentication = auth

        filterChain.doFilter(request, response)
    }
}
