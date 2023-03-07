package com.kamelia.ugeoverflow

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity

@EnableWebSecurity
@SpringBootApplication
class BackendProjectApplication

fun main(args: Array<String>) {
    runApplication<BackendProjectApplication>(*args)
}
