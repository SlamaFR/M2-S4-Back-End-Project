package com.kamelia.ugeoverflow

import com.fasterxml.jackson.module.kotlin.kotlinModule
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

@SpringBootApplication(exclude = [SecurityAutoConfiguration::class])
class BackendProjectApplication {

    @Bean
    fun objectMapperBuilder(): Jackson2ObjectMapperBuilder = Jackson2ObjectMapperBuilder()
        .modulesToInstall(kotlinModule())
        .also { LOGGER.info("Loaded Kotlin Jackson module") }

    companion object {

        private val LOGGER = LoggerFactory.getLogger(this::class.java)
    }
}

fun main(args: Array<String>) {
    runApplication<BackendProjectApplication>(*args)
}
