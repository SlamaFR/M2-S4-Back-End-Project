package com.kamelia.ugeoverflow.follow

import java.util.*

data class FollowedUserDTO(
    val id: UUID,
    val username: String,
    val trust: Int,
) {

    init {
        require(trust in -50..50) { "Trust must be between -50 and 50, was $trust" }
    }

}

fun FollowedUser.toDTO() = FollowedUserDTO(followed.id, followed.username, trust)
