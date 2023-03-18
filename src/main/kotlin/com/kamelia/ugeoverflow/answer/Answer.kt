package com.kamelia.ugeoverflow.answer

import com.kamelia.ugeoverflow.core.AbstractCommentablePost
import com.kamelia.ugeoverflow.user.User
import com.kamelia.ugeoverflow.vote.Vote
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.validation.constraints.PastOrPresent
import java.time.Instant

@Entity
@Table(name = "answer")
class Answer(
    author: User,
    content: String,
) : AbstractCommentablePost(author, content) {

    init {
        require(content.isNotBlank()) { "Answer content cannot be blank" }
    }

    @OneToMany
    @JoinColumn(name = "answer_id")
    private var _votes: MutableSet<Vote> = mutableSetOf()

    @PastOrPresent
    @Column(name = "creation_date")
    var creationDate: Instant = Instant.now()
        set(value) {
            require(!value.isAfter(Instant.now())) { "Answer creation date cannot be in the future" }
            field = value
        }

    val votes: Set<Vote>
        get() = _votes

    fun addVote(vote: Vote) {
        _votes.add(vote)
    }

    fun removeVote(vote: Vote) {
        _votes.remove(vote)
    }
}
