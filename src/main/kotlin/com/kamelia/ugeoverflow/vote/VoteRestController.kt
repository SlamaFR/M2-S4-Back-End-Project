package com.kamelia.ugeoverflow.vote

import com.kamelia.ugeoverflow.utils.Roles
import com.kamelia.ugeoverflow.utils.Routes
import java.util.*
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(Routes.Api.Answer.ROOT)
class VoteRestController(
    private val voteService: VoteService,
) {

    @Secured(Roles.USER)
    @PutMapping("/{answerId}/vote")
    fun voteAnswer(@PathVariable answerId: UUID, @RequestParam("vote") vote: Boolean): ResponseEntity<VoteState> =
        ResponseEntity.ok(voteService.voteAnswer(answerId, vote))

}
