package com.kamelia.ugeoverflow.utils

import com.kamelia.ugeoverflow.user.User
import org.springframework.security.core.context.SecurityContextHolder

val currentUser: User?
    get() = SecurityContextHolder.getContext().authentication?.principal as? User
