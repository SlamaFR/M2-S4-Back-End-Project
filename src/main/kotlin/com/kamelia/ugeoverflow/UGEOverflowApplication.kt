package com.kamelia.ugeoverflow

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.http.ResponseEntity
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@EnableMethodSecurity(securedEnabled = true)
@SpringBootApplication(exclude = [UserDetailsServiceAutoConfiguration::class])
class BackendProjectApplication {

   // @Bean
   // fun dummyTagsInsertion(tagRepository: TagRepository): CommandLineRunner = CommandLineRunner { _ ->
   //     tagRepository.saveAll(
   //         listOf("Spring", "Kotlin", "Java", "Web Application").map(::Tag)
   //     )
   // }

}

fun main(args: Array<String>) {
    runApplication<BackendProjectApplication>(*args)
}

@RestController
@RequestMapping("/api/v1/bidon")
class DummyController {

    @GetMapping
    fun getBidon() = ResponseEntity.ok("bidon")

}
