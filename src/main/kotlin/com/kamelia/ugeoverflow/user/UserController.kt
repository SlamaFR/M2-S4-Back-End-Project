package com.kamelia.ugeoverflow.user

import com.kamelia.ugeoverflow.core.MvcController
import com.kamelia.ugeoverflow.follow.FollowingService
import com.kamelia.ugeoverflow.session.SessionManager
import com.kamelia.ugeoverflow.utils.Roles
import com.kamelia.ugeoverflow.utils.Routes
import com.kamelia.ugeoverflow.utils.currentUser
import com.kamelia.ugeoverflow.utils.referer
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.*

@MvcController
@Controller
class UserController(
    private val userService: UserService,
    private val followingService: FollowingService,
    private val sessionManager: SessionManager,
) {

    @Secured(Roles.USER)
    @GetMapping(Routes.User.Details.ROOT)
    fun profile(
        @RequestParam("updateSuccess", required = false) updateSuccess: Boolean?,
        @Valid @ModelAttribute("updatePasswordForm") updatePasswordForm: UpdatePasswordForm,
        model: Model,
    ): String {
        val user = currentUser().let { userService.findByIdOrNull(it.id) }

        if (user == null) {
            model.addAttribute("errorMessage", "User not found")
            return "error/404"
        }

        val followedUsers = followingService.getFollowedUsers()
        model.addAttribute("updateSuccess", updateSuccess)
        model.addAttribute("followedUsers", followedUsers)

        return "user/details"
    }

    @Secured(Roles.USER)
    @PostMapping(Routes.User.Details.PASSWORD)
    fun updatePassword(
        @Valid @ModelAttribute("updatePasswordForm") updatePasswordForm: UpdatePasswordForm,
        model: Model,
        binding: BindingResult,
        response: HttpServletResponse,
    ): String {
        if (binding.hasErrors() || updatePasswordForm.newPassword != updatePasswordForm.confirmPassword) {
            return "redirect:${Routes.User.Details.ROOT}?updateSuccess=false"
        }

        val user = currentUser().let { userService.findByIdOrNull(it.id) }
        if (user == null) {
            model.addAttribute("errorMessage", "User not found")
            return "error/404"
        }

        userService.updatePassword(PasswordUpdateDTO(updatePasswordForm.oldPassword, updatePasswordForm.newPassword))
        sessionManager
            .login(user.username, updatePasswordForm.newPassword)
            .toCookies()
            .forEach(response::addCookie)

        return "redirect:${Routes.User.Details.ROOT}?updateSuccess=true"
    }

    @Secured(Roles.USER)
    @GetMapping("${Routes.User.EVALUATE}/{id}")
    fun updateTrustForm(
        @PathVariable("id") id: UUID,
        @Valid @ModelAttribute("updateTrustForm") updateTrustForm: UpdateTrustForm,
        model: Model,
    ): String {
        val followed = followingService.getFollowedUsers().firstOrNull { it.id == id }
        if (followed == null) {
            model.addAttribute("errorMessage", "You do not follow this user")
            return "error/404"
        }

        model.addAttribute("followed", followed)
        updateTrustForm.trust = followed.trust

        return "user/evaluate"
    }

    @Secured(Roles.USER)
    @PostMapping("${Routes.User.EVALUATE}/{id}")
    fun updateTrust(
        @PathVariable("id") id: UUID,
        @Valid @ModelAttribute("updateTrustForm") updateTrustForm: UpdateTrustForm,
        model: Model,
        binding: BindingResult,
    ): String {
        val trust = updateTrustForm.trust
        if (binding.hasErrors() || trust == null || trust < 1 || trust > 20) return "redirect:${Routes.User.Details.ROOT}/$id?updateSuccess=false"

        val followed = followingService.getFollowedUsers().firstOrNull { it.id == id }
        if (followed == null) {
            model.addAttribute("errorMessage", "You do not follow this user")
            return "error/404"
        }

        followingService.evaluateFollowed(followed.id, trust)

        return "redirect:${Routes.User.Details.ROOT}?updateSuccess=true"
    }

    @Secured(Roles.USER)
    @PostMapping("${Routes.User.FOLLOW}/{id}")
    fun follow(
        @PathVariable("id") id: UUID,
        request: HttpServletRequest,
    ): String {
        followingService.follow(id)
        return "redirect:${request.referer}"
    }

    @Secured(Roles.USER)
    @PostMapping("${Routes.User.UNFOLLOW}/{id}")
    fun unfollow(
        @PathVariable("id") id: UUID
    ): String {
        followingService.unfollow(id)
        return "redirect:${Routes.User.Details.ROOT}?updateSuccess=true"
    }
}

@Validated
class UpdatePasswordForm(
    var oldPassword: String = "",
    var newPassword: String = "",
    var confirmPassword: String = "",
)

@Validated
class UpdateTrustForm(
    @Min(1)
    @Max(20)
    var trust: Int? = null,
)
