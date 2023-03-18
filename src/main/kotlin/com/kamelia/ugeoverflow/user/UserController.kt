package com.kamelia.ugeoverflow.user

import com.kamelia.ugeoverflow.core.MvcController
import com.kamelia.ugeoverflow.follow.FollowedUserRepository
import com.kamelia.ugeoverflow.follow.FollowingService
import com.kamelia.ugeoverflow.utils.currentUser
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import java.util.*
import org.springframework.data.jpa.domain.AbstractPersistable_.id
import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

@MvcController
@Controller
@RequestMapping("/user")
class UserController(
    private val userService: UserService,
    private val followingService: FollowingService,
) {

    @Secured("ROLE_USER")
    @GetMapping("/details")
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

    @Secured("ROLE_USER")
    @PostMapping("/details/password")
    fun updatePassword(
        @Valid @ModelAttribute("updatePasswordForm") updatePasswordForm: UpdatePasswordForm,
        model: Model,
        binding: BindingResult,
    ): String {
        if (binding.hasErrors() || updatePasswordForm.newPassword != updatePasswordForm.confirmPassword) {
            return "redirect:/user/details?updateSuccess=false"
        }

        val user = currentUser().let { userService.findByIdOrNull(it.id) }
        if (user == null) {
            model.addAttribute("errorMessage", "User not found")
            return "error/404"
        }

        userService.updatePassword(user, updatePasswordForm.newPassword)

        return "redirect:/user/details?updateSuccess=true"
    }

    @Secured("ROLE_USER")
    @GetMapping("/evaluate/{id}")
    fun updateTrustForm(
        @PathVariable("id") id: UUID,
        @Valid @ModelAttribute("updateTrustForm") updateTrustForm: UpdateTrustForm,
        model: Model,
    ): String {
        val user = currentUser().let { userService.findByIdOrNull(it.id) }
        if (user == null) {
            model.addAttribute("errorMessage", "User not found")
            return "error/404"
        }

        val followed = user.followed.firstOrNull { it.followed.id == id }
        if (followed == null) {
            model.addAttribute("errorMessage", "You do not follow this user")
            return "error/404"
        }

        model.addAttribute("user", user)
        model.addAttribute("evaluationModel", followed)
        updateTrustForm.trust = followed.trust

        return "user/evaluate"
    }

    @Secured("ROLE_USER")
    @PostMapping("/evaluate/{id}")
    fun updateTrust(
        @PathVariable("id") id: UUID,
        @Valid @ModelAttribute("updateTrustForm") updateTrustForm: UpdateTrustForm,
        model: Model,
        binding: BindingResult,
    ): String {
        if (binding.hasErrors()) return "redirect:/user/details/$id?updateSuccess=false"

        val user = currentUser().let { userService.findByIdOrNull(it.id) }
        if (user == null) {
            model.addAttribute("errorMessage", "User not found")
            return "error/404"
        }

        val followed = user.followed.firstOrNull { it.followed.id == id }
        if (followed == null) {
            model.addAttribute("errorMessage", "You do not follow this user")
            return "error/404"
        }

        // TODO: Update trust

        return "redirect:/user/details/$id?updateSuccess=true"
    }

    @Secured("ROLE_USER")
    @PostMapping("/follow/{username}")
    fun follow(
        @PathVariable("username") username: String,
        request: HttpServletRequest,
        model: Model,
    ): String {
        // TODO: Do follow

        return "redirect:${request.getHeader("referer")}"
    }

    @Secured("ROLE_USER")
    @PostMapping("/unfollow/{username}")
    fun unfollow(
        @PathVariable("username") username: String,
        model: Model,
    ): String {
        val user = currentUser().let { userService.findByIdOrNull(it.id) }

        if (user == null) {
            model.addAttribute("errorMessage", "User not found")
            return "error/404"
        }

        val followed = user.followed.firstOrNull { it.followed.username == username }
        if (followed == null) {
            model.addAttribute("errorMessage", "You do not follow this user")
            return "error/404"
        }

        // TODO: Unfollow

        return "redirect:/user/details/$id?updateSuccess=true"
    }
}

class UpdatePasswordForm(
    var oldPassword: String = "",
    var newPassword: String = "",
    var confirmPassword: String = "",
)

class UpdateTrustForm(
    var trust: Int? = null,
)
