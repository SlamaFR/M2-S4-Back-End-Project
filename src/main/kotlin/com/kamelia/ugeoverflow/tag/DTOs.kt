package com.kamelia.ugeoverflow.tag

data class TagDTO(
    val name: String,
)

fun Tag.toDTO() = TagDTO(name)
