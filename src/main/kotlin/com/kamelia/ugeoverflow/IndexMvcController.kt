package com.kamelia.ugeoverflow

import com.kamelia.ugeoverflow.core.MvcController
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@MvcController
@Controller
@RequestMapping("/")
class IndexMvcController {

    @GetMapping
    fun index(): String = "redirect:/question"
}