package com.kamelia.ugeoverflow.comment

import com.kamelia.ugeoverflow.AbstractIdEntity
import com.kamelia.ugeoverflow.user.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.PastOrPresent
import java.time.Instant

@Entity
@Table(name = "comment")
class Comment(
    @ManyToOne
    var owner: User,
    content: String,
    votes: Set<User>,
    creationDate: Instant = Instant.now(),
) : AbstractIdEntity() {

    init {
        require(!creationDate.isAfter(Instant.now())) { "Comment creation date cannot be in the future" }
    }

    @JoinTable(
        name = "comment_votes",
        joinColumns = [JoinColumn(name = "comment_id")],
        inverseJoinColumns = [JoinColumn(name = "voter_id")]
    )
    @ManyToMany
    @Column(name = "votes")
    private var _votes: MutableSet<User> = votes.toMutableSet()

    @NotBlank
    var content: String = content
        set(value) {
            require(value.isNotBlank()) { "Comment content cannot be blank" }
            field = value
        }

    @PastOrPresent
    @Column(name = "creation_date")
    var creationDate: Instant = creationDate
        set(value) {
            require(!value.isAfter(Instant.now())) { "Comment creation date cannot be in the future" }
            field = value
        }

    var votes: Set<User>
        get() = _votes
        set(value) {
            _votes = value.toMutableSet()
        }

}
