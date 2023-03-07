package com.kamelia.ugeoverflow.user

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/user")
class UserRestController(
    private val userService: UserService,
    private val authenticationManager: AuthenticationManager,
) {

    @PostMapping("/login")
    fun login(@RequestBody @Valid userDTO: UserCredentialsDTO): ResponseEntity<String> {
        ResponseEntity.ok(userService.checkIdentity(userDTO.username, userDTO.password))

        val auth = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                userDTO.username,
                userDTO.password,
            )
        )

        return ResponseEntity.ok(auth.toString())
    }

    @PostMapping("/register")
    fun register(@RequestBody @Valid userDTO: UserCredentialsDTO): ResponseEntity<UserDTO> =
        ResponseEntity.ok(userService.create(userDTO))

}
