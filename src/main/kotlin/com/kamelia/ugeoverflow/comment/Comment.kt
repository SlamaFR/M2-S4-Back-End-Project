package com.kamelia.ugeoverflow.comment

import com.kamelia.ugeoverflow.AbstractIdEntity
import com.kamelia.ugeoverflow.user.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
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
    @PastOrPresent
    creationDate: Instant = Instant.now(),
) : AbstractIdEntity() {

    @ManyToMany
    @Column(name = "votes")
    private var _votes: MutableSet<User> = votes.toMutableSet()

    @NotBlank
    var content: String = content
        set(value) {
            require(value.isNotBlank()) { "Comment content cannot be blank" }
            field = value
        }

    @Column(name = "creation_date")
    var creationDate: Instant = creationDate
        set(value) {
            val now = Instant.now()
            require(!value.isAfter(Instant.now())) { "Comment creation date cannot be in the future" }
            field = value
        }

    var votes: Set<User> = _votes
        get() = _votes
        set(value) {
            field = value
            _votes = value.toMutableSet()
        }

}
