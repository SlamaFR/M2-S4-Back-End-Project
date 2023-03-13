package com.kamelia.ugeoverflow.post

import com.kamelia.ugeoverflow.tag.TagDTO
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.time.Instant
import java.util.*

val posts = listOf(
    PostLightDTO(
        UUID.randomUUID(),
        "How to make a web app with Kotlin and Spring Boot?",
        setOf(
            TagDTO(UUID.randomUUID(), "kotlin"),
            TagDTO(UUID.randomUUID(), "spring-boot"),
            TagDTO(UUID.randomUUID(), "web-app"),
        ),
        Instant.now(),
    ),
    PostLightDTO(
        UUID.randomUUID(),
        "Why Kotlin is better than Java?",
        setOf(
            TagDTO(UUID.randomUUID(), "kotlin"),
            TagDTO(UUID.randomUUID(), "java"),
        ),
        Instant.now(),
    ),
    PostLightDTO(
        UUID.randomUUID(),
        "Why Spring MVC is a fubbing pain",
        setOf(
            TagDTO(UUID.randomUUID(), "spring-mvc"),
            TagDTO(UUID.randomUUID(), "pain"),
            TagDTO(UUID.randomUUID(), "ouch"),
        ),
        Instant.now(),
    ),
)

@Controller
@RequestMapping("/post")
class PostMVController {

    @GetMapping
    fun list(
        @RequestParam("page", required = false) page: Int?,
        model: Model,
    ): String {
        // TODO: Get posts from database with page (?)
        model.addAttribute("postModel", PostModel(posts.map(PostLightDTO::toModel)))
        return "post/list"
    }

    @GetMapping("/{id}")
    fun details(
        @PathVariable("id") id: UUID,
        model: Model,
    ): String {

        // TODO: Get post from database
        val post = posts.firstOrNull { it.id == id }

        if (post == null) {
            model.addAttribute("errorMessage", "Post not found")
            return "error/404"
        }

        model.addAttribute("post", post)

        return "post/details"
    }
}

class PostModel(
    val posts: List<PostLightModel>,
)
class PostLightModel(
    val id: UUID,
    val title: String,
    val tags: Set<TagDTO>,
    val creationDate: Date,
)
fun PostLightDTO.toModel() = PostLightModel(id, title, tags, Date.from(creationDate))