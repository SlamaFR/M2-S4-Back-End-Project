package com.kamelia.ugeoverflow.user

import com.kamelia.ugeoverflow.core.auth.TokenData
import jakarta.validation.constraints.NotBlank
import java.util.UUID
import org.springframework.validation.annotation.Validated

data class UserDTO(
    val id: UUID,
    val username: String,
    val following: Set<UserDTO>?,
    //val trustEvaluations: Set<TrustEvaluationDTO>,
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
    following.map(User::toDTOWithoutFollowing).toSet(),
    //trustEvaluations = trustEvaluations.map { it.toDTO() }.toSet(),
)

fun User.toDTOWithoutFollowing(): UserDTO = UserDTO(
    id,
    username,
    following = null,
    //trustEvaluations = trustEvaluations.map { it.toDTO() }.toSet(),
)
