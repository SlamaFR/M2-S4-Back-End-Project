package com.kamelia.ugeoverflow.answer

import com.kamelia.ugeoverflow.utils.Roles
import com.kamelia.ugeoverflow.utils.Routes
import java.util.*
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AnswerRestController(
    private val answerService: AnswerService
) {

    @Secured(Roles.USER)
    @PostMapping("${Routes.Api.Question.ROOT}/{questionId}/answers")
    fun postAnswer(
        @PathVariable questionId: UUID,
        @RequestBody answerDto: PostAnswerDTO,
    ): ResponseEntity<AnswerDTO> = ResponseEntity.ok(answerService.postAnswer(questionId, answerDto))

    @Secured(Roles.USER)
    @DeleteMapping("${Routes.Api.Answer.ROOT}/{answerId}")
    fun deleteAnswer(@PathVariable answerId: UUID): ResponseEntity<Unit> {
        answerService.deleteAnswer(answerId)
        return ResponseEntity.ok().build()
    }

}
