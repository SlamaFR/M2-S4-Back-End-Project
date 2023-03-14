package com.kamelia.ugeoverflow.session

import java.util.UUID

data class UserTokensDTO(
    val userId: UUID,
    val accessToken: UUID,
)
