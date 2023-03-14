package com.kamelia.ugeoverflow.post

import com.kamelia.ugeoverflow.comment.Comment
import com.kamelia.ugeoverflow.tag.Tag
import com.kamelia.ugeoverflow.tag.TagDTO
import com.kamelia.ugeoverflow.tag.toDTO
import com.kamelia.ugeoverflow.user.User
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.time.Instant
import java.util.*

val user = User(
    "notKamui",
    "aaa"
)

val posts = listOf(
    Post(
        user,
        "How to make a Spring Boot app in Kotlin",
        "?????????????? how pls",
        setOf(
            Comment(user, "I don't know either bro", setOf()),
            Comment(user, "I know but I won't tell", setOf()),
        ),
        setOf(Tag("kotlin"), Tag("spring"), Tag("spring-boot")),
    ),
    Post(
        user,
        "How to make a Spring Boot app in Java",
        "?????????????? how pls",
        setOf(
            Comment(user, "I don't know either bro", setOf()),
            Comment(user, "I know but I won't tell", setOf()),
        ),
        setOf(Tag("java"), Tag("spring"), Tag("spring-boot")),
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
        model.addAttribute("postModel", PostModel(posts.map(Post::toModel)))
        return "post/list"
    }

    @GetMapping("/{id}")
    fun details(
        @PathVariable("id") id: UUID,
        model: Model,
    ): String {

        // TODO: Get post from database
        val post = posts.firstOrNull()

        if (post == null) {
            model.addAttribute("errorMessage", "Post not found")
            return "error/404"
        }

        model.addAttribute("post", post.toModel())

        return "post/details"
    }

    @GetMapping("/vote/{id}/{commentId}")
    fun voteComment(
        @PathVariable("id") id: UUID,
        @PathVariable("commentId") commentId: UUID,
        @RequestParam("vote", required = true) vote: Boolean,
        model: Model,
    ): String {
        // TODO: Update comment vote in database

        return "redirect:/post/$id"
    }
}

class PostModel(
    val posts: List<PostLightModel>,
)

class PostLightModel(
    val owner: String,
    val title: String,
    val content: String,
    val tags: Set<TagModel>,
    val comments: Set<CommentModel>,
    val creationDate: Date,
    val id: UUID = UUID.randomUUID(), // TODO: Get id from database
)

class CommentModel(
    val owner: String,
    val content: String,
    val creationDate: Date,
    val id: UUID = UUID.randomUUID(), // TODO: Get id from database
)

class TagModel(
    val name: String,
    val id: UUID = UUID.randomUUID(), // TODO: Get id from database
)

fun Post.toModel() = PostLightModel(
    owner.username,
    title,
    content,
    tags.map(Tag::toModel).toSet(),
    comments.map(Comment::toModel).toSet(),
    Date.from(creationDate),
)

fun Comment.toModel() = CommentModel(
    owner.username,
    content,
    Date.from(creationDate)
)

fun Tag.toModel() = TagModel(name)