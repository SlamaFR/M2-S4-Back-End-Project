package com.kamelia.ugeoverflow.tag

import org.springframework.stereotype.Service

@Service
class TagService(
    private val tagRepository: TagRepository,
) {

    fun addTag(name: String): Tag {
        require(name.isNotBlank()) { "Tag name cannot be blank" }
        val tag = Tag(name)
        return tagRepository.save(tag)
    }

    fun allTags(): List<Tag> = tagRepository.findAll()

}
