package com.kamelia.ugeoverflow.session

import java.util.UUID

/**
 * DTO containing tokens passed to by the client on each authenticated request. Inserted into the
 * [Authentication][org.springframework.security.core.Authentication] by the authentication filter.
 */
data class UserTokensDTO(
    val userId: UUID,
    val accessToken: UUID,
)
