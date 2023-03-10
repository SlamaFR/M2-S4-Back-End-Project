package com.kamelia.ugeoverflow.question

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/posts")
class QuestionRestController(
    private val questionRepository: QuestionRepository,
) {

    @GetMapping
    fun getPage(page: Pageable): Page<PostLightDTO> = questionRepository
        .findAll(page)
        .map(Question::toLightDTO)

}
