package com.kamelia.ugeoverflow

import com.kamelia.ugeoverflow.core.MvcController
import com.kamelia.ugeoverflow.utils.Routes
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@MvcController
@Controller
@RequestMapping("/")
class IndexMvcController {

    @GetMapping
    fun index(): String = "redirect:${Routes.Question.ROOT}"
}