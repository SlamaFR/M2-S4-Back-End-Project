package com.kamelia.ugeoverflow.question

import com.kamelia.ugeoverflow.answer.AnswerDTO
import com.kamelia.ugeoverflow.comment.CommentDTO
import com.kamelia.ugeoverflow.tag.TagDTO
import com.kamelia.ugeoverflow.user.User
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import java.time.Instant
import java.util.*

val user = User(
    "notKamui",
    "aaa"
)

val questions = listOf(
    QuestionDTO(
        UUID.randomUUID(),
        user.username,
        "How to make a good questions?",
        "I want to know how to make a good questions",
        setOf(
            AnswerDTO(
                UUID.randomUUID(),
                user.username,
                "You should use a good title and a good description",
                setOf(
                    CommentDTO(
                        UUID.randomUUID(),
                        user.username,
                        "I agree",
                        Instant.now(),
                    ),
                ),
                Instant.now(),
            ),
        ),
        setOf(
            TagDTO("questions"),
        ),
        Instant.now()
    ),
    QuestionDTO(
        UUID.randomUUID(),
        user.username,
        "How to make a good answer?",
        "I want to know how to make a good answer",
        setOf(
            AnswerDTO(
                UUID.randomUUID(),
                user.username,
                "Git gud",
                setOf(),
                Instant.now(),
            ),
        ),
        setOf(
            TagDTO("answer"),
        ),
        Instant.now()
    ),
)

@Controller
@RequestMapping("/question")
class QuestionMVController {

    @GetMapping
    fun list(
        @RequestParam("page", required = false) page: Int?,
        model: Model,
    ): String {
        // TODO: Get posts from database with page (?)
        model.addAttribute("questionsModel", QuestionsModel(questions))
        return "question/list"
    }

    @GetMapping("/{id}")
    fun details(
        @PathVariable("id") id: UUID,
        @Valid @ModelAttribute("commentForm") commentForm: CommentForm,
        model: Model,
    ): String {

        // TODO: Get question from database
        val question = questions.firstOrNull { it.id == id }

        if (question == null) {
            model.addAttribute("errorMessage", "Question not found")
            return "error/404"
        }

        model.addAttribute("question", question)

        return "question/details"
    }

    @PostMapping("/vote/{id}/{commentId}")
    fun voteComment(
        @PathVariable("id") id: UUID,
        @PathVariable("commentId") commentId: UUID,
        @RequestParam("vote", required = true) vote: Boolean,
        model: Model,
    ): String {
        // TODO: Update comment vote in database

        return "redirect:/question/$id"
    }

    @PostMapping("/comment/{id}")
    fun comment(
        @PathVariable("id") id: UUID,
        @Valid @ModelAttribute("commentForm") commentForm: CommentForm,
        model: Model,
        bindingResult: BindingResult,
    ): String {
        if (bindingResult.hasErrors()) {
            model.addAttribute("commentErrorMessage", "Invalid comment")
            return "redirect:/question/$id"
        }

        // TODO add comment to database
        println(commentForm.content)

        return "redirect:/question/$id"
    }

    @GetMapping("/create")
    fun createForm(
        @Valid @ModelAttribute("createQuestionForm") createQuestionForm: CreateQuestionForm,
        model: Model,
    ): String {
        // TODO get tags from database
        model.addAttribute("tags", listOf(TagDTO("questions"), TagDTO("answer")))

        return "question/create"
    }

    @PostMapping("/create")
    fun create(
        @Valid @ModelAttribute("createQuestionForm") createQuestionForm: CreateQuestionForm,
        model: Model,
        bindingResult: BindingResult,
    ): String {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "Invalid question")
            return "redirect:/question/create"
        }

        // TODO add question to database
        println(createQuestionForm.title)
        println(createQuestionForm.content)
        println(createQuestionForm.tags)

        return "redirect:/question"
    }
}

class CommentForm(
    @NotBlank
    val content: String = "",
)

class CreateQuestionForm(
    @NotBlank
    val title: String = "",
    @NotBlank
    val content: String = "",
    @Valid
    val tags: Set<@NotBlank String> = setOf(),
)

class QuestionsModel(
    val questions: List<QuestionDTO>
)