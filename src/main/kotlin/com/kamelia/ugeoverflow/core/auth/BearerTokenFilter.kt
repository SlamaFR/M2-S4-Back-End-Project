package com.kamelia.ugeoverflow.core.auth

import com.kamelia.ugeoverflow.core.InvalidRequestException
import com.kamelia.ugeoverflow.util.toUUIDFromBase64OrNull
import com.kamelia.ugeoverflow.util.toUUIDOrNull
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.util.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
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
            ?.let {
                it.toUUIDOrNull() ?: throw InvalidRequestException.badRequest("Invalid credentials")
            }

        val base64Token = request.getHeader("Authorization")
            ?.removePrefix("Bearer ")

        if (!checkAuthHeaders(userId, base64Token)) return null

        val token = base64Token.toUUIDFromBase64OrNull()
            ?: throw InvalidRequestException.badRequest("Invalid token")
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
        filterChain: FilterChain,
    ) {
        val auth = try {
            getAuth(request) ?: UsernamePasswordAuthenticationToken(null, null)
        } catch (e: InvalidRequestException) {
            response.status = e.statusCode
            val writer = response.writer
            writer.write(e.message ?: "Invalid request")
            writer.flush()
            return
        }

        auth.details = WebAuthenticationDetailsSource().buildDetails(request)

        SecurityContextHolder.getContext().authentication = auth

        filterChain.doFilter(request, response)
    }
}
