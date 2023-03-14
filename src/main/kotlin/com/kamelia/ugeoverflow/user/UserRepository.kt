package com.kamelia.ugeoverflow.user

import java.util.UUID
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

}
