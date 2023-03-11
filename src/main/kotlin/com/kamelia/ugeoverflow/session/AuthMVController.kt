package com.kamelia.ugeoverflow.session

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import org.springframework.stereotype.Controller
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/auth")
class AuthMVController {

    @GetMapping
    fun auth(
        @ModelAttribute("loginForm") loginForm: LoginForm,
        @ModelAttribute("registerForm") registerForm: RegisterForm,
    ): String = "session/auth"

    @PostMapping("/login")
    fun login(
        @Valid @ModelAttribute("loginForm") loginForm: LoginForm,
        binding: BindingResult,
    ): String {
        if (binding.hasErrors()) return "session/auth"

        TODO("Do login")
    }

    @PostMapping("/register")
    fun register(
        @Valid @ModelAttribute("registerForm") registerForm: RegisterForm,
        binding: BindingResult,
    ): String {
        if (binding.hasErrors()) return "session/auth"

        TODO("Do register")
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
