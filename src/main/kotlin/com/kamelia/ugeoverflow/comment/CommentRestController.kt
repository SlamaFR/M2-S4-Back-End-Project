package com.kamelia.ugeoverflow.comment

import java.util.UUID
import org.springframework.http.ResponseEntity
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

    @PostMapping("/questions/{questionId}/comments")
    fun postCommentOnQuestion(
        @PathVariable questionId: UUID,
        commentDTO: PostCommentDTO,
    ): ResponseEntity<Unit> {
        return ResponseEntity.ok(commentService.postCommentOnQuestion(questionId, commentDTO))
    }

    @PostMapping("/answers/{answerId}/comments")
    fun postCommentOnAnswer(
        @PathVariable answerId: UUID,
        commentDTO: PostCommentDTO,
    ): ResponseEntity<Unit> {
        return ResponseEntity.ok(commentService.postCommentOnAnswer(answerId, commentDTO))
    }

    @DeleteMapping("/questions/{questionId}/comments/{commentId}")
    fun removeCommentFromQuestion(
        @PathVariable questionId: UUID,
        @PathVariable commentId: UUID,
    ): ResponseEntity<Unit> {
        return ResponseEntity.ok(commentService.removeCommentFromQuestion(questionId, commentId))
    }

    @DeleteMapping("/answers/{answerId}/comments/{commentId}")
    fun removeCommentFromAnswer(
        @PathVariable answerId: UUID,
        @PathVariable commentId: UUID,
    ): ResponseEntity<Unit> {
        return ResponseEntity.ok(commentService.removeCommentFromAnswer(answerId, commentId))
    }

}