package com.kamelia.ugeoverflow.question

import com.kamelia.ugeoverflow.utils.Roles
import com.kamelia.ugeoverflow.utils.Routes
import jakarta.validation.Valid
import java.util.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(Routes.Api.Question.ROOT)
class QuestionRestController(
    private val questionService: QuestionService,
) {

    @GetMapping
    fun getPage(
        page: Pageable,
        @Valid
        @RequestBody(required = false)
        filterDTO: QuestionSearchFilterDTO?,
    ): ResponseEntity<Page<QuestionLightDTO>> =
        ResponseEntity.ok(questionService.getPage(page, filterDTO))

    @Secured(Roles.USER)
    @PostMapping
    fun postQuestion(@RequestBody questionDto: PostQuestionDTO): ResponseEntity<QuestionDTO> {
        val questionDTO = questionService.postQuestion(questionDto)
        return ResponseEntity.ok(questionDTO)
    }

    @GetMapping("/{questionId}")
    fun getQuestion(@PathVariable questionId: UUID): ResponseEntity<QuestionDTO> {
        val questionDTO = questionService.getQuestion(questionId)
        return ResponseEntity.ok(questionDTO)
    }

    @Secured(Roles.ADMIN)
    @DeleteMapping("/{questionId}")
    fun deleteQuestion(@PathVariable questionId: UUID): ResponseEntity<Unit> {
        questionService.deleteQuestion(questionId)
        return ResponseEntity.ok().build()
    }

}
