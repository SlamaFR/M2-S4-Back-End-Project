package com.kamelia.ugeoverflow.vote

enum class VoteState {
    UPVOTE,
    DOWNVOTE,
    NOT_VOTED,
    ;

    val isUpvote: Boolean
        get() = this == UPVOTE

    val isDownvote: Boolean
        get() = this == DOWNVOTE

}

val Vote.state: VoteState
    get() = if (isUpvote) VoteState.UPVOTE else VoteState.DOWNVOTE
