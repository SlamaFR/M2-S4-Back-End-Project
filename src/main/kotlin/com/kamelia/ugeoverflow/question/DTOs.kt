package com.kamelia.ugeoverflow.question

import com.fasterxml.jackson.annotation.JsonInclude
import com.kamelia.ugeoverflow.answer.Answer
import com.kamelia.ugeoverflow.answer.AnswerDTO
import com.kamelia.ugeoverflow.answer.toDTO
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
    val author: UserLightDTO,
    val title: String,
    val content: String,
    val answers: List<AnswerDTO>,
    val comments: List<CommentDTO>,
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

inline fun Question.toDTO(
    answerSorter: (Set<Answer>) -> List<AnswerDTO> = { it.map(Answer::toDTO) },
) = QuestionDTO(
    id,
    author.toLightDTO(),
    title,
    content,
    answers.let(answerSorter),
    postComments.map(Comment::toDTO).sortedByDescending { it.creationDate },
    tags.mapTo(mutableSetOf(), Tag::toDTO),
    creationDate,
)
