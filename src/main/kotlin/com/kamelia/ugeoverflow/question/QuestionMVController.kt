package com.kamelia.ugeoverflow.question

import com.kamelia.ugeoverflow.core.MvcController
import com.kamelia.ugeoverflow.tag.TagDTO
import com.kamelia.ugeoverflow.user.UserDTO
import com.kamelia.ugeoverflow.user.dummy
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import java.util.*
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

//val questions = listOf(
//    QuestionDTO(
//        UUID.randomUUID(),
//        "ZwenDo",
//        "How to make a good questions?",
//        "I want to know how to make a good questions",
//        setOf(
//            AnswerDTO(
//                UUID.randomUUID(),
//                "Slama",
//                "You should use a good title and a good description",
//                setOf(
//                    CommentDTO(
//                        UUID.randomUUID(),
//                        "notKamui",
//                        "I agree",
//                        Instant.now(),
//                    ),
//                ),
//                Instant.now(),
//            ),
//            AnswerDTO(
//                UUID.randomUUID(),
//                "notKamui",
//                "You should use a good title and a good description",
//                setOf(
//                    CommentDTO(
//                        UUID.randomUUID(),
//                        "Slama",
//                        "I agree",
//                        Instant.now(),
//                    ),
//                ),
//                Instant.now(),
//            ),
//        ),
//        setOf(
//            TagDTO("questions"),
//        ),
//        Instant.now()
//    ),
//    QuestionDTO(
//        UUID.randomUUID(),
//        "notKamui",
//        "How to make a good answer?",
//        "I want to know how to make a good answer",
//        setOf(
//            AnswerDTO(
//                UUID.randomUUID(),
//                "Slama",
//                "Git gud",
//                setOf(),
//                Instant.now(),
//            ),
//        ),
//        setOf(
//            TagDTO("answer"),
//        ),
//        Instant.now()
//    ),
//)
val questions = listOf<QuestionDTO>()
val tags = listOf(TagDTO("questions"), TagDTO("answer"))

@MvcController
@Controller
@RequestMapping("/question")
class QuestionMVController {

    @GetMapping
    fun list(
        @RequestParam("page", required = false) page: Int?,
        @RequestParam("searchName", required = false) searchName: String?,
        @RequestParam("searchTags", required = false) searchTags: List<String>?,
        model: Model,
    ): String {
        println(searchName)
        println(searchTags)
        // TODO get tags from database
        model.addAttribute("tags", tags)
        // TODO: Get posts from database with page (?) and search filters
        model.addAttribute("questionsModel", QuestionsModel(questions))

        return "question/list"
    }

    @GetMapping("/{id}")
    fun details(
        @PathVariable("id") id: UUID,
        @Valid @ModelAttribute("answerForm") answerForm: CommentForm,
        @Valid @ModelAttribute("commentForm") commentForm: CommentForm,
        model: Model,
    ): String {
        // TODO get user from database
        val user: UserDTO? = dummy

        // TODO: Get question from database
        val question = questions.firstOrNull { it.id == id }

        if (question == null) {
            model.addAttribute("errorMessage", "Question not found")
            return "error/404"
        }

        val followButtonsToHide = mutableMapOf<String, Boolean>()
        user?.followed?.forEach { followButtonsToHide[it.username] = true }
        user?.let { followButtonsToHide[it.username] = true }

        val deleteButtonsToShow = mutableMapOf<UUID, Boolean>()
        question.comments.forEach { deleteButtonsToShow[it.id] = it.authorUsername == user?.username }

        model.addAttribute("question", question)
        model.addAttribute("hideFollowForUser", HideFollowModel(followButtonsToHide))
        model.addAttribute("showDeleteForUser", ShowDeleteModel(deleteButtonsToShow))

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

    @PostMapping("/answer/{id}")
    fun answer(
        @PathVariable("id") id: UUID,
        @Valid @ModelAttribute("answerForm") answerForm: CommentForm,
        model: Model,
        bindingResult: BindingResult,
    ): String {
        if (bindingResult.hasErrors()) {
            model.addAttribute("answerErrorMessage", "Invalid answer")
            return "redirect:/question/$id"
        }

        // TODO add comment to database
        println(answerForm.content)

        return "redirect:/question/$id"
    }

    @PostMapping("/answer/delete/{id}")
    fun deleteAnswer(
        @PathVariable("id") id: UUID,
        request: HttpServletRequest,
    ): String {
        // TODO verify user is owner of answer
        // TODO delete answer from database
        println("Delete answer $id")
        return "redirect:${request.getHeader("referer")}"
    }

    @PostMapping("/answer/{id}/{commentId}")
    fun comment(
        @PathVariable("id") id: UUID,
        @PathVariable("commentId") commentId: UUID,
        @Valid @ModelAttribute("commentForm") commentForm: CommentForm,
        model: Model,
        bindingResult: BindingResult,
    ): String {
        if (bindingResult.hasErrors()) {
            model.addAttribute("commentErrorMessages", mapOf(commentId to "Invalid comment"))
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
        model.addAttribute("tags", tags)

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

class HideFollowModel(
    val map: Map<String, Boolean>
)

class ShowDeleteModel(
    val map: Map<UUID, Boolean>
)