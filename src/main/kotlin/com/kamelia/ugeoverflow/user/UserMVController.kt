package com.kamelia.ugeoverflow.user

import jakarta.validation.Valid
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import java.util.*

@Controller
@RequestMapping("/user")
class UserMVController {

    @GetMapping("/details/{id}") // TODO secure route to ROLE_USER and ROLE_ADMIN
    fun profile(
        @PathVariable("id") id: UUID,
        @RequestParam("updateSuccess", required = false) updateSuccess: Boolean?,
        @Valid @ModelAttribute("updatePasswordForm") updatePasswordForm: UpdatePasswordForm,
        model: Model,
    ): String {
        // TODO: Get user from database
        val user: UserDTO? = UserDTO(id, "notKamui", setOf(UserDTO(UUID.randomUUID(), "ZwenDo", null)))

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
}

class UpdatePasswordForm(
    var oldPassword: String = "",
    var newPassword: String = "",
    var confirmPassword: String = "",
)