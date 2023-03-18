package com.kamelia.ugeoverflow.core

import com.kamelia.ugeoverflow.comment.Comment
import com.kamelia.ugeoverflow.user.User
import com.kamelia.ugeoverflow.utils.Constants
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToOne
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.OneToMany
import jakarta.validation.constraints.NotBlank

@MappedSuperclass
abstract class AbstractCommentablePost(
    @ManyToOne(fetch = FetchType.EAGER)
    val author: User,
    @NotBlank
    @Column(name = "content", length = Constants.MAX_POST_CONTENT_LENGTH)
    val content: String,
) : AbstractIdEntity() {

    init {
        require(content.isNotBlank()) { "Post content cannot be blank" }
    }

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinTable(
        joinColumns = [JoinColumn(name = "post_id")],
        inverseJoinColumns = [JoinColumn(name = "comment_id")]
    )
    private var comments: MutableSet<Comment> = mutableSetOf()

    val postComments: Set<Comment>
        get() = comments

    fun addComment(comment: Comment) {
        comments.add(comment)
    }

    fun removeComment(comment: Comment) {
        comments.remove(comment)
    }

}
