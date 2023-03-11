package com.kamelia.ugeoverflow.comment

import com.kamelia.ugeoverflow.answer.AnswerRepository
import com.kamelia.ugeoverflow.core.AbstractCommentablePost
import com.kamelia.ugeoverflow.core.InvalidRequestException
import com.kamelia.ugeoverflow.question.QuestionRepository
import com.kamelia.ugeoverflow.utils.currentUser
import java.util.UUID
import org.springframework.stereotype.Service

@Service
class CommentService(
    private val commentRepository: CommentRepository,
    private val questionRepository: QuestionRepository,
    private val answerRepository: AnswerRepository
) {

    private fun postComment(commentable: AbstractCommentablePost, commentDTO: PostCommentDTO) {
        val currentUser = currentUser ?: throw InvalidRequestException.unauthorized()

        val comment = Comment(currentUser, commentDTO.content)
        commentRepository.save(comment)
        commentable.addComment(comment)
    }

    fun postCommentOnQuestion(questionId: UUID, commentDTO: PostCommentDTO) {
        val question = questionRepository.findById(questionId).orElseThrow {
            InvalidRequestException.notFound("Question not found")
        }
        postComment(question, commentDTO)
    }

    fun postCommentOnAnswer(answerId: UUID, commentDTO: PostCommentDTO) {
        val answer = answerRepository.findById(answerId).orElseThrow {
            InvalidRequestException.notFound("Answer not found")
        }
        postComment(answer, commentDTO)
    }

    private fun deleteComment(commentable: AbstractCommentablePost, commentId: UUID) {
        val currentUser = currentUser ?: throw InvalidRequestException.unauthorized()
        val comment = commentRepository.findById(commentId).orElseThrow {
            InvalidRequestException.notFound("Comment not found")
        }
        if (comment.author != currentUser) {
            throw InvalidRequestException.forbidden("You are not the author of this comment")
        }
        commentable.removeComment(comment)
        commentRepository.delete(comment)
    }

    fun removeCommentFromQuestion(questionId: UUID, commentId: UUID) {
        val question = questionRepository.findById(questionId).orElseThrow {
            InvalidRequestException.notFound("Question not found")
        }
        deleteComment(question, commentId)
    }

    fun removeCommentFromAnswer(answerId: UUID, commentId: UUID) {
        val answer = answerRepository.findById(answerId).orElseThrow {
            InvalidRequestException.notFound("Answer not found")
        }
        deleteComment(answer, commentId)
    }

}