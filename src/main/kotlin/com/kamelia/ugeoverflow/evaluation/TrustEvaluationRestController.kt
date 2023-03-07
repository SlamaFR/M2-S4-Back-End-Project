package com.kamelia.ugeoverflow.evaluation

import jakarta.validation.Valid
import java.util.UUID
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/evaluations")
class TrustEvaluationRestController(
    private val trustEvaluationService: TrustEvaluationService,
) {

    @PutMapping("/evaluate/{userId}")
    fun evaluateUser(@RequestBody @Valid trust: Int, @PathVariable userId: UUID): ResponseEntity<Unit> =
        ResponseEntity.ok(trustEvaluationService.evaluateUser(userId, trust))

    @DeleteMapping("/evaluate/{userId}")
    fun removeEvaluation(@PathVariable userId: UUID): ResponseEntity<Unit> =
        ResponseEntity.ok(trustEvaluationService.removeEvaluation(userId))

}