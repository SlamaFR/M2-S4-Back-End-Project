package com.kamelia.ugeoverflow.user

import com.kamelia.ugeoverflow.core.MvcController
import com.kamelia.ugeoverflow.follow.FollowedUserDTO
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import java.util.*
import org.springframework.data.jpa.domain.AbstractPersistable_.id
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

val dummy = UserDTO(UUID.randomUUID(), "notKamui", setOf(FollowedUserDTO(UUID.randomUUID(), "ZwenDo", -50)))

@MvcController
@Controller
@RequestMapping("/user")
class UserController {

    @GetMapping("/details/{id}") // TODO secure route to ROLE_USER and ROLE_ADMIN
    fun profile(
        @PathVariable("id") id: UUID,
        @RequestParam("updateSuccess", required = false) updateSuccess: Boolean?,
        @Valid @ModelAttribute("updatePasswordForm") updatePasswordForm: UpdatePasswordForm,
        model: Model,
    ): String {
        // TODO: Get user from database
        val user: UserDTO? = dummy

        if (user == null) {
            model.addAttribute("errorMessage", "User not found")
            return "error/404"
        }

        model.addAttribute("user", user)
        model.addAttribute("updateSuccess", updateSuccess)

        return "user/details"
    }

    @PostMapping("/details/{id}/password") // TODO secure route to ROLE_USER and ROLE_ADMIN
    fun updatePassword(
        @PathVariable("id") id: UUID,
        @Valid @ModelAttribute("updatePasswordForm") updatePasswordForm: UpdatePasswordForm,
        model: Model,
        binding: BindingResult,
    ): String {
        if (binding.hasErrors()) return "redirect:/user/details/$id?updateSuccess=false"

        // TODO update password

        return "redirect:/user/details/$id?updateSuccess=true"
    }

    @GetMapping("/evaluate/{id}")
    fun updateTrustForm(
        @PathVariable("id") id: UUID,
        @Valid @ModelAttribute("updateTrustForm") updateTrustForm: UpdateTrustForm,
        model: Model,
    ): String {
        // TODO: Get user from database
        val user: UserDTO? = dummy
        if (user == null) {
            model.addAttribute("errorMessage", "User not found")
            return "error/404"
        }

        val followed = user.followed.firstOrNull { it.id == id }
        if (followed == null) {
            model.addAttribute("errorMessage", "You do not follow this user")
            return "error/404"
        }

        model.addAttribute("user", user)
        model.addAttribute("evaluationModel", followed)
        updateTrustForm.trust = followed.trust

        return "user/evaluate"
    }

    @PostMapping("/evaluate/{id}")
    fun updateTrust(
        @PathVariable("id") id: UUID,
        @Valid @ModelAttribute("updateTrustForm") updateTrustForm: UpdateTrustForm,
        model: Model,
        binding: BindingResult,
    ): String {
        if (binding.hasErrors()) return "redirect:/user/details/$id?updateSuccess=false"

        // TODO: Get user from database
        val user: UserDTO? = dummy
        if (user == null) {
            model.addAttribute("errorMessage", "User not found")
            return "error/404"
        }

        val followed = user.followed.firstOrNull { it.id == id }
        if (followed == null) {
            model.addAttribute("errorMessage", "You do not follow this user")
            return "error/404"
        }

        // TODO: Update trust

        return "redirect:/user/details/$id?updateSuccess=true"
    }

    @PostMapping("/follow/{username}")
    fun follow(
        @PathVariable("username") username: String,
        request: HttpServletRequest,
        model: Model,
    ): String {
        // TODO: Do follow

        return "redirect:${request.getHeader("referer")}"
    }

    @PostMapping("/unfollow/{username}")
    fun unfollow(
        @PathVariable("username") username: String,
        model: Model,
    ): String {
        // TODO: Get user from database
        val user: UserDTO? = dummy
        if (user == null) {
            model.addAttribute("errorMessage", "User not found")
            return "error/404"
        }

        val followed = user.followed.firstOrNull { it.username == username }
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
