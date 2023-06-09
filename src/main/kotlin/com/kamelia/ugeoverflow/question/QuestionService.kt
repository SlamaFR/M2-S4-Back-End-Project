package com.kamelia.ugeoverflow.question

import com.kamelia.ugeoverflow.core.InvalidRequestException
import com.kamelia.ugeoverflow.tag.TagService
import com.kamelia.ugeoverflow.utils.currentUser
import com.kamelia.ugeoverflow.utils.currentUserOrNull
import jakarta.transaction.Transactional
import java.util.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class QuestionService(
    private val questionRepository: QuestionRepository,
    private val tagService: TagService,
    private val questionPageService: QuestionPageListService,
    private val questionPageAnswerSortingService: QuestionPageAnswerSortingService,
) {

    fun getPage(page: Pageable, filterDTO: QuestionSearchFilterDTO?): Page<QuestionLightDTO> =
        currentUserOrNull()?.let {
            questionPageService.questionsAsUser(it, page, filterDTO)
        } ?: questionPageService.questionsAsAnonymous(page, filterDTO)

    @Transactional
    fun postQuestion(questionDto: PostQuestionDTO): QuestionDTO {
        val author = currentUser()

        val tags = tagService.mapStringsToEntities(questionDto.tags)
        if (tags.size != questionDto.tags.size) {
            throw InvalidRequestException.badRequest("Some tags are invalid")
        }

        val question = Question(
            author,
            questionDto.title,
            questionDto.content,
            tags,
        )

        return questionRepository.save(question).toDTO()
    }

    @Transactional
    fun deleteQuestion(id: UUID) {
        val question = questionRepository.findById(id).orElseThrow {
            InvalidRequestException.notFound("Question not found")
        }
        questionRepository.delete(question)
    }

    @Transactional
    fun getQuestion(questionId: UUID): QuestionDTO = currentUserOrNull()?.let {
        questionPageAnswerSortingService.getQuestion(it, questionId)
    } ?: questionPageAnswerSortingService.getQuestionAsAnonymous(questionId)

}
