package com.kamelia.ugeoverflow.evaluation

import com.kamelia.ugeoverflow.core.InvalidRequestException
import com.kamelia.ugeoverflow.user.User
import com.kamelia.ugeoverflow.user.UserRepository
import java.util.UUID
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class TrustEvaluationService(
    private val trustEvaluationRepository: TrustEvaluationRepository,
    private val userRepository: UserRepository,
) {

    fun evaluateUser(evaluatedUserId: UUID, trust: Int) {
        // TODO: get current user from security context
        val currentUser: User = SecurityContextHolder.getContext().authentication.principal as User
        val evaluatedUser = userRepository.findById(evaluatedUserId).orElseThrow {
            InvalidRequestException.notFound("User not found.")
        }

        if (evaluatedUser == currentUser) {
            InvalidRequestException.forbidden("You cannot evaluate yourself.")
        }
        if (evaluatedUser !in currentUser.following) {
            InvalidRequestException.forbidden("You cannot evaluate a user you are not following.")
        }

        // If the user has already evaluated the user, update the evaluation.
        // TODO: change this piece of junk
        currentUser.trustEvaluations.firstOrNull { it.evaluated == evaluatedUser }?.let {
            it.trust = trust
            trustEvaluationRepository.save(it)
            return
        }

        val evaluation = TrustEvaluation(evaluatedUser, trust)
        trustEvaluationRepository.save(evaluation)
    }

    fun removeEvaluation(evaluatedUserId: UUID) {
        // TODO: get current user from security context
        val currentUser: User = SecurityContextHolder.getContext().authentication.principal as User

        // TODO: change this piece of junk
        currentUser.trustEvaluations.firstOrNull { it.evaluated.id == evaluatedUserId }?.let {
            trustEvaluationRepository.delete(it)
            return
        } ?: run {
            InvalidRequestException.notFound("You did not evaluate this user.")
        }
    }
}