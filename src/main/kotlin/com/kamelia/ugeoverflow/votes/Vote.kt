package com.kamelia.ugeoverflow.votes

import com.kamelia.ugeoverflow.AbstractIdEntity
import com.kamelia.ugeoverflow.user.User
import jakarta.persistence.Entity
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "vote")
class Vote(
    @ManyToOne
    var voter: User,
    val isUpvote: Boolean,
) : AbstractIdEntity()
