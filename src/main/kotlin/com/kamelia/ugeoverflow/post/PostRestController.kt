package com.kamelia.ugeoverflow.post

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/posts")
class PostRestController(
    private val postRepository: PostRepository,
) {

    @GetMapping
    fun getPage(page: Pageable): Page<PostLightDTO> = postRepository
        .findAll(page)
        .map(Post::toLightDTO)

}
