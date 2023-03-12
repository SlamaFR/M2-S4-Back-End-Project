package com.kamelia.ugeoverflow.answer

import com.kamelia.ugeoverflow.comment.Comment
import com.kamelia.ugeoverflow.comment.CommentDTO
import com.kamelia.ugeoverflow.comment.toDTO
import java.time.Instant
import java.util.UUID

data class AnswerDTO(
    val id: UUID,
    val authorUsername: String,
    val content: String,
    val comments: Set<CommentDTO>,
    val creationDate: Instant,
)

data class PostAnswerDTO(
    val content: String,
)

fun Answer.toDTO() = AnswerDTO(
    id,
    author.username,
    content,
    comments.mapTo(mutableSetOf(), Comment::toDTO),
    creationDate,
)
