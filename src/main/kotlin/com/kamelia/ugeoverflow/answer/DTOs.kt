package com.kamelia.ugeoverflow.answer

import com.kamelia.ugeoverflow.comment.Comment
import com.kamelia.ugeoverflow.comment.CommentDTO
import com.kamelia.ugeoverflow.comment.toDTO
import com.kamelia.ugeoverflow.user.UserLightDTO
import com.kamelia.ugeoverflow.user.toLightDTO
import java.time.Instant
import java.util.UUID

data class AnswerDTO(
    val id: UUID,
    val author: UserLightDTO,
    val content: String,
    val comments: List<CommentDTO>,
    val creationDate: Instant,
    val note: Int,
)

data class PostAnswerDTO(
    val content: String,
)

fun Answer.toDTO(
    computedNote: Int = votes.sumOf { (if (it.isUpvote) 1 else 0).toInt() },
) = AnswerDTO(
    id,
    author.toLightDTO(),
    content,
    postComments.map(Comment::toDTO).sortedByDescending { it.creationDate },
    creationDate,
    computedNote
)
