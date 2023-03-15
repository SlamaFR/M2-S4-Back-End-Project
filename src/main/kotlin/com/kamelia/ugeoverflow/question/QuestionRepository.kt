package com.kamelia.ugeoverflow.question

import java.util.UUID
import java.util.stream.Stream
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface QuestionRepository : JpaRepository<Question, UUID> {

    @Query(
        value = """SELECT * FROM "question" q ORDER BY q."creation_date" DESC""",
        nativeQuery = true,
        countQuery = """SELECT COUNT(*) FROM "question""""
    )
    fun findAllAsAnonymous(page: Pageable): Page<Question>

    @Query(
        value = """SELECT COUNT(*) FROM "question" q WHERE q."author_id" = :id""",
        nativeQuery = true,
    )
    fun countAllByAuthorId(id: UUID): Long

}
