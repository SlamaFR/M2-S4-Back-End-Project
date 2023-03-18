package com.kamelia.ugeoverflow.core

import com.kamelia.ugeoverflow.comment.Comment
import com.kamelia.ugeoverflow.user.User
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.OneToMany
import jakarta.validation.constraints.NotBlank

@MappedSuperclass
abstract class AbstractCommentablePost(
    @ManyToOne(fetch = FetchType.EAGER)
    val author: User,
    @NotBlank
    @Column(name = "content")
    val content: String,
) : AbstractIdEntity() {

    init {
        require(content.isNotBlank()) { "Post content cannot be blank" }
    }

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "parentId")
    private var _comments: MutableSet<Comment> = mutableSetOf()


    val comments: Set<Comment>
        get() = _comments

    fun addComment(comment: Comment) {
        _comments.add(comment)
    }

    fun removeComment(comment: Comment) {
        _comments.remove(comment)
    }

}
