package com.kamelia.ugeoverflow.util

import org.springframework.security.core.userdetails.User
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.web.servlet.request.RequestPostProcessor


fun mockLoggedUser(name: String): RequestPostProcessor = User.builder()
    .username(name)
    .password("")
    .roles("USER")
    .build()
    .let(::user)

val mockedUser1
    get() = mockLoggedUser("user1")

val mockedUser2
    get() = mockLoggedUser("user2")

val mockedAdmin
    get() = mockLoggedUser("admin")
