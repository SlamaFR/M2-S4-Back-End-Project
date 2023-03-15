package com.kamelia.ugeoverflow.utils

import com.kamelia.ugeoverflow.session.TokensDTO
import com.kamelia.ugeoverflow.session.UserTokensDTO
import com.kamelia.ugeoverflow.user.User
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder

private val currentUser: User?
    get() = currentAuthOrNull()?.principal as? User

fun currentAuthOrNull(): Authentication? = SecurityContextHolder.getContext().authentication

fun currentAuth(): Authentication = SecurityContextHolder.getContext().authentication
    ?: throw AssertionError("Authentication should not be null")

fun currentUser(): User = currentUser ?: throw AssertionError("User should not be null")

fun currentUserOrNull(): User? = currentUser

val Authentication.user: User
    get() = principal as User

val Authentication.tokens: UserTokensDTO
    get() = credentials as UserTokensDTO

val Authentication.refreshedTokens: TokensDTO
    get() = credentials as TokensDTO
