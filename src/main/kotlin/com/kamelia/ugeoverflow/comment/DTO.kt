package com.kamelia.ugeoverflow.comment

import java.time.Instant
import java.util.UUID

data class PostCommentDTO(
    val content: String,
)

open class CommentDTO(
    val id: UUID,
    val authorUsername: String,
    val content: String,
    val creationDate: Instant,
)

fun Comment.toDTO() = CommentDTO(
    id,
    author.username,
    content,
    creationDate,
)
