package com.kamelia.ugeoverflow.core.auth

import com.kamelia.ugeoverflow.user.User
import com.kamelia.ugeoverflow.user.UserService
import com.kamelia.ugeoverflow.util.InvalidRequestException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class SessionManager(
    val userService: UserService,
    val passwordEncoder: PasswordEncoder,
) {

    private val lock = Any()

    private val userIdToSessions = mutableMapOf<UUID, UserSessionContext>()

    private fun addSession(user: User): TokenData = synchronized(lock) {
        val tokenData = TokenData.fromUser(user)
        val sessionContext = userIdToSessions.computeIfAbsent(user.id) { UserSessionContext(user) }
        sessionContext.addSessionToken(tokenData.accessToken)
        sessionContext.addRefreshToken(tokenData.refreshToken)
        return tokenData
    }

    private fun removeSession(userId: UUID, token: UUID): Unit = synchronized(lock) {
        userIdToSessions[userId]?.removeSessionToken(token)
    }

    private fun removeRefreshToken(userId: UUID, token: UUID): Unit = synchronized(lock) {
        userIdToSessions[userId]?.removeRefreshToken(token)
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

    fun verify(userId: UUID, sessionId: UUID): Boolean = synchronized(lock) {
        userIdToSessions[userId]
            ?.hasSessionToken(sessionId)
            ?: false
    }

    fun verifyRefresh(userId: UUID, refreshToken: UUID): Boolean = synchronized(lock) {
        userIdToSessions[userId]
            ?.hasRefreshToken(refreshToken)
            ?: false
    }

    fun login(username: String, password: String): TokenData {
        val user = userService.findByUsernameOrNull(username)
            ?: throw InvalidRequestException.unauthorized("Invalid credentials")

        if (!passwordEncoder.matches(password, user.password)) {
            throw InvalidRequestException.unauthorized("Invalid credentials")
        }

        return addSession(user)
    }

    fun logout(userId: UUID, token: UUID, refreshToken: UUID): Unit = synchronized(lock) {
        removeSession(userId, token)
        removeRefreshToken(userId, refreshToken)
        logoutCleanup(userId)
    }

    fun logoutAll(userId: UUID): Unit = synchronized(lock) {
        userIdToSessions.remove(userId)
    }

    fun refresh(userId: UUID, refreshToken: UUID): TokenData = synchronized(lock) {
        val user = userService.findByIdOrNull(userId)
            ?: throw InvalidRequestException.unauthorized("Invalid credentials")

        val tokenData = TokenData.fromUser(user)
        userIdToSessions.compute(user.id) { _, _ -> UserSessionContext(user) }
        return tokenData
    }
}

class UserSessionContext(val user: User) {

    private val sessionTokens = mutableSetOf<UUID>()
    private val refreshTokens = mutableSetOf<UUID>()

    fun addSessionToken(token: UUID) {
        sessionTokens += token
    }

    fun addRefreshToken(token: UUID) {
        refreshTokens += token
    }

    fun removeSessionToken(token: UUID) {
        sessionTokens -= token
    }

    fun removeRefreshToken(token: UUID) {
        refreshTokens -= token
    }

    fun hasSessionToken(token: UUID) = token in sessionTokens

    fun hasRefreshToken(token: UUID) = token in refreshTokens

    fun isEmpty(): Boolean = sessionTokens.isEmpty() && refreshTokens.isEmpty()
}