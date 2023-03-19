package com.kamelia.ugeoverflow.session

import com.kamelia.ugeoverflow.core.toBase64
import com.kamelia.ugeoverflow.user.User
import com.kamelia.ugeoverflow.utils.Cookies
import com.kamelia.ugeoverflow.utils.Routes
import jakarta.servlet.http.Cookie
import java.util.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class TokenFactory protected constructor(
    @Value("\${ugeoverflow.session.access-token-lifetime:3600000}")
    private val accessTokenLifetime: Long = 10, // 1 hour
    @Value("\${ugeoverflow.session.refresh-token-lifetime:2592000000}")
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

    fun toCookies(): List<Cookie> = buildList {
        add(Cookie(Cookies.USER_ID, userId.toString()).apply {
            path = "/"
            maxAge = 60 * 60 * 24 * 30 // 30 days
        })
        add(Cookie(Cookies.ACCESS_TOKEN, accessToken).apply {
            path = "/"
            maxAge = 60 * 60 // 1 hour
        })
        add(Cookie(Cookies.REFRESH_TOKEN, refreshToken).apply {
            path = Routes.Auth.REFRESH
            maxAge = 60 * 60 * 24 * 30 // 30 days
        })
    }
}
