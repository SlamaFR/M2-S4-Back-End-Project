package com.kamelia.ugeoverflow.votes

import com.kamelia.ugeoverflow.utils.Roles
import java.util.*
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/answers")
class VoteRestController(
    private val voteService: VoteService,
) {

    @Secured(Roles.USER)
    @PutMapping("/{answerId}/vote")
    fun voteAnswer(@PathVariable answerId: UUID, @RequestParam("vote") vote: Boolean): ResponseEntity<Unit> {
        voteService.voteAnswer(answerId, vote)
        return ResponseEntity.ok().build()
    }

}
