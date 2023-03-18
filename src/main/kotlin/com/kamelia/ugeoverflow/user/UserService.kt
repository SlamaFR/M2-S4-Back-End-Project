package com.kamelia.ugeoverflow.user

import com.kamelia.ugeoverflow.core.InvalidRequestException
import com.kamelia.ugeoverflow.utils.currentUser
import jakarta.transaction.Transactional
import java.util.*
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val hasher: PasswordEncoder,
) {

    @Transactional
    fun create(user: UserCredentialsDTO): UserDTO {
        val (username, password) = user

        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw InvalidRequestException.forbidden("Username $username already exists")
        }

        val hashedPassword = hasher.encode(password)
        val userEntity = User(username, hashedPassword)
        userRepository.save(userEntity)
        return userEntity.toDTO()
    }

    @Transactional
    fun currentUserInformation(): UserDTO {
        val user = userRepository.findById(currentUser().id).orElseThrow {
            throw AssertionError("User is logged in but not found in database")
        }
        return user.toDTO()
    }

    @Transactional
    fun findByUsernameOrNull(username: String): User? = userRepository
        .findByUsernameIgnoreCase(username)

    @Transactional
    fun findByIdOrNull(userId: UUID): User? = userRepository.findByIdOrNull(userId)

    @Transactional
    fun updatePassword(user: User, newPassword: String) = userRepository.updatePassword(user.id, hasher.encode(newPassword))

}
