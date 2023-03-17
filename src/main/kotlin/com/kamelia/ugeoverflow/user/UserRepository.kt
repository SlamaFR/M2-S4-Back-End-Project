package com.kamelia.ugeoverflow.user

import java.util.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, UUID> {

    @Query(
        value = """SELECT * FROM "user" u WHERE UPPER(u."username") = UPPER(:username)""",
        nativeQuery = true,
    )
    fun findByUsernameIgnoreCase(username: String): User?

    @Query(
        value = """
            SELECT EXISTS (
                SELECT 1 FROM "user" u WHERE UPPER(u."username") = UPPER(:username)
            )
        """,
        nativeQuery = true,
    )
    fun existsByUsernameIgnoreCase(username: String): Boolean

    @Query(
        """
            SELECT u.*
            FROM "user" u
            JOIN "followed_user" f 
            ON u."id" = f."followed_id"
            WHERE f."follower_id" = :followerId 
            AND f."followed_id" NOT IN (:ignoredIds)
            ORDER BY f."trust" DESC
        """,
        nativeQuery = true,
    )
    fun findAllUsersFollowedByUserId(followerId: UUID, ignoredIds: Set<UUID>): List<User>

    @Query(
        """
            SELECT u.*
            FROM "user" u
            JOIN "followed_user" f 
            ON u."id" = f."followed_id"
            WHERE f."follower_id" = :followerId 
            ORDER BY f."trust" DESC
        """,
        nativeQuery = true,
    )
    fun findAllUsersFollowedByUserId(followerId: UUID): List<User>

    @Query(
        """SELECT * FROM "user" u WHERE u."username" = :username""",
        nativeQuery = true,
    )
    fun findByUsername(username: String): User?

}
