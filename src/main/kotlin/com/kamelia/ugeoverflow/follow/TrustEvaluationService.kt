package com.kamelia.ugeoverflow.follow

import com.kamelia.ugeoverflow.core.InvalidRequestException
import com.kamelia.ugeoverflow.user.User
import com.kamelia.ugeoverflow.user.UserRepository
import com.kamelia.ugeoverflow.utils.currentUser
import jakarta.transaction.Transactional
import java.util.UUID
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class TrustEvaluationService(
    private val followedUserRepository: FollowedUserRepository,
    private val userRepository: UserRepository,
) {

    @Transactional
    fun follow(followedId: UUID) {
        val currentUser: User = currentUser()

        if (currentUser.id == followedId) {
            throw InvalidRequestException.forbidden("Cannot evaluate yourself")
        }

        val user = userRepository.findByIdOrNull(followedId)
            ?: throw InvalidRequestException.notFound("User not found.")

        if (followedUserRepository.existsByFollowerAndFollowed(currentUser.id, user.id)) {
            throw InvalidRequestException.forbidden("User already followed")
        }

        val followedUser = FollowedUser(currentUser, user)
        followedUserRepository.save(followedUser)
    }

    @Transactional
    fun unfollow(followedId: UUID) {
        val currentUser: User = currentUser()

        if (!followedUserRepository.deleteByFollowerIdAndFollowedId(currentUser.id, followedId)) {
            throw InvalidRequestException.notFound("User not followed")
        }
    }

    @Transactional
    fun evaluateFollowed(followedId: UUID, trust: Int) {
        require(trust in -50..50) { "Trust must be between -50 and 50, was $trust" }
        val currentUser: User = currentUser()

        if (currentUser.id == followedId) {
            throw InvalidRequestException.forbidden("Cannot evaluate yourself")
        }

        followedUserRepository.updateFollowedTrust(currentUser.id, followedId, trust)
            ?: throw InvalidRequestException.notFound("User not followed")
    }

    @Transactional
    fun getFollowedUsers(): List<FollowedUserDTO> = followedUserRepository
        .getFollowedIds(currentUser().id)
        .map(FollowedUser::toDTO)
        .toList()

}
