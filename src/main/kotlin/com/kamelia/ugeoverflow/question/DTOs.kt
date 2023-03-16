package com.kamelia.ugeoverflow.question

import com.fasterxml.jackson.annotation.JsonInclude
import com.kamelia.ugeoverflow.comment.Comment
import com.kamelia.ugeoverflow.comment.CommentDTO
import com.kamelia.ugeoverflow.comment.toDTO
import com.kamelia.ugeoverflow.tag.Tag
import com.kamelia.ugeoverflow.tag.TagDTO
import com.kamelia.ugeoverflow.tag.toDTO
import com.kamelia.ugeoverflow.user.UserLightDTO
import com.kamelia.ugeoverflow.user.toLightDTO
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import java.time.Instant
import java.util.*
import org.springframework.validation.annotation.Validated

data class QuestionLightDTO(
    val id: UUID,
    val author: UserLightDTO,
    val title: String,
    val tags: Set<TagDTO>,
    val creationDate: Instant,
)


data class QuestionDTO(
    val id: UUID,
    val authorUsername: String,
    val title: String,
    val content: String,
    val comments: Set<CommentDTO>,
    val tags: Set<TagDTO>,
    val creationDate: Instant,
)

@Validated
data class PostQuestionDTO(
    @NotBlank
    val title: String,
    @NotBlank
    val content: String,
    @Valid
    val tags: Set<@NotBlank String>,
)

@Validated
data class QuestionSearchFilterDTO(
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val title: String? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val tags: Set<@NotBlank String>? = null,
)

fun Question.toLightDTO() = QuestionLightDTO(
    id,
    author.toLightDTO(),
    title,
    tags.mapTo(mutableSetOf(), Tag::toDTO),
    creationDate,
)

fun Question.toDTO() = QuestionDTO(
    id,
    author.username,
    title,
    content,
    comments.mapTo(mutableSetOf(), Comment::toDTO),
    tags.mapTo(mutableSetOf(), Tag::toDTO),
    creationDate,
)
