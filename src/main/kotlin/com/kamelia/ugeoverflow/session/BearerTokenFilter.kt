package com.kamelia.ugeoverflow.session

import com.kamelia.ugeoverflow.core.InvalidRequestException
import com.kamelia.ugeoverflow.core.toUUIDFromBase64OrNull
import com.kamelia.ugeoverflow.core.toUUIDOrNull
import com.kamelia.ugeoverflow.user.UserRepository
import com.kamelia.ugeoverflow.utils.*
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.util.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@Component
class BearerTokenFilter(
    private val sessionManager: SessionManager,
    private val userRepository: UserRepository,
) : OncePerRequestFilter() {

    @Value("\${ugeoverflow.admin.username}")
    private lateinit var adminName: String

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        if (currentAuthOrNull()?.isAuthenticated == true) {
            // TODO no.
            val springUser = currentAuth().principal as User
            val user = userRepository.findByUsername(springUser.username)
                ?: throw AssertionError("User not found")
            val newAuth = UsernamePasswordAuthenticationToken(
                user,
                null,
                springUser.authorities
            )
            newAuth.details = WebAuthenticationDetailsSource().buildDetails(request)
            SecurityContextHolder.getContext().authentication = newAuth
            filterChain.doFilter(request, response)
            return
        }

        val auth = try {
            getAuth(request) ?: UsernamePasswordAuthenticationToken(null, null)
        } catch (e: InvalidRequestException) {
            if (request.servletPath.startsWith("/api")) {
                response.status = e.statusCode
                val writer = response.writer
                writer.write(e.message ?: "Invalid request")
                writer.flush()
            } else {
                response.removeCookie(Cookies.USER_ID)
                response.removeCookie(Cookies.ACCESS_TOKEN)
                response.sendRedirect("/auth?error=Please+log+in")
            }
            return
        }

        auth.details = WebAuthenticationDetailsSource().buildDetails(request)

        SecurityContextHolder.getContext().authentication = auth

        filterChain.doFilter(request, response)
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean = request.servletPath == Routes.User.REFRESH

    @OptIn(ExperimentalContracts::class)
    private fun checkCredendtials(userId: UUID?, base64Token: String?): Boolean {
        contract { returns(true) implies (userId != null && base64Token != null) }

        if (userId == null) {
            if (base64Token != null) {
                throw InvalidRequestException.badRequest(
                    "User-Id header or user-id cookie is required when Authorization header or access-token cookie is present"
                )
            } else {
                return false
            }
        }

        if (base64Token == null) {
            throw InvalidRequestException.badRequest("Authorization header or access-token cookie is required when User-Id header or user-id cookie is present")
        }

        return true
    }

    private fun getAuth(request: HttpServletRequest): UsernamePasswordAuthenticationToken? {
        val userId = (request.getHeader("User-Id") ?: request.getCookieOrNull(Cookies.USER_ID)?.value)
            ?.let {
                it.toUUIDOrNull() ?: throw InvalidRequestException.badRequest("Invalid credentials")
            }

        val base64Token = request.getHeader("Authorization")
            ?.removePrefix("Bearer ")
            ?: request.getCookieOrNull(Cookies.ACCESS_TOKEN)?.value

        if (!checkCredendtials(userId, base64Token)) return null

        val token = base64Token.toUUIDFromBase64OrNull()
            ?: throw InvalidRequestException.badRequest("Invalid credentials")

        val user = sessionManager.verify(userId, token)
            ?: throw InvalidRequestException.unauthorized("Invalid credentials")

        val tokens = UserTokensDTO(userId, token)
        val roles = if (user.username == adminName) {
            listOf(Roles.ADMIN, Roles.USER)
        } else {
            listOf(Roles.USER)
        }
        return UsernamePasswordAuthenticationToken(
            user,
            tokens,
            roles.map(::SimpleGrantedAuthority)
        )
    }

}
