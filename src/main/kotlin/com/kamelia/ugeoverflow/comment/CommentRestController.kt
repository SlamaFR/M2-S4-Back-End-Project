package com.kamelia.ugeoverflow.comment

import com.kamelia.ugeoverflow.utils.Roles
import com.kamelia.ugeoverflow.utils.Routes
import java.util.UUID
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class CommentRestController(
    private val commentService: CommentService,
) {

    @Secured(Roles.USER)
    @PostMapping("${Routes.Api.Question.ROOT}/{questionId}/comments")
    fun postCommentOnQuestion(
        @PathVariable questionId: UUID,
        commentDTO: PostCommentDTO,
    ): ResponseEntity<Unit> =
        ResponseEntity.ok(commentService.postCommentOnQuestion(questionId, commentDTO))

    @Secured(Roles.USER)
    @PostMapping("${Routes.Api.Answer.ROOT}/{answerId}/comments")
    fun postCommentOnAnswer(
        @PathVariable answerId: UUID,
        commentDTO: PostCommentDTO,
    ): ResponseEntity<Unit> =
        ResponseEntity.ok(commentService.postCommentOnAnswer(answerId, commentDTO))

    @Secured(Roles.USER)
    @DeleteMapping("${Routes.Api.Question.ROOT}/{questionId}/comments/{commentId}")
    fun removeCommentFromQuestion(
        @PathVariable questionId: UUID,
        @PathVariable commentId: UUID,
    ): ResponseEntity<Unit> =
        ResponseEntity.ok(commentService.removeCommentFromQuestion(questionId, commentId))

    @Secured(Roles.USER)
    @DeleteMapping("${Routes.Api.Answer.ROOT}/{answerId}/comments/{commentId}")
    fun removeCommentFromAnswer(
        @PathVariable answerId: UUID,
        @PathVariable commentId: UUID,
    ): ResponseEntity<Unit> =
        ResponseEntity.ok(commentService.removeCommentFromAnswer(answerId, commentId))

}
