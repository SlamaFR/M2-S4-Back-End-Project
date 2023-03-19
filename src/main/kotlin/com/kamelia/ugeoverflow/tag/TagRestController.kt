package com.kamelia.ugeoverflow.tag

import com.kamelia.ugeoverflow.utils.Routes
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(Routes.Api.Tag.ROOT)
class TagRestController(
    private val tagService: TagService,
) {

    @GetMapping
    fun allTags() = tagService.allTags()

}
