package com.kamelia.ugeoverflow.core.auth

import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder


@Component
class SecurityProvider(
    private val sessionManager: SessionManager,
) : AuthenticationProvider {

    @Value("\${ugeoverflow.admin.username}")
    private lateinit var adminName: String

    override fun authenticate(auth: Authentication): Authentication? {
        val username = auth.principal as String
        val password = auth.credentials as String

        println(RequestContextHolder.currentRequestAttributes().sessionId)
        println("Authenticating $username with password $password")
        kotlin.runCatching {
            // sessionManager.checkIdentity(username, password)
        }.onFailure {
            return auth
        }

        val roles = if (username == adminName) listOf("USER", "ADMIN") else listOf("USER")
        return UsernamePasswordAuthenticationToken(
            username,
            null,
            roles.map(::SimpleGrantedAuthority)
        )
    }

    override fun supports(authentication: Class<*>): Boolean =
        authentication == UsernamePasswordAuthenticationToken::class.java
}

