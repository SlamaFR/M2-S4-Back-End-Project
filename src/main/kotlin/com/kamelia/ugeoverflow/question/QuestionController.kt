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
import com.kamelia.ugeoverflow.utils.Routes
import com.kamelia.ugeoverflow.utils.currentUserOrNull
import com.kamelia.ugeoverflow.utils.referer
import com.kamelia.ugeoverflow.vote.VoteService
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

@MvcController
@Controller
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

    @GetMapping(Routes.Question.ROOT)
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
        model.addAttribute("page", actualPage)
        model.addAttribute("pageNumber", questions.totalPages)

        return "question/list"
    }

    @GetMapping("${Routes.Question.ROOT}/{id}")
    fun details(
        @PathVariable("id") id: UUID,
        @Valid @ModelAttribute("answerForm") answerForm: CommentForm,
        @Valid @ModelAttribute("commentForm") commentForm: CommentForm,
        model: Model,
    ): String {
        val user: User? = currentUserOrNull()?.let { userService.findByIdOrNull(it.id) }

        val question = questionService.getQuestion(id)

        val followButtonsToHide = mutableMapOf<UUID, Boolean>()
        if (user != null) {
            followingService.getFollowedUsers().forEach { followButtonsToHide[it.id] = true }
            followButtonsToHide[user.id] = true
        }

        model.addAttribute("question", question)
        model.addAttribute("hideFollowForUser", HideFollowModel(followButtonsToHide))
        model.addAttribute("converter", mdToHtmlService)

        return "question/details"
    }

    @Secured(Roles.USER)
    @PostMapping("${Routes.Question.VOTE}/{questionId}/{answerId}")
    fun voteAnswer(
        @PathVariable("questionId") questionId: UUID,
        @PathVariable("answerId") answerId: UUID,
        @RequestParam("vote", required = true) vote: Boolean,
        model: Model,
    ): String {
        voteService.voteAnswer(answerId, vote)
        return "redirect:/question/$questionId"
    }

    @Secured(Roles.USER)
    @PostMapping("${Routes.Question.UNVOTE}/{questionId}/{answerId}")
    fun unvoteAnswer(
        @PathVariable("questionId") questionId: UUID,
        @PathVariable("answerId") answerId: UUID,
        model: Model,
    ): String {
        voteService.removeVoteFromAnswer(answerId)
        return "redirect:${Routes.Question.ROOT}/$questionId"
    }

    @Secured(Roles.USER)
    @PostMapping("${Routes.Question.Answer.ROOT}/{id}")
    fun answer(
        @PathVariable("id") id: UUID,
        @Valid @ModelAttribute("answerForm") answerForm: CommentForm,
        model: Model,
        bindingResult: BindingResult,
    ): String {
        if (bindingResult.hasErrors()) {
            model.addAttribute("answerErrorMessage", "Invalid answer")
            return "redirect:${Routes.Question.ROOT}/$id"
        }

        answerService.postAnswer(id, PostAnswerDTO(answerForm.content))

        return "redirect:${Routes.Question.ROOT}/$id"
    }

    @Secured(Roles.ADMIN)
    @PostMapping("${Routes.Question.DELETE}/{id}")
    fun deleteQuestion(
        @PathVariable("id") id: UUID,
    ): String {
        questionService.deleteQuestion(id)
        return "redirect:${Routes.Question.ROOT}"
    }

    @Secured(Roles.USER)
    @PostMapping("${Routes.Question.Answer.DELETE}/{id}")
    fun deleteAnswer(
        @PathVariable("id") id: UUID,
        request: HttpServletRequest,
    ): String {
        answerService.deleteAnswer(id)
        return "redirect:${request.referer}"
    }

    @Secured(Roles.USER)
    @PostMapping("${Routes.Question.COMMENT}/{id}")
    fun commentQuestion(
        @PathVariable("id") id: UUID,
        @Valid @ModelAttribute("commentForm") commentForm: CommentForm,
        model: Model,
        bindingResult: BindingResult,
    ): String {
        if (bindingResult.hasErrors()) {
            model.addAttribute("commentErrorMessage", mapOf(id to "Invalid comment"))
            return "redirect:${Routes.Question.ROOT}/$id"
        }

        commentService.postCommentOnQuestion(id, PostCommentDTO(commentForm.content))
        return "redirect:${Routes.Question.ROOT}/$id"
    }

    @Secured(Roles.USER)
    @PostMapping("${Routes.Question.Answer.COMMENT}/{id}")
    fun commentAnswer(
        @PathVariable("id") id: UUID,
        @Valid @ModelAttribute("commentForm") commentForm: CommentForm,
        model: Model,
        bindingResult: BindingResult,
        request: HttpServletRequest,
    ): String {
        if (bindingResult.hasErrors()) {
            model.addAttribute("commentErrorMessages", mapOf(id to "Invalid comment"))
            return "redirect:${request.referer}"
        }

        commentService.postCommentOnAnswer(id, PostCommentDTO(commentForm.content))
        return "redirect:${request.referer}"
    }

    @Secured(Roles.USER)
    @GetMapping(Routes.Question.CREATE)
    fun createForm(
        @Valid @ModelAttribute("createQuestionForm") createQuestionForm: CreateQuestionForm,
        model: Model,
    ): String {
        model.addAttribute("tags", tagService.allTags())
        return "question/create"
    }

    @Secured(Roles.USER)
    @PostMapping(Routes.Question.CREATE)
    fun create(
        @Valid @ModelAttribute("createQuestionForm") createQuestionForm: CreateQuestionForm,
        model: Model,
        bindingResult: BindingResult,
    ): String {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "Invalid question")
            return "redirect:${Routes.Question.CREATE}"
        }

        val question = questionService.postQuestion(
            PostQuestionDTO(
                createQuestionForm.title,
                createQuestionForm.content,
                createQuestionForm.tags,
            )
        )

        return "redirect:${Routes.Question.ROOT}/${question.id}"
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
