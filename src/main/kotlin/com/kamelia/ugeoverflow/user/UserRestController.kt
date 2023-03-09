package com.kamelia.ugeoverflow.user

import com.kamelia.ugeoverflow.core.auth.SessionManager
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/user")
class UserRestController(
    private val sessionManager: SessionManager,
    private val userService: UserService,
) {

    @PostMapping("/login")
    fun login(
        @RequestBody @Valid userDTO: UserCredentialsDTO,
        response: HttpServletResponse
    ): ResponseEntity<Nothing> {
        val token = sessionManager.login(userDTO.username, userDTO.password)
        token.createCookies().forEach(response::addCookie)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/register")
    fun register(@RequestBody @Valid userDTO: UserCredentialsDTO): ResponseEntity<UserDTO> =
        ResponseEntity.ok(userService.create(userDTO))

}
