package com.kamelia.ugeoverflow.user

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
) {

    @Transactional
    fun create(user: UserCredentialsDTO): UserDTO {
        val (username, password) = user

        require(!userRepository.existsByUsernameIgnoreCase(username)) { "Username $username already exists" }

        // TODO +hashing
        val userEntity = User(username, password)
        userRepository.save(userEntity)
        return userEntity.toDTO()
    }

}
