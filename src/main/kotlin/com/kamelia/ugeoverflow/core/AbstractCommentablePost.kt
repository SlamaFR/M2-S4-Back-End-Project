package com.kamelia.ugeoverflow.core

import com.kamelia.ugeoverflow.comment.Comment
import com.kamelia.ugeoverflow.user.User
import jakarta.persistence.Column
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.OneToMany
import jakarta.validation.constraints.NotBlank

@MappedSuperclass
abstract class AbstractCommentablePost(
    @ManyToOne
    var author: User,
    content: String,
) : AbstractIdEntity() {

    init {
        require(content.isNotBlank()) { "Post content cannot be blank" }
    }

    @NotBlank
    @Column(name = "content")
    private var _content: String = content

    @OneToMany
    @JoinColumn(name = "parentId")
    private var _comments: MutableSet<Comment> = mutableSetOf()

    val content: String
        get() = _content

    val comments: Set<Comment>
        get() = _comments

    fun addComment(comment: Comment) {
        _comments.add(comment)
    }

    fun removeComment(comment: Comment) {
        _comments.remove(comment)
    }

}
