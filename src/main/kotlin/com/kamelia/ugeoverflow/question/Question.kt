package com.kamelia.ugeoverflow.question

import com.kamelia.ugeoverflow.answer.Answer
import com.kamelia.ugeoverflow.comment.Comment
import com.kamelia.ugeoverflow.core.AbstractCommentablePost
import com.kamelia.ugeoverflow.tag.Tag
import com.kamelia.ugeoverflow.user.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.PastOrPresent
import java.time.Instant

@Entity
@Table(name = "question")
class Question(
    owner: User,
    title: String,
    comments: Set<Comment>,
    content: String,
    answers: Set<Answer>,
    tags: Set<Tag>,
    creationDate: Instant = Instant.now(),
) : AbstractCommentablePost(owner, content, comments) {

    @JoinTable(
        name = "question_answer",
        joinColumns = [JoinColumn(name = "question_id")],
        inverseJoinColumns = [JoinColumn(name = "answer_id")]
    )
    @OneToMany
    @Column(name = "answers")
    private var _answers: MutableSet<Answer> = answers.toMutableSet()

    @JoinTable(
        name = "question_tag",
        joinColumns = [JoinColumn(name = "question_id")],
        inverseJoinColumns = [JoinColumn(name = "tag_id")]
    )
    @OneToMany
    @Column(name = "tags")
    private var _tags: MutableSet<Tag> = tags.toMutableSet()

    init {
        require(title.isNotBlank()) { "Question title cannot be blank" }
        require(content.isNotBlank()) { "Question content cannot be blank" }
        require(!creationDate.isAfter(Instant.now())) { "Question creation date cannot be in the future" }
    }

    @NotBlank
    var title: String = title
        set(value) {
            require(value.isNotBlank()) { "Question title cannot be blank" }
            field = value
        }

    var answers: Set<Answer>
        get() = _answers
        set(value) {
            _answers = value.toMutableSet()
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
            require(!value.isAfter(Instant.now())) { "Question creation date cannot be in the future" }
            field = value
        }

}
