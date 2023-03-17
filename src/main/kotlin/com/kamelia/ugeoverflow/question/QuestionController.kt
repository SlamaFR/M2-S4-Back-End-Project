package com.kamelia.ugeoverflow.question

import com.kamelia.ugeoverflow.answer.AnswerService
import com.kamelia.ugeoverflow.answer.PostAnswerDTO
import com.kamelia.ugeoverflow.core.MvcController
import com.kamelia.ugeoverflow.tag.TagDTO
import com.kamelia.ugeoverflow.tag.TagService
import com.kamelia.ugeoverflow.user.User
import com.kamelia.ugeoverflow.user.UserService
import com.kamelia.ugeoverflow.utils.currentUserOrNull
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import java.util.*
import org.springframework.data.domain.Pageable
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
class QuestionController(
    private val questionService: QuestionService,
    private val userService: UserService,
    private val answerService: AnswerService,
    private val tagService: TagService,
) {

    @GetMapping
    fun list(
        @RequestParam("page", required = false) page: Int?,
        @RequestParam("searchName", required = false) searchName: String?,
        @RequestParam("searchTags", required = false) searchTags: Set<String>?,
        model: Model,
    ): String {
        model.addAttribute("tags", tagService.allTags())

        val questions = questionService.getPage(
            Pageable.ofSize(25).withPage(page ?: 0),
            QuestionSearchFilterDTO(searchName, searchTags)
        )

        model.addAttribute("questionsModel", QuestionsModel(questions.toList()))

        return "question/list"
    }

    @GetMapping("/{id}")
    fun details(
        @PathVariable("id") id: UUID,
        @Valid @ModelAttribute("answerForm") answerForm: CommentForm,
        @Valid @ModelAttribute("commentForm") commentForm: CommentForm,
        model: Model,
    ): String {
        val user: User? = currentUserOrNull()?.let { userService.findByIdOrNull(it.id) }

        val question = questionService.getQuestion(id)

        println(question)

        val followButtonsToHide = mutableMapOf<String, Boolean>()
        val deleteButtonsToShow = mutableMapOf<UUID, Boolean>()
        if (user != null) {
            //user.followed.forEach { followButtonsToHide[it.followed.username] = true }
            //user.let { followButtonsToHide[it.username] = true }
            //
            //question.comments.forEach { deleteButtonsToShow[it.id] = it.authorUsername == user.username }
        } else {
            // TODO: hide shit jim said
        }

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

        answerService.postAnswer(id, PostAnswerDTO(answerForm.content))

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

        val question = questionService.postQuestion(
            PostQuestionDTO(
                createQuestionForm.title,
                createQuestionForm.content,
                createQuestionForm.tags,
            )
        )

        return "redirect:/question/${question.id}"
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
    val questions: List<QuestionLightDTO>,
)

class HideFollowModel(
    val map: Map<String, Boolean>,
)

class ShowDeleteModel(
    val map: Map<UUID, Boolean>,
)