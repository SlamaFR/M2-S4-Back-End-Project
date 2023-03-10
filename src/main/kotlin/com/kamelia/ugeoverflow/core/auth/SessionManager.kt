package com.kamelia.ugeoverflow.core.auth

import com.kamelia.ugeoverflow.user.User
import com.kamelia.ugeoverflow.user.UserService
import com.kamelia.ugeoverflow.core.InvalidRequestException
import java.util.*
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class SessionManager(
    private val userService: UserService,
    private val passwordEncoder: PasswordEncoder,
    private val tokenFactory: TokenFactory,
) {

    private val lock = Any()

    private val userIdToSessions = mutableMapOf<UUID, UserSessionContext>()

    fun verify(userId: UUID, sessionId: UUID): User? = synchronized(lock) {
        val context = userIdToSessions[userId] ?: return null
        return if (context.hasSessionToken(sessionId)) context.user else null
    }

    fun verifyRefresh(userId: UUID, refreshToken: UUID): User? = synchronized(lock) {
        val context = userIdToSessions[userId] ?: return null
        return if (context.hasRefreshToken(refreshToken)) context.user else null
    }

    fun login(username: String, password: String): TokensDTO {
        val user = userService.findByUsernameOrNull(username)
            ?: throw InvalidRequestException.unauthorized("Invalid credentials")

        if (!passwordEncoder.matches(password, user.password)) {
            throw InvalidRequestException.unauthorized("Invalid credentials")
        }

        return addSession(user)
    }

    fun logout(userId: UUID, token: UUID, refreshToken: UUID): Unit = synchronized(lock) {
        val context = userIdToSessions[userId]
            ?: throw InvalidRequestException.unauthorized("Invalid credentials")
        context.removeTokens(token, refreshToken)
        logoutCleanup(userId)
    }

    fun logoutAll(userId: UUID): Unit = synchronized(lock) {
        userIdToSessions.remove(userId)
    }

    fun refresh(userId: UUID, refreshToken: UUID): TokensDTO = synchronized(lock) {
        val user = userService.findByIdOrNull(userId)
            ?: throw InvalidRequestException.unauthorized("Invalid credentials")

        val tokenData = tokenFactory.createUserTokens(user)
        val context = userIdToSessions.computeIfAbsent(user.id) { UserSessionContext(user) }

        context.addTokens(
            tokenData.accessToken,
            tokenData.accessTokenExpiration,
            tokenData.refreshToken,
            tokenData.refreshTokenExpiration,
        )
        return tokenData.toDTO(userId)
    }

    private fun addSession(user: User): TokensDTO = synchronized(lock) {
        val tokenData = tokenFactory.createUserTokens(user)
        val sessionContext = userIdToSessions.computeIfAbsent(user.id) { UserSessionContext(user) }
        sessionContext.addTokens(
            tokenData.accessToken,
            tokenData.accessTokenExpiration,
            tokenData.refreshToken,
            tokenData.refreshTokenExpiration,
        )
        return tokenData.toDTO(user.id)
    }

    private fun logoutCleanup(userId: UUID): Unit = synchronized(lock) {
        userIdToSessions.computeIfPresent(userId) { _, sessionContext ->
            if (sessionContext.isEmpty()) {
                null
            } else {
                sessionContext
            }
        }
    }

}

private class UserSessionContext(val user: User) {

    private val sessionTokens = HashMap<UUID, Long>() // <token, expiration>
    private val refreshTokens = HashMap<UUID, Pair<Long, UUID>>() // <refreshToken, <expiration, accessToken>>

    fun addTokens(
        sessionToken: UUID,
        sessionTokenExpiration: Long,
        refreshToken: UUID,
        refreshTokenExpiration: Long,
    ) {
        sessionTokens[sessionToken] = sessionTokenExpiration
        refreshTokens[refreshToken] = refreshTokenExpiration to sessionToken
    }

    fun removeTokens(sessionToken: UUID, refreshToken: UUID) {
        sessionTokens.remove(sessionToken)
        refreshTokens.remove(refreshToken)
    }

    fun hasSessionToken(token: UUID) = token in sessionTokens

    fun hasRefreshToken(token: UUID) = token in refreshTokens

    fun isEmpty(): Boolean = sessionTokens.isEmpty() && refreshTokens.isEmpty()
}
