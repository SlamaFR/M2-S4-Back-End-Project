package com.kamelia.ugeoverflow.votes

import com.kamelia.ugeoverflow.answer.AnswerRepository
import com.kamelia.ugeoverflow.core.InvalidRequestException
import com.kamelia.ugeoverflow.utils.currentUser
import jakarta.transaction.Transactional
import java.util.UUID
import org.springframework.stereotype.Service

@Service
class VoteService(
    private val voteRepository: VoteRepository,
    private val answerRepository: AnswerRepository,
) {

    @Transactional
    fun voteAnswer(answerId: UUID, isUpvote: Boolean) {
        val user = currentUser()
        val answer = answerRepository.findById(answerId).orElseThrow {
            InvalidRequestException.notFound("Answer not found")
        }

        if (voteRepository.existsByUserIdAndAnswerId(user.id, answerId)) {
            throw InvalidRequestException.forbidden("You already voted this answer")
        }

        val vote = Vote(user, isUpvote)
        voteRepository.save(vote)
        answer.addVote(vote)
    }
}
