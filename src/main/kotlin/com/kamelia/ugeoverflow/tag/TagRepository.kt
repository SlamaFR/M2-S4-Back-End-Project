package com.kamelia.ugeoverflow.tag

import java.util.stream.Stream
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository


@Repository
interface TagRepository : JpaRepository<Tag, String> {

    fun findAllBy(): Stream<Tag>

    @Query(
        value = """SELECT * FROM "tag" t WHERE t."name" IN :names""",
        nativeQuery = true,
    )
    fun findAllByNameIn(names: Set<String>): Set<Tag>

}
