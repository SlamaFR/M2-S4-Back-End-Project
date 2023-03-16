package com.kamelia.ugeoverflow

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity

@EnableMethodSecurity(securedEnabled = true)
@SpringBootApplication(exclude = [UserDetailsServiceAutoConfiguration::class])
class BackendProjectApplication {

//    @Bean
//    fun dummyTagsInsertion(tagRepository: TagRepository): CommandLineRunner = CommandLineRunner { _ ->
//        tagRepository.saveAll(
//            listOf("Spring", "Kotlin", "Java", "Web Application").map(::Tag)
//        )
//    }

}

fun main(args: Array<String>) {
    runApplication<BackendProjectApplication>(*args)
}
