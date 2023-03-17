package com.kamelia.ugeoverflow.votes

import com.kamelia.ugeoverflow.utils.Roles
import java.util.*
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/answers")
class VoteRestController(
    private val voteService: VoteService,
) {

    @Secured(Roles.USER)
    @PutMapping("/{answerId}/upvote")
    fun upvoteAnswer(@PathVariable answerId: UUID): ResponseEntity<Unit> {
        voteService.upvoteAnswer(answerId)
        return ResponseEntity.ok().build()
    }

    @Secured(Roles.USER)
    @PutMapping("/{answerId}/downvote")
    fun downvoteAnswer(@PathVariable answerId: UUID): ResponseEntity<Unit> {
        voteService.downvoteAnswer(answerId)
        return ResponseEntity.ok().build()
    }

}
