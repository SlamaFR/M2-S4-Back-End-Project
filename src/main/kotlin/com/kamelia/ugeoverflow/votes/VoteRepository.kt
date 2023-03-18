package com.kamelia.ugeoverflow.votes

import com.kamelia.ugeoverflow.answer.Answer
import com.kamelia.ugeoverflow.user.User
import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface VoteRepository : JpaRepository<Vote, UUID> {

    @Query(
        """
            SELECT EXISTS (
                SELECT 1 
                FROM "vote" v 
                WHERE v."user_id" = :userId
                AND v."answer_id" = :answerId
            )
        """,
        nativeQuery = true
    )
    fun existsByUserIdAndAnswerId(userId: UUID, answerId: UUID): Boolean

}
