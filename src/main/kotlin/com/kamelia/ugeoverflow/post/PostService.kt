package com.kamelia.ugeoverflow.post

import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class PostService(
    private val postRepository: PostRepository,
) {

    @Transactional
    fun getPage(page: Pageable): Page<PostLightDTO> = postRepository.findAll(page).map(Post::toLightDTO)

}
