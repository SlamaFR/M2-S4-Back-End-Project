package com.kamelia.ugeoverflow.user

import jakarta.validation.constraints.NotBlank
import org.springframework.validation.annotation.Validated
import java.util.*

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
 emptySet()//    followed.map(User::toDTOWithoutFollowing).toSet(),
    //trustEvaluations = trustEvaluations.map { it.toDTO() }.toSet(),
)

fun User.toDTOWithoutFollowing(): UserDTO = UserDTO(
    id,
    username,
    following = null,
    //trustEvaluations = trustEvaluations.map { it.toDTO() }.toSet(),
)
