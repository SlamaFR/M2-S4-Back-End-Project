package com.kamelia.ugeoverflow.votes

import java.util.UUID
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class VoteRestController(
    private val voteService: VoteService,
) {

    @PutMapping("/answer/{answerId}/upvote")
    fun upvoteAnswer(@PathVariable answerId: UUID): ResponseEntity<Unit> {
        return ResponseEntity.ok(voteService.upvoteAnswer(answerId))
    }

    @PutMapping("/answer/{answerId}/downvote")
    fun downvoteAnswer(@PathVariable answerId: UUID): ResponseEntity<Unit> {
        return ResponseEntity.ok(voteService.downvoteAnswer(answerId))
    }

}