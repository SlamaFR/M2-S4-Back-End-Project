package com.kamelia.ugeoverflow.question

import com.kamelia.ugeoverflow.utils.Roles
import java.util.UUID
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
@RequestMapping("/api/v1/questions")
class QuestionRestController(
    private val questionService: QuestionService
) {

    @GetMapping
    fun getPage(page: Pageable): ResponseEntity<Page<QuestionLightDTO>> =
        ResponseEntity.ok(questionService.getPage(page))

    @Secured(Roles.USER)
    @PostMapping
    fun postQuestion(@RequestBody questionDto: PostQuestionDTO): ResponseEntity<QuestionDTO> =
        ResponseEntity.ok(questionService.postQuestion(questionDto))

    @Secured(Roles.USER)
    @DeleteMapping("/{questionId}")
    fun deleteQuestion(@PathVariable questionId: UUID): ResponseEntity<Unit> =
        ResponseEntity.ok(questionService.deleteQuestion(questionId))

}
