package com.kamelia.ugeoverflow.session

import com.kamelia.ugeoverflow.core.InvalidRequestException
import com.kamelia.ugeoverflow.core.MvcController
import com.kamelia.ugeoverflow.core.toUUIDFromBase64OrNull
import com.kamelia.ugeoverflow.user.UserCredentialsDTO
import com.kamelia.ugeoverflow.user.UserService
import com.kamelia.ugeoverflow.utils.*
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Controller
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@MvcController
@Controller
@RequestMapping("/auth")
class AuthController(
    val userService: UserService,
    val sessionManager: SessionManager,
) {

    @GetMapping
    fun auth(
        @ModelAttribute("loginForm") loginForm: LoginForm,
        @ModelAttribute("registerForm") registerForm: RegisterForm,
    ): String = "session/auth"

    @PostMapping("/login")
    fun login(
        @Valid @ModelAttribute("loginForm") loginForm: LoginForm,
        binding: BindingResult,
        response: HttpServletResponse
    ): String {
        if (binding.hasErrors()) return "redirect:/auth"

        sessionManager
            .login(loginForm.username, loginForm.password)
            .toCookies()
            .forEach(response::addCookie)

        return "redirect:/"
    }

    @PostMapping("/register")
    fun register(
        @Valid @ModelAttribute("registerForm") registerForm: RegisterForm,
        binding: BindingResult,
        response: HttpServletResponse
    ): String {
        if (binding.hasErrors() || registerForm.password != registerForm.passwordConfirm) {
            return "redirect:/auth"
        }

        userService.create(UserCredentialsDTO(registerForm.username, registerForm.password))
        sessionManager
            .login(registerForm.username, registerForm.password)
            .toCookies()
            .forEach(response::addCookie)

        return "redirect:/"
    }

    @Secured(Roles.USER)
    @GetMapping("/logout")
    fun logout(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): String {
        val tokens = currentAuth().tokens
        val refreshTokenUUID = request.cookies
            .firstOrNull { it.name == Cookies.REFRESH_TOKEN }
            ?.value
            ?.toUUIDFromBase64OrNull()
            ?: throw InvalidRequestException.unauthorized("Invalid credentials")

        sessionManager.logout(tokens.userId, tokens.accessToken, refreshTokenUUID)

        response.removeCookie(Cookies.USER_ID)
        response.removeCookie(Cookies.ACCESS_TOKEN)
        response.removeCookie(Cookies.REFRESH_TOKEN, Routes.Auth.REFRESH)

        return "redirect:/"
    }

    @Secured(Roles.USER)
    @GetMapping("/refresh")
    fun refresh(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): String {
        val tokens = currentAuth().refreshedTokens
        tokens.toCookies().forEach(response::addCookie)
        return "redirect:${request.getHeader("referer") ?: "/"}"
    }
}

class LoginForm(
    @NotBlank @Pattern(regexp = "[a-zA-Z0-9_]+")
    var username: String = "",
    @NotBlank
    var password: String = "",
)

class RegisterForm(
    @NotBlank @Pattern(regexp = "[a-zA-Z0-9_]+")
    var username: String = "",
    @NotBlank
    var password: String = "",
    @NotBlank
    var passwordConfirm: String = "",
)
