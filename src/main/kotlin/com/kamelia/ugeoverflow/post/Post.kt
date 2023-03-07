package com.kamelia.ugeoverflow.post

import com.kamelia.ugeoverflow.AbstractIdEntity
import com.kamelia.ugeoverflow.comment.Comment
import com.kamelia.ugeoverflow.tag.Tag
import com.kamelia.ugeoverflow.user.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.validation.constraints.PastOrPresent
import java.time.Instant
import org.springframework.data.annotation.CreatedDate

@Entity
@Table(name = "post")
class Post(
    @ManyToOne
    var owner: User,
    title: String,
    content: String,
    comments: Set<Comment>,
    tags: Set<Tag>,
    creationDate: Instant = Instant.now(),
) : AbstractIdEntity() {

    @JoinTable(
        name = "post_comment",
        joinColumns = [JoinColumn(name = "post_id")],
        inverseJoinColumns = [JoinColumn(name = "comment_id")]
    )
    @OneToMany
    @Column(name = "comments")
    private var _comments: MutableSet<Comment> = comments.toMutableSet()

    @JoinTable(
        name = "post_tag",
        joinColumns = [JoinColumn(name = "post_id")],
        inverseJoinColumns = [JoinColumn(name = "tag_id")]
    )
    @OneToMany
    @Column(name = "tags")
    private var _tags: MutableSet<Tag> = tags.toMutableSet()

    init {
        require(title.isNotBlank()) { "Post title cannot be blank" }
        require(content.isNotBlank()) { "Post content cannot be blank" }
        require(!creationDate.isAfter(Instant.now())) { "Post creation date cannot be in the future" }
    }

    var title: String = title
        set(value) {
            require(value.isNotBlank()) { "Post title cannot be blank" }
            field = value
        }

    var content: String = content
        set(value) {
            require(value.isNotBlank()) { "Post content cannot be blank" }
            field = value
        }

    var comments: Set<Comment>
        get() = _comments
        set(value) {
            _comments = value.toMutableSet()
        }

    var tags: Set<Tag>
        get() = _tags
        set(value) {
            _tags = value.toMutableSet()
        }

    @PastOrPresent
    @Column(name = "creation_date")
    var creationDate: Instant = creationDate
        set(value) {
            require(!value.isAfter(Instant.now())) { "Post creation date cannot be in the future" }
            field = value
        }

}
