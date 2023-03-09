package com.kamelia.ugeoverflow.core

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.web.SecurityFilterChain


@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val authProvider: SecurityProvider,
) {

    @Value("\${ugeoverflow.admin.username}")
    private lateinit var adminName: String

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            csrf { disable() }
            authorizeHttpRequests { authorize(anyRequest, permitAll) }
            formLogin { permitAll() }
            httpBasic {}
        }
        return http.build()
    }

    @Bean
    fun authManager(http: HttpSecurity): AuthenticationManager {
        val authenticationManagerBuilder = http.getSharedObject(
            AuthenticationManagerBuilder::class.java
        )
        authenticationManagerBuilder.authenticationProvider(authProvider)
        //authenticationManagerBuilder.userDetailsService()
        return authenticationManagerBuilder.build()
    }

//    @Bean
//    fun authenticationManager(
//        users: UserService,
//        passwordEncoder: PasswordEncoder,
//    ): AuthenticationManager = AuthenticationManager { auth ->
//        val username = auth.principal as String
//        val password = auth.credentials as String
//
//        users.checkIdentity(username, password)
//
//        val roles = if (username == adminName) listOf("USER", "ADMIN") else listOf("USER")
//        UsernamePasswordAuthenticationToken(
//            username,
//            null,
//            roles.map(::SimpleGrantedAuthority)
//        )
//    }
}
