package com.kamelia.ugeoverflow.comment

import com.kamelia.ugeoverflow.utils.Roles
import java.util.UUID
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class CommentRestController(
    private val commentService: CommentService,
) {

    @Secured(Roles.USER)
    @PostMapping("/questions/{questionId}/comments")
    fun postCommentOnQuestion(
        @PathVariable questionId: UUID,
        commentDTO: PostCommentDTO,
    ): ResponseEntity<Unit> =
        ResponseEntity.ok(commentService.postCommentOnQuestion(questionId, commentDTO))

    @Secured(Roles.USER)
    @PostMapping("/answers/{answerId}/comments")
    fun postCommentOnAnswer(
        @PathVariable answerId: UUID,
        commentDTO: PostCommentDTO,
    ): ResponseEntity<Unit> =
        ResponseEntity.ok(commentService.postCommentOnAnswer(answerId, commentDTO))

    @Secured(Roles.USER)
    @DeleteMapping("/questions/{questionId}/comments/{commentId}")
    fun removeCommentFromQuestion(
        @PathVariable questionId: UUID,
        @PathVariable commentId: UUID,
    ): ResponseEntity<Unit> =
        ResponseEntity.ok(commentService.removeCommentFromQuestion(questionId, commentId))

    @Secured(Roles.USER)
    @DeleteMapping("/answers/{answerId}/comments/{commentId}")
    fun removeCommentFromAnswer(
        @PathVariable answerId: UUID,
        @PathVariable commentId: UUID,
    ): ResponseEntity<Unit> =
        ResponseEntity.ok(commentService.removeCommentFromAnswer(answerId, commentId))

}