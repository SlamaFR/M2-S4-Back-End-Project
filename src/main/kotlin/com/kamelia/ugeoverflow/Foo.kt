package com.kamelia.ugeoverflow

import com.kamelia.ugeoverflow.user.UserCredentialsDTO
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.HttpSession
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextHolderStrategy
import org.springframework.security.web.context.HttpSessionSecurityContextRepository
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.filter.OncePerRequestFilter

@RestController
@RequestMapping("/api/v1/foo")
class Foo(
    private val fooService: FooService,
    private val authenticationManager: AuthenticationManager,
) {
    private val securityContextRepository: HttpSessionSecurityContextRepository = HttpSessionSecurityContextRepository()

    @GetMapping("/baz")
    fun baz(): String {
        return "BAZ"
    }

    @Secured("ADMIN")
    @GetMapping("/foo")
    fun foo(auth: Authentication?) = fooService.foo().also { println(auth?.credentials) }

    @Secured("USER")
    @GetMapping("/bar")
    fun bar(request: HttpServletRequest): String {
        println(request.cookies.contentToString())
        println(SecurityContextHolder.getContext().authentication?.credentials)
        println(RequestContextHolder.currentRequestAttributes().sessionId)
        return fooService.bar()
    }


    @PostMapping("/bim")
    fun bim(@RequestBody dto: UserCredentialsDTO, response: HttpServletResponse): ResponseEntity<String> {
        val (username, password) = dto
        val dummy = UsernamePasswordAuthenticationToken(username, password)
        val auth = authenticationManager.authenticate(dummy)
        if (!auth.isAuthenticated) return ResponseEntity.badRequest().build()

        val sessionId = RequestContextHolder.currentRequestAttributes().sessionId
        println(sessionId)
        SecurityContextHolder.getContext().authentication = auth

        val cookie = Cookie("JSESSIONID", sessionId)
        cookie.secure = true
        cookie.isHttpOnly = true
        cookie.path = "/"
        response.addCookie(cookie)
        return ResponseEntity.ok("BIM")
    }

}

@Service
class FooService {

//    @Secured("ADMIN")
    fun foo() = "FOO"

//    @Secured("USER")
    fun bar() = "BAR"
}
