package com.kamelia.ugeoverflow.user

import com.kamelia.ugeoverflow.follow.FollowedUser
import com.kamelia.ugeoverflow.follow.FollowedUserDTO
import com.kamelia.ugeoverflow.follow.toDTO
import jakarta.validation.constraints.NotBlank
import java.util.*
import org.springframework.validation.annotation.Validated

data class UserDTO(
    val id: UUID,
    val username: String,
    val followed: Set<FollowedUserDTO>,
    val postedQuestionCount: Int,
)

data class UserLightDTO(
    val id: UUID,
    val username: String,
)

@Validated
data class UserCredentialsDTO(
    @NotBlank
    val username: String,
    @NotBlank
    val password: String,
)

fun User.toDTO(): UserDTO = UserDTO(
    id,
    username,
    followed.mapTo(mutableSetOf(), FollowedUser::toDTO),
    postedQuestionCount,
)

fun User.toLightDTO(): UserLightDTO = UserLightDTO(
    id,
    username,
)
