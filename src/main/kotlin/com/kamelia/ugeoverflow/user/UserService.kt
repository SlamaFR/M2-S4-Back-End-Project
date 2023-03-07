package com.kamelia.ugeoverflow.user

import com.kamelia.ugeoverflow.core.Hasher
import com.kamelia.ugeoverflow.core.InvalidRequestException
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val hasher: Hasher,
) {

    @Transactional
    fun create(user: UserCredentialsDTO): UserDTO {
        val (username, password) = user

        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw InvalidRequestException.forbidden("Username $username already exists")
        }

        val hashedPassword = hasher.hash(password)
        val userEntity = User(username, hashedPassword)
        userRepository.save(userEntity)
        return userEntity.toDTO()
    }

}
