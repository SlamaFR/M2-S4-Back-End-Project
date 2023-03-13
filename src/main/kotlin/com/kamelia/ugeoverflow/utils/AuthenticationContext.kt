package com.kamelia.ugeoverflow.utils

import com.kamelia.ugeoverflow.user.User
import org.springframework.security.core.context.SecurityContextHolder

private val currentUser: User?
    get() = SecurityContextHolder.getContext().authentication?.principal as? User


fun currentUser(): User = currentUser ?: throw AssertionError("User should not be null")

fun currentUserOrNull(): User? = currentUser
