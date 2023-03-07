package com.kamelia.ugeoverflow.tag

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class TagService(
    private val tagRepository: TagRepository,
) {

    @Transactional
    fun addTag(name: String): TagDTO {
        require(name.isNotBlank()) { "Tag name cannot be blank" }
        val tag = Tag(name)
        return tagRepository.save(tag).toDTO()
    }

    @Transactional
    fun allTags(): List<TagDTO> = tagRepository
        .findAllBy()
        .map(Tag::toDTO)
        .toList()

}
