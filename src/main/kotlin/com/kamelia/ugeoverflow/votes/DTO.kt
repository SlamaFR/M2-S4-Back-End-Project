package com.kamelia.ugeoverflow.votes

data class VoteDTO(
    val isUpvote: Boolean,
)

fun Vote.toDTO() = VoteDTO(
    isUpvote = isUpvote,
)
