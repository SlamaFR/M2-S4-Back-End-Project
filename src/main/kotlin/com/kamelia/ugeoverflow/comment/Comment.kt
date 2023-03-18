package com.kamelia.ugeoverflow.comment

import com.kamelia.ugeoverflow.core.AbstractIdEntity
import com.kamelia.ugeoverflow.user.User
import com.kamelia.ugeoverflow.utils.Constants
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.PastOrPresent
import java.time.Instant

@Entity
@Table(name = "comment")
class Comment(
    author: User,
    content: String,
) : AbstractIdEntity() {

    init {
        require(content.isNotBlank()) { "Answer content cannot be blank" }
    }

    @ManyToOne
    @JoinColumn(name = "author_id")
    private var _author: User = author

    @NotBlank
    @Column(name = "content", length = Constants.MAX_POST_CONTENT_LENGTH)
    private var _content: String = content

    @PastOrPresent
    @Column(name = "creation_date")
    private var _creationDate: Instant = Instant.now()

    val author: User
        get() = _author

    val content: String
        get() = _content

    val creationDate: Instant
        get() = _creationDate

}
