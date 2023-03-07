package com.kamelia.ugeoverflow

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity

@SpringBootApplication
@EnableMethodSecurity(securedEnabled = true)
class BackendProjectApplication

fun main(args: Array<String>) {
    runApplication<BackendProjectApplication>(*args)
}
