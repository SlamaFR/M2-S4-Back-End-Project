package com.kamelia.ugeoverflow.follow

import com.kamelia.ugeoverflow.utils.Roles
import com.kamelia.ugeoverflow.utils.Routes
import java.util.*
import org.hibernate.validator.constraints.Range
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
@RequestMapping(Routes.Api.Following.ROOT)
class FollowingRestController(
    private val followingService: FollowingService,
) {

    @Secured(Roles.USER)
    @PostMapping("/{userId}")
    fun followUser(@PathVariable userId: UUID): ResponseEntity<Unit> {
        followingService.follow(userId)
        return ResponseEntity.ok().build()
    }

    @Secured(Roles.USER)
    @DeleteMapping("/{userId}")
    fun unfollowUser(@PathVariable userId: UUID): ResponseEntity<Unit> {
        followingService.unfollow(userId)
        return ResponseEntity.ok().build()
    }

    @Secured(Roles.USER)
    @PutMapping("/{userId}")
    fun evaluateFollowedUser(
        @PathVariable userId: UUID,
        @RequestBody @Range(min = 1, max = 20) trust: Int,
    ): ResponseEntity<Unit> {
        followingService.evaluateFollowed(userId, trust)
        return ResponseEntity.ok().build()
    }

    @GetMapping
    @Secured(Roles.USER)
    fun getFollowedUsers(): ResponseEntity<List<FollowedUserDTO>> {
        val followedUsers = followingService.getFollowedUsers()
        return ResponseEntity.ok(followedUsers)
    }

}
