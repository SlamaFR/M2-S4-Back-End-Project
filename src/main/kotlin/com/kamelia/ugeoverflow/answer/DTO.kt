package com.kamelia.ugeoverflow.answer

import com.kamelia.ugeoverflow.comment.Comment
import com.kamelia.ugeoverflow.comment.CommentDTO
import com.kamelia.ugeoverflow.comment.toDTO
import java.time.Instant
import java.util.UUID

class AnswerDTO(
    id: UUID,
    authorUsername: String,
    content: String,
    var comments: Set<CommentDTO>,
    creationDate: Instant,
) : CommentDTO(id, authorUsername, content, creationDate)

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
