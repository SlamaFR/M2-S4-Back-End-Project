package com.kamelia.ugeoverflow.evaluation

import com.kamelia.ugeoverflow.core.InvalidRequestException
import com.kamelia.ugeoverflow.user.User
import com.kamelia.ugeoverflow.user.UserRepository
import com.kamelia.ugeoverflow.utils.currentUser
import java.util.UUID
import org.springframework.stereotype.Service

@Service
class TrustEvaluationService(
    private val trustEvaluationRepository: TrustEvaluationRepository,
    private val userRepository: UserRepository,
) {

    fun evaluateUser(evaluatedUserId: UUID, trust: Int) {
        // TODO: get current user from security context
        val currentUser: User = currentUser ?: throw InvalidRequestException.unauthorized()
        val evaluatedUser = userRepository.findById(evaluatedUserId).orElseThrow {
            throw InvalidRequestException.notFound("User not found.")
        }

        if (evaluatedUser == currentUser) {
            throw InvalidRequestException.forbidden("You cannot evaluate yourself.")
        }
        if (evaluatedUser !in currentUser.following) {
            throw InvalidRequestException.forbidden("You cannot evaluate a user you are not following.")
        }

        // If the user has already evaluated the user, update the evaluation.
        if (trustEvaluationRepository.existsByEvaluatorAndEvaluated(currentUser, evaluatedUser)) {
            trustEvaluationRepository.updateTrustEvaluation(currentUser.id, evaluatedUserId, trust)
            return
        }

        val evaluation = TrustEvaluation(evaluatedUser, trust)
        trustEvaluationRepository.save(evaluation)
    }

    fun removeEvaluation(evaluatedUserId: UUID) {
        // TODO: get current user from security context
        val currentUser: User = currentUser ?: throw InvalidRequestException.unauthorized()

        if (!trustEvaluationRepository.deleteByEvaluatorIdAndEvaluatedId(currentUser.id, evaluatedUserId)) {
            throw InvalidRequestException.notFound("You did not evaluate this user.")
        }
    }
}