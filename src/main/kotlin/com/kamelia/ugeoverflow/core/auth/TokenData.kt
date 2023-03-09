package com.kamelia.ugeoverflow.core.auth

import com.kamelia.ugeoverflow.user.User
import jakarta.servlet.http.Cookie
import java.util.*

class TokenData(
    val userId: UUID,
    val accessToken: UUID,
    val accessTokenExpiration: Long,
    val refreshToken: UUID,
    val refreshTokenExpiration: Long,
) {

    fun toDTO(): TokensDTO = TokensDTO(userId, accessToken, refreshToken)

    companion object {

        // 1 hour
        const val DEFAULT_ACCESS_TOKEN_LIFETIME = 1000L * 60L * 60L

        // 30 days
        const val DEFAULT_REFRESH_TOKEN_LIFETIME = 1000L * 60L * 60L * 24L * 30L

        fun fromUser(
            user: User,
            accessTokenLifetime: Long = DEFAULT_ACCESS_TOKEN_LIFETIME,
            refreshTokenLifetime: Long = DEFAULT_REFRESH_TOKEN_LIFETIME,
        ): TokenData {
            val now = System.currentTimeMillis()
            val accessTokenExpiration = now + accessTokenLifetime
            val refreshTokenExpiration = now + refreshTokenLifetime
            return TokenData(
                user.id,
                UUID.randomUUID(),
                accessTokenExpiration,
                UUID.randomUUID(),
                refreshTokenExpiration,
            )
        }
    }
}

data class TokensDTO(
    val userId: UUID,
    val accessToken: UUID,
    val refreshToken: UUID,
) {

    private fun createCookie(name: String, value: String): Cookie = Cookie(name, value).apply {
        path = "/"
        isHttpOnly = true
        secure = true
    }

    fun createCookies(): List<Cookie> = buildList {
        add(createCookie("USER_ID", userId.toString()))
        add(createCookie("ACCESS_TOKEN", accessToken.toString()))
        add(createCookie("REFRESH_TOKEN", refreshToken.toString()))
    }
}
