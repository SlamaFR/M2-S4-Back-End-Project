package com.kamelia.ugeoverflow.votes

import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface VoteRepository : JpaRepository<Vote, UUID> {

    @Query(
        """
            SELECT EXISTS (
                SELECT 1 FROM "vote" v WHERE v."user_id" = :userId AND v."answer_id" = :answerId
            )
        """,
        nativeQuery = true
    )
    fun existsByUserIdAndAnswerId(userId: UUID, answerId: UUID): Boolean

    @Query(
        """
            SELECT * FROM "vote" v WHERE v."user_id" = :userId AND v."answer_id" = :answerId
        """,
        nativeQuery = true
    )
    fun findByUserIdAndAnswerId(userId: UUID, answerId: UUID): Vote?

    //@Query(
    //    """
    //        MERGE INTO "vote" ("user_id", "answer_id", "is_upvote") KEY (:userId, :answerId) VALUES (:userId, :answer_id, :isUpvote)
    //    """,
    //    nativeQuery = true
    //)
    //fun upsertVote(userId: UUID, answerId: UUID, isUpvote: Boolean): Vote
}
