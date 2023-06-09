package com.kamelia.ugeoverflow.question

import com.kamelia.ugeoverflow.answer.Answer
import com.kamelia.ugeoverflow.core.AbstractCommentablePost
import com.kamelia.ugeoverflow.tag.Tag
import com.kamelia.ugeoverflow.user.User
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.PastOrPresent
import java.time.Instant

@Entity
@Table(name = "question")
class Question(
    author: User,
    @NotBlank
    @Column(name = "title")
    val title: String,
    content: String,
    tags: Set<Tag>,
) : AbstractCommentablePost(author, content) {

    init {
        require(title.isNotBlank()) { "Question title cannot be blank" }
    }

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "question_id")
    private var _answers: MutableSet<Answer> = mutableSetOf()

    @JoinTable(
        name = "question_tag",
        joinColumns = [JoinColumn(name = "question_id")],
        inverseJoinColumns = [JoinColumn(name = "tag_id")]
    )
    @ManyToMany
    private var _tags: MutableSet<Tag> = tags.toMutableSet()

    @PastOrPresent
    @Column(name = "creation_date")
    val creationDate: Instant = Instant.now()

    val answers: Set<Answer>
        get() = _answers

    val tags: Set<Tag>
        get() = _tags

    fun addAnswer(answer: Answer) {
        _answers.add(answer)
    }

    fun removeAnswer(answer: Answer) {
        _answers.remove(answer)
    }

}
