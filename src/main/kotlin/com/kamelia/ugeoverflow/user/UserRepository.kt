package com.kamelia.ugeoverflow.user

import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, UUID> {

    fun findByUsernameIgnoreCase(username: String): User?

    fun existsByUsernameIgnoreCase(username: String): Boolean

}
