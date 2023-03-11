package com.kamelia.ugeoverflow.answer

import com.kamelia.ugeoverflow.comment.Comment
import com.kamelia.ugeoverflow.core.AbstractCommentablePost
import com.kamelia.ugeoverflow.user.User
import com.kamelia.ugeoverflow.votes.Vote
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
    comments: Set<Comment>,
    votes: Set<Vote>,
    creationDate: Instant = Instant.now(),
) : AbstractCommentablePost(author, content, comments) {

    init {
        require(!creationDate.isAfter(Instant.now())) { "Answer creation date cannot be in the future" }
    }

    @OneToMany
    @JoinColumn(name = "answerId")
    private var _votes: MutableSet<Vote> = votes.toMutableSet()

    @PastOrPresent
    @Column(name = "creation_date")
    var creationDate: Instant = creationDate
        set(value) {
            require(!value.isAfter(Instant.now())) { "Answer creation date cannot be in the future" }
            field = value
        }

    var votes: Set<Vote>
        get() = _votes
        set(value) {
            _votes = value.toMutableSet()
        }

    fun addVote(vote: Vote) {
        _votes.add(vote)
    }

    fun removeVote(vote: Vote) {
        _votes.remove(vote)
    }
}
