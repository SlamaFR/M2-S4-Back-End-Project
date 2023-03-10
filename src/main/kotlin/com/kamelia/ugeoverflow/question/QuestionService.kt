package com.kamelia.ugeoverflow.question

import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class QuestionService(
    private val questionRepository: QuestionRepository,
) {

    @Transactional
    fun getPage(page: Pageable): Page<PostLightDTO> = questionRepository.findAll(page).map(Question::toLightDTO)

}
