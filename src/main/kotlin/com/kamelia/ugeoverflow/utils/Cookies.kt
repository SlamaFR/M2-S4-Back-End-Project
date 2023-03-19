package com.kamelia.ugeoverflow.utils

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.aspectj.weaver.tools.cache.SimpleCacheFactory.path

object Cookies {
    const val USER_ID = "user-id"
    const val ACCESS_TOKEN = "access-token"
    const val REFRESH_TOKEN = "refresh-token"
}

fun HttpServletResponse.removeCookie(name: String, path: String = "/") = addCookie(Cookie(name, "").apply {
    this.path = path
    maxAge = 0
})

fun HttpServletRequest.getCookieOrNull(name: String): Cookie? = cookies?.firstOrNull { it.name == name }