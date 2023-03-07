package com.kamelia.ugeoverflow.core

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Value("\${ugeoverflow.admin.username}")
    private lateinit var adminName: String

    @Value("\${ugeoverflow.admin.password}")
    private lateinit var adminPassword: String

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun userDetailsService(encoder: PasswordEncoder): InMemoryUserDetailsManager {
        val admin = User.withUsername(adminName)
            .password(encoder.encode(adminPassword))
            .roles("USER", "ADMIN")
            .build()

        return InMemoryUserDetailsManager(admin)
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.authorizeHttpRequests()
            .anyRequest()
            .authenticated()
            .and()
            .httpBasic()
        return http.build()
    }

}