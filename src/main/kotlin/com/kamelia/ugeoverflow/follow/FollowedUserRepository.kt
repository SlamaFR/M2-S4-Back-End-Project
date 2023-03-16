package com.kamelia.ugeoverflow.follow

import java.util.*
import java.util.stream.Stream
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface FollowedUserRepository : JpaRepository<FollowedUser, FollowedUser.Id> {

    @Modifying
    @Query(
        """
            UPDATE "followed_user" u 
            SET "trust" = :trust
            WHERE u."follower_id" = :authorId 
            AND u."followed_id" = :userId
        """,
        nativeQuery = true
    )
    fun updateFollowedTrust(authorId: UUID, userId: UUID, trust: Int): Int

    @Query(
        """
            SELECT EXISTS (
                SELECT 1 
                FROM "followed_user" u 
                WHERE u."follower_id" = :follower 
                AND u."followed_id" = :followed
            )
        """,
        nativeQuery = true
    )
    fun existsByFollowerAndFollowed(follower: UUID, followed: UUID): Boolean

    @Modifying
    @Query(
        """DELETE FROM "followed_user" u WHERE u."follower_id" = :followerId AND u."followed_id" = :followedId""",
        nativeQuery = true
    )
    fun deleteByFollowerIdAndFollowedId(followerId: UUID, followedId: UUID): Int

    @Query(
        """SELECT * FROM "followed_user" u WHERE u."follower_id" = :followerId""",
        nativeQuery = true
    )
    fun getFollowedIds(followerId: UUID): Stream<FollowedUser>

}
