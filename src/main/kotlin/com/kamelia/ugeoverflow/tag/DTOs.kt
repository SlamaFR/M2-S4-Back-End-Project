package com.kamelia.ugeoverflow.tag

import java.util.UUID

data class TagDTO(
    val id: UUID,
    val name: String,
)

fun Tag.toDTO() = TagDTO(id, name)
