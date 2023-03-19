package com.kamelia.ugeoverflow.user

import com.kamelia.ugeoverflow.core.InvalidRequestException
import com.kamelia.ugeoverflow.core.toUUIDFromBase64OrNull
import com.kamelia.ugeoverflow.session.SessionManager
import com.kamelia.ugeoverflow.session.TokensDTO
import com.kamelia.ugeoverflow.utils.Roles
import com.kamelia.ugeoverflow.utils.Routes
import com.kamelia.ugeoverflow.utils.currentAuth
import com.kamelia.ugeoverflow.utils.refreshedTokens
import com.kamelia.ugeoverflow.utils.tokens
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class UserRestController(
    private val sessionManager: SessionManager,
    private val userService: UserService,
) {

    @PostMapping(Routes.Api.User.REGISTER)
    fun register(@RequestBody @Valid userDTO: UserCredentialsDTO): ResponseEntity<UserDTO> =
        ResponseEntity.ok(userService.create(userDTO))

    @PostMapping(Routes.Api.User.LOGIN)
    fun login(
        @RequestBody @Valid userDTO: UserCredentialsDTO,
        response: HttpServletResponse,
    ): ResponseEntity<TokensDTO> {
        val tokens = sessionManager.login(userDTO.username, userDTO.password)
        return ResponseEntity.ok(tokens)
    }

    @Secured(Roles.USER)
    @PostMapping(Routes.Api.User.LOGOUT)
    fun logout(@RequestBody refreshToken: String): ResponseEntity<Nothing> {
        val tokens = currentAuth().tokens
        val refreshTokenUUID = refreshToken.toUUIDFromBase64OrNull()
            ?: throw InvalidRequestException.unauthorized("Invalid credentials")
        sessionManager.logout(tokens.userId, tokens.accessToken, refreshTokenUUID)
        return ResponseEntity.noContent().build()
    }

    @Secured(Roles.USER)
    @PostMapping(Routes.Api.User.LOGOUT_ALL)
    fun logoutAll(): ResponseEntity<Nothing> {
        val tokens = currentAuth().tokens
        sessionManager.logoutAll(tokens.userId)
        return ResponseEntity.noContent().build()
    }

    @Secured(Roles.USER)
    @PostMapping(Routes.Api.User.REFRESH)
    fun refresh(): ResponseEntity<TokensDTO> {
        val tokens = currentAuth().refreshedTokens
        return ResponseEntity.ok(tokens)
    }

    @Secured(Roles.USER)
    @GetMapping("${Routes.Api.User.ROOT}/me")
    fun me(): ResponseEntity<UserDTO> = ResponseEntity.ok(userService.currentUserInformation())

    @Secured(Roles.USER)
    @PostMapping(Routes.Api.User.UPDATE_PASSWORD)
    fun updatePassword(@RequestBody @Valid passwordDTO: PasswordUpdateDTO): ResponseEntity<Nothing> {
        userService.updatePassword(passwordDTO)
        return ResponseEntity.ok().build()
    }

}
