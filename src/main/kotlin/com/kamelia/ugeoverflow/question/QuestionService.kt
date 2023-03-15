package com.kamelia.ugeoverflow.question

import com.kamelia.ugeoverflow.core.InvalidRequestException
import com.kamelia.ugeoverflow.utils.currentUser
import jakarta.transaction.Transactional
import java.util.UUID
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class QuestionService(
    private val questionRepository: QuestionRepository,
) {

    @Transactional
    fun getPage(page: Pageable): Page<QuestionLightDTO> = questionRepository.findAll(page).map(Question::toLightDTO)

    @Transactional
    fun postQuestion(questionDto: PostQuestionDTO): QuestionDTO {
        val author = currentUser()

        val question = Question(
            author = author,
            title = questionDto.title,
            content = questionDto.content,
        )
        // TODO : add tags
        return questionRepository.save(question).toDTO()
    }

    @Transactional
    fun deleteQuestion(id: UUID) {
        val currentUser = currentUser()
        val question = questionRepository.findById(id).orElseThrow {
            InvalidRequestException.notFound("Question not found")
        }
        if (question.author != currentUser) {
            throw InvalidRequestException.forbidden("You are not the author of this questions")
        }
        questionRepository.delete(question)
    }
}
