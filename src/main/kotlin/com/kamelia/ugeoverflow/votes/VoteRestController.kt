package com.kamelia.ugeoverflow.votes

import com.kamelia.ugeoverflow.utils.Roles
import java.util.UUID
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class VoteRestController(
    private val voteService: VoteService,
) {

    @Secured(Roles.USER)
    @PutMapping("/answers/{answerId}/upvote")
    fun upvoteAnswer(@PathVariable answerId: UUID): ResponseEntity<Unit> =
        ResponseEntity.ok(voteService.upvoteAnswer(answerId))

    @Secured(Roles.USER)
    @PutMapping("/answers/{answerId}/downvote")
    fun downvoteAnswer(@PathVariable answerId: UUID): ResponseEntity<Unit> =
        ResponseEntity.ok(voteService.downvoteAnswer(answerId))

}
