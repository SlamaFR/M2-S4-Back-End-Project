package com.kamelia.ugeoverflow.session

import com.kamelia.ugeoverflow.core.InvalidRequestException
import com.kamelia.ugeoverflow.user.User
import com.kamelia.ugeoverflow.user.UserRepository
import java.util.*
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
@EnableAsync
class SessionManager(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val tokenFactory: TokenFactory,
) {

    private val lock = Any()

    private val userIdToSessions = mutableMapOf<UUID, UserSessionContext>()

    fun verify(userId: UUID, sessionId: UUID): User? = synchronized(lock) {
        val context = userIdToSessions[userId] ?: return null
        return if (context.hasSessionToken(sessionId)) context.user else null
    }

    fun verifyRefresh(userId: UUID, refreshToken: UUID): TokensDTO? = synchronized(lock) {
        val context = userIdToSessions[userId] ?: return null
        if (!context.hasRefreshToken(refreshToken)) return null

        val tokens = tokenFactory.createUserTokens(context.user)
        context.addTokens(
            tokens.accessToken,
            tokens.accessTokenExpiration,
            tokens.refreshToken,
            tokens.refreshTokenExpiration,
        )
        return tokens.toDTO(userId)
    }

    fun login(username: String, password: String): TokensDTO {
        val user = userRepository.findByUsername(username)
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
        val user = userRepository.findByIdOrNull(userId)
            ?: throw InvalidRequestException.unauthorized("Invalid credentials")

        val tokenData = tokenFactory.createUserTokens(user)
        val context = userIdToSessions[user.id]
            ?: throw AssertionError("User session context should exist as refresh token was valid")

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

    @Scheduled(fixedRateString = "\${ugeoverflow.session.purge-rate:300000}")
    private fun purgeExpiredSessions() {
        val now = System.currentTimeMillis()
        var removedCount = 0
        synchronized(lock) {
            val iterator = userIdToSessions.iterator()
            while (iterator.hasNext()) {
                val sessionContext = iterator.next().value
                removedCount += sessionContext.removeExpiredTokens(now)
                if (sessionContext.isEmpty()) {
                    iterator.remove()
                }
            }
        }
        logger.info("Purged $removedCount expired sessions")
    }

    private companion object {

        private val logger = LoggerFactory.getLogger(SessionManager::class.java)

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
        refreshTokens.compute(refreshToken) { _, value ->
            if (value?.second == sessionToken) {
                sessionTokens.remove(sessionToken)
                null
            } else {
                throw InvalidRequestException.unauthorized("Invalid credentials")
            }
        }
    }

    fun hasSessionToken(token: UUID): Boolean = sessionTokens.computeIfPresent(token) { _, expiration ->
        if (expiration < System.currentTimeMillis()) {
            null
        } else {
            expiration
        }
    } != null

    fun hasRefreshToken(token: UUID) = refreshTokens.computeIfPresent(token) { _, value ->
        val (expiration, sessionToken) = value
        if (expiration < System.currentTimeMillis()) {
            sessionTokens.remove(sessionToken)
            null
        } else {
            value
        }
    } != null

    fun isEmpty(): Boolean = sessionTokens.isEmpty() && refreshTokens.isEmpty()

    fun removeExpiredTokens(now: Long): Int {
        var count = 0
        sessionTokens.entries.removeIf {
            (it.value < now).also { removed -> if (removed) count++ }
        }
        refreshTokens.entries.removeIf {
            (it.value.first < now).also { removed -> if (removed) count++ }
        }
        return count
    }

}
