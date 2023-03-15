package com.kamelia.ugeoverflow.question

import com.kamelia.ugeoverflow.core.InvalidRequestException
import com.kamelia.ugeoverflow.tag.Tag
import com.kamelia.ugeoverflow.tag.TagRepository
import com.kamelia.ugeoverflow.tag.TagService
import com.kamelia.ugeoverflow.utils.currentUser
import jakarta.transaction.Transactional
import java.util.UUID
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class QuestionService(
    private val questionRepository: QuestionRepository,
    private val tagService: TagService,
) {

    @Transactional
    fun getPage(page: Pageable): Page<QuestionLightDTO> {
        return questionRepository.findAll(page).map(Question::toLightDTO)
    }

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
        val currentUser = currentUser()
        val question = questionRepository.findById(id).orElseThrow {
            InvalidRequestException.notFound("Question not found")
        }
        if (question.author != currentUser) {
            throw InvalidRequestException.forbidden("You are not the author of this question")
        }
        questionRepository.delete(question)
    }
}
