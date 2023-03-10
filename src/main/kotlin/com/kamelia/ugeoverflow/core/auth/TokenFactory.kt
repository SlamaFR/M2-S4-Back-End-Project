package com.kamelia.ugeoverflow.core.auth

import com.kamelia.ugeoverflow.user.User
import com.kamelia.ugeoverflow.util.toBase64
import jakarta.servlet.http.Cookie
import java.util.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class TokenFactory protected constructor(
    @Value("\${ugeoverflow.auth.access-token-lifetime:3600000}")
    private val accessTokenLifetime: Long, // 1 hour
    @Value("\${ugeoverflow.auth.refresh-token-lifetime:2592000000}")
    private val refreshTokenLifetime: Long, // 30 days
) {

    fun createUserTokens(user: User): TokenData {
        val now = System.currentTimeMillis()
        val accessToken = UUID.randomUUID()
        val refreshToken = UUID.randomUUID()
        return TokenData(
            accessToken = accessToken,
            accessTokenExpiration = now + accessTokenLifetime,
            refreshToken = refreshToken,
            refreshTokenExpiration = now + refreshTokenLifetime,
        )
    }

}

data class TokenData(
    val accessToken: UUID,
    val accessTokenExpiration: Long,
    val refreshToken: UUID,
    val refreshTokenExpiration: Long,
) {

    fun toDTO(userId: UUID): TokensDTO = TokensDTO(userId, accessToken.toBase64(), refreshToken.toBase64())

}

data class TokensDTO(
    val userId: UUID,
    val accessToken: String,
    val refreshToken: String,
) {

    private fun createCookie(name: String, value: String): Cookie = Cookie(name, value).apply {
        path = "/"
        isHttpOnly = true
    }

    fun createCookies(): List<Cookie> = buildList {
        add(createCookie("USER_ID", userId.toString()))
        add(createCookie("ACCESS_TOKEN", accessToken))
        add(createCookie("REFRESH_TOKEN", refreshToken))
    }
}
