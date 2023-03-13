package com.kamelia.ugeoverflow.answer

import com.kamelia.ugeoverflow.utils.Roles
import java.util.UUID
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class AnswerRestController(
    private val answerService: AnswerService
) {

    //TODO: Implement paging?
    //@GetMapping("/questions/{questionId}/answers")
    //fun getPage(page: Pageable): ResponseEntity<Page<AnswerDTO>> =
    //    ResponseEntity.ok(answerService.getPage(page))

    @Secured(Roles.USER)
    @PostMapping("/questions/{questionId}/answers")
    fun postAnswer(
        @PathVariable questionId: UUID,
        @RequestBody answerDto: PostAnswerDTO,
    ): ResponseEntity<AnswerDTO> =
        ResponseEntity.ok(answerService.postAnswer(questionId, answerDto))

    @Secured(Roles.USER)
    @DeleteMapping("/answers/{answerId}")
    fun deleteAnswer(@PathVariable answerId: UUID): ResponseEntity<Unit> =
        ResponseEntity.ok(answerService.deleteAnswer(answerId))

}