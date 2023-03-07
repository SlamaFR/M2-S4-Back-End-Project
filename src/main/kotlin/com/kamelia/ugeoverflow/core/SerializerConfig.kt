package com.kamelia.ugeoverflow.core

import com.fasterxml.jackson.module.kotlin.kotlinModule
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

@Configuration
class SerializerConfig {

    @Bean
    fun objectMapperBuilder(): Jackson2ObjectMapperBuilder = Jackson2ObjectMapperBuilder()
        .modulesToInstall(kotlinModule())
        .also { LOGGER.info("Loaded Kotlin Jackson module") }

    companion object {

        private val LOGGER = LoggerFactory.getLogger(SerializerConfig::class.java)
    }
}