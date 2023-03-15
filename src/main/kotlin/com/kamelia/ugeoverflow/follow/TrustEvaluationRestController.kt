package com.kamelia.ugeoverflow.follow

import com.kamelia.ugeoverflow.utils.Roles
import jakarta.validation.Valid
import java.util.UUID
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/follows")
class TrustEvaluationRestController(
    private val trustEvaluationService: TrustEvaluationService,
) {

    @Secured(Roles.USER)
    @PostMapping("/{userId}")
    fun followUser(@PathVariable userId: UUID): ResponseEntity<Unit> {
        trustEvaluationService.follow(userId)
        return ResponseEntity.ok().build()
    }

    @Secured(Roles.USER)
    @DeleteMapping("/{userId}")
    fun removeEvaluation(@PathVariable userId: UUID): ResponseEntity<Unit> {
        trustEvaluationService.unfollow(userId)
        return ResponseEntity.ok().build()
    }

    @Secured(Roles.USER)
    @PutMapping("/{userId}")
    fun evaluateUser(@RequestBody @Valid trust: Int, @PathVariable userId: UUID): ResponseEntity<Unit> {
        trustEvaluationService.evaluateFollowed(userId, trust)
        return ResponseEntity.ok().build()
    }

    @GetMapping
    @Secured(Roles.USER)
    fun getFollowedUsers(): ResponseEntity<List<FollowedUserDTO>> {
        val followedUsers = trustEvaluationService.getFollowedUsers()
        return ResponseEntity.ok(followedUsers)
    }

}
