package com.kamelia.ugeoverflow

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/")
class IndexMVController {

    @GetMapping
    fun index(): String = "redirect:/post"
}