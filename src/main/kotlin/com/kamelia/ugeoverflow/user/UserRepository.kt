package com.kamelia.ugeoverflow.user

import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, UUID> {

    fun existsByUsernameIgnoreCase(username: String): Boolean

}
