package com.kamelia.ugeoverflow.votes

import com.kamelia.ugeoverflow.core.AbstractIdEntity
import com.kamelia.ugeoverflow.user.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "vote")
class Vote(
    voter: User,
    isUpvote: Boolean,
) : AbstractIdEntity() {

    @ManyToOne
    @JoinColumn(name = "voter")
    private var _voter: User = voter

    val voter: User
        get() = _voter

    @Column(name = "is_upvote")
    var isUpvote: Boolean = isUpvote

}
