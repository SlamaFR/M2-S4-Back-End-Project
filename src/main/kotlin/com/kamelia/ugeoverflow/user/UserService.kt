package com.kamelia.ugeoverflow.user

import com.kamelia.ugeoverflow.core.Hasher
import com.kamelia.ugeoverflow.util.InvalidRequestException
import jakarta.transaction.Transactional
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
    fun checkIdentity(username: String, password: String): UserDTO {
        val user = userRepository.findByUsernameIgnoreCase(username)
            ?: throw InvalidRequestException.unauthorized("Invalid credentials")

        if (!hasher.matches(password, user.password)) {
            throw InvalidRequestException.unauthorized("Invalid credentials")
        }

        return user.toDTO()
    }

}
