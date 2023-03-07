package com.kamelia.ugeoverflow.core

import com.kamelia.ugeoverflow.user.UserService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Value("\${ugeoverflow.admin.username}")
    private lateinit var adminName: String

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain = http
        .csrf().disable()
        .authorizeHttpRequests()
        .anyRequest().permitAll()
        .and().httpBasic()
        .and().build()

    @Bean
    fun authenticationManager(
        users: UserService,
        passwordEncoder: PasswordEncoder,
    ): AuthenticationManager = AuthenticationManager { auth ->
        val username = auth.principal as String
        val password = auth.credentials as String

        users.checkIdentity(username, password)

        val roles = if (username == adminName) listOf("USER", "ADMIN") else listOf("USER")
        UsernamePasswordAuthenticationToken(
            username,
            null,
            roles.map(::SimpleGrantedAuthority)
        )
    }
}