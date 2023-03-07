package com.kamelia.ugeoverflow

import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.Authentication
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/foo")
class Foo(private val fooService: FooService) {


    @GetMapping("/foo")
    fun foo(@AuthenticationPrincipal auth: Authentication) = fooService.foo()


    @GetMapping("/bar")
    fun bar() = fooService.bar()
}

@Service
class FooService {

    @Secured("ADMIN")
    fun foo() = "FOO"

    @Secured("USER")
    fun bar() = "BAR"
}