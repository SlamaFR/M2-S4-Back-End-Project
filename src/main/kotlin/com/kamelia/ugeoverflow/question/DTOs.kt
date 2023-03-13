package com.kamelia.ugeoverflow.question

import com.kamelia.ugeoverflow.comment.Comment
import com.kamelia.ugeoverflow.comment.CommentDTO
import com.kamelia.ugeoverflow.comment.toDTO
import com.kamelia.ugeoverflow.tag.Tag
import com.kamelia.ugeoverflow.tag.TagDTO
import com.kamelia.ugeoverflow.tag.toDTO
import java.time.Instant
import java.util.UUID

data class QuestionLightDTO(
    val id: UUID,
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

data class PostQuestionDTO(
    val title: String,
    val content: String,
    val tags: Set<String>,
)

fun Question.toLightDTO() = QuestionLightDTO(
    id,
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
