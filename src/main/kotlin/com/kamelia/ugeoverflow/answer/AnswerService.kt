package com.kamelia.ugeoverflow.answer

import com.kamelia.ugeoverflow.core.InvalidRequestException
import com.kamelia.ugeoverflow.question.QuestionRepository
import com.kamelia.ugeoverflow.utils.currentUser
import jakarta.transaction.Transactional
import java.util.UUID
import org.springframework.stereotype.Service

@Service
class AnswerService(
    private val questionRepository: QuestionRepository,
    private val answerRepository: AnswerRepository,
) {

    @Transactional
    fun postAnswer(questionId: UUID, answerDto: PostAnswerDTO): AnswerDTO {
        val author = currentUser()
        val question = questionRepository.findById(questionId).orElseThrow {
            InvalidRequestException.notFound("Question not found")
        }

        val answer = Answer(author, answerDto.content)
        answerRepository.save(answer)
        question.addAnswer(answer)
        return answer.toDTO()
    }

    @Transactional
    fun deleteAnswer(answerId: UUID) {
        val currentUser = currentUser()
        val answer = answerRepository.findById(answerId).orElseThrow {
            InvalidRequestException.notFound("Answer not found")
        }
        if (answer.author != currentUser) {
            throw InvalidRequestException.forbidden("You are not the author of this answer")
        }
        answerRepository.delete(answer)
    }

}
