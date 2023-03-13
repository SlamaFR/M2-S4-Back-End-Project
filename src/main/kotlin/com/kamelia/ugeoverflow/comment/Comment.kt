package com.kamelia.ugeoverflow.comment

import com.kamelia.ugeoverflow.core.AbstractIdEntity
import com.kamelia.ugeoverflow.user.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.PastOrPresent
import java.time.Instant

@Entity
@Table(name = "comment")
class Comment(
    @ManyToOne
    var author: User,
    content: String,
) : AbstractIdEntity() {

    init {
        require(content.isNotBlank()) { "Answer content cannot be blank" }
    }

    @NotBlank
    var content: String = content
        set(value) {
            require(value.isNotBlank()) { "Answer content cannot be blank" }
            field = value
        }

    @PastOrPresent
    @Column(name = "creation_date")
    var creationDate: Instant = Instant.now()
        set(value) {
            require(!value.isAfter(Instant.now())) { "Answer creation date cannot be in the future" }
            field = value
        }

}
