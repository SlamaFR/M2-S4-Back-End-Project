package com.kamelia.ugeoverflow.question

import com.kamelia.ugeoverflow.answer.AnswerService
import com.kamelia.ugeoverflow.answer.PostAnswerDTO
import com.kamelia.ugeoverflow.comment.CommentService
import com.kamelia.ugeoverflow.comment.PostCommentDTO
import com.kamelia.ugeoverflow.core.MdToHtmlService
import com.kamelia.ugeoverflow.core.MvcController
import com.kamelia.ugeoverflow.follow.FollowingService
import com.kamelia.ugeoverflow.tag.TagDTO
import com.kamelia.ugeoverflow.tag.TagService
import com.kamelia.ugeoverflow.user.User
import com.kamelia.ugeoverflow.user.UserService
import com.kamelia.ugeoverflow.utils.Roles
import com.kamelia.ugeoverflow.utils.currentUserOrNull
import com.kamelia.ugeoverflow.votes.VoteService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import java.util.*
import org.springframework.data.domain.Pageable
import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

val tags = listOf(TagDTO("questions"), TagDTO("answer"))

@MvcController
@Controller
@RequestMapping("/question")
class QuestionController(
    private val questionService: QuestionService,
    private val userService: UserService,
    private val answerService: AnswerService,
    private val tagService: TagService,
    private val voteService: VoteService,
    private val commentService: CommentService,
    private val mdToHtmlService: MdToHtmlService,
    private val followingService: FollowingService,
) {

    @GetMapping
    fun list(
        @RequestParam("page", required = false) page: Int?,
        @RequestParam("searchName", required = false) searchName: String?,
        @RequestParam("searchTags", required = false) searchTags: Set<String>?,
        model: Model,
        response: HttpServletResponse,
    ): String {
        model.addAttribute("tags", tagService.allTags())

        val actualPage = page ?: 0

        val questions = questionService.getPage(
            Pageable.ofSize(25).withPage(actualPage),
            QuestionSearchFilterDTO(searchName, searchTags)
        )

        model.addAttribute("questionsModel", QuestionsModel(questions.toList()))
        model.addAttribute("initialPage", actualPage)

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

        val followButtonsToHide = mutableMapOf<UUID, Boolean>()
        val deleteButtonsToShow = mutableMapOf<UUID, Boolean>()
        if (user != null) {
            followingService.getFollowedUsers().forEach { followButtonsToHide[it.id] = true }
            followButtonsToHide[user.id] = true

//            question.comments.forEach { deleteButtonsToShow[it.id] = it.authorUsername == user.username }
        }

        model.addAttribute("question", question)
        model.addAttribute("hideFollowForUser", HideFollowModel(followButtonsToHide))
        model.addAttribute("showDeleteForUser", ShowDeleteModel(deleteButtonsToShow))
        model.addAttribute("converter", mdToHtmlService)

        return "question/details"
    }

    @Secured(Roles.USER)
    @PostMapping("/vote/{questionId}/{answerId}")
    fun voteComment(
        @PathVariable("questionId") questionId: UUID,
        @PathVariable("answerId") answerId: UUID,
        @RequestParam("vote", required = true) vote: Boolean,
        model: Model,
    ): String {
        voteService.voteAnswer(answerId, vote)
        return "redirect:/question/$questionId"
    }

    @Secured(Roles.USER)
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

    @Secured(Roles.ADMIN)
    @PostMapping("/answer/delete/{id}")
    fun deleteAnswer(
        @PathVariable("id") id: UUID,
        request: HttpServletRequest,
    ): String {
        questionService.deleteQuestion(id)
        return "redirect:/question"
    }

    @Secured(Roles.USER)
    @PostMapping("/comment/{id}")
    fun commentQuestion(
        @PathVariable("id") id: UUID,
        @Valid @ModelAttribute("commentForm") commentForm: CommentForm,
        model: Model,
        bindingResult: BindingResult,
    ): String {
        if (bindingResult.hasErrors()) {
            model.addAttribute("commentErrorMessage", mapOf(id to "Invalid comment"))
            return "redirect:/question/$id"
        }

        // TODO add comment to database
        println(commentForm.content)

        return "redirect:/question/$id"
    }

    @Secured(Roles.USER)
    @PostMapping("/answer/{id}/{answerId}")
    fun commentAnswer(
        @PathVariable("id") id: UUID,
        @PathVariable("answerId") answerId: UUID,
        @Valid @ModelAttribute("commentForm") commentForm: CommentForm,
        model: Model,
        bindingResult: BindingResult,
    ): String {
        if (bindingResult.hasErrors()) {
            model.addAttribute("commentErrorMessages", mapOf(answerId to "Invalid comment"))
            return "redirect:/question/$id"
        }

        commentService.postCommentOnAnswer(answerId, PostCommentDTO(commentForm.content))
        return "redirect:/question/$id"
    }

    @Secured(Roles.USER)
    @GetMapping("/create")
    fun createForm(
        @Valid @ModelAttribute("createQuestionForm") createQuestionForm: CreateQuestionForm,
        model: Model,
    ): String {
        model.addAttribute("tags", tagService.allTags())
        return "question/create"
    }

    @Secured(Roles.USER)
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
    val map: Map<UUID, Boolean>,
)

class ShowDeleteModel(
    val map: Map<UUID, Boolean>,
)