package com.kamelia.ugeoverflow.core.auth

import com.kamelia.ugeoverflow.user.User
import java.util.*

class TokenData(
    val accessToken: UUID,
    val accessTokenExpiration: Long,
    val refreshToken: UUID,
    val refreshTokenExpiration: Long,
) {

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
                UUID.randomUUID(),
                accessTokenExpiration,
                UUID.randomUUID(),
                refreshTokenExpiration,
            )
        }
    }
}