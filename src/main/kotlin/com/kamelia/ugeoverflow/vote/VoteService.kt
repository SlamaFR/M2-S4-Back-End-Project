package com.kamelia.ugeoverflow.vote

import com.kamelia.ugeoverflow.answer.AnswerRepository
import com.kamelia.ugeoverflow.core.InvalidRequestException
import com.kamelia.ugeoverflow.utils.currentUser
import jakarta.transaction.Transactional
import java.util.*
import org.springframework.stereotype.Service

@Service
class VoteService(
    private val voteRepository: VoteRepository,
    private val answerRepository: AnswerRepository,
) {

    @Transactional
    fun voteAnswer(answerId: UUID, isUpvote: Boolean): VoteState {
        val user = currentUser()
        val answer = answerRepository.findById(answerId).orElseThrow {
            InvalidRequestException.notFound("Answer not found")
        }

        val vote = voteRepository.findByUserIdAndAnswerId(user.id, answerId)
        return if (vote != null) {
            if (vote.isUpvote != isUpvote) {
                vote.isUpvote = isUpvote
            }
            vote.state
        } else {
            val newVote = Vote(user, isUpvote)
            voteRepository.save(newVote)
            answer.addVote(newVote)
            newVote.state
        }
    }

    @Transactional
    fun removeVoteFromAnswer(answerId: UUID) {
        val user = currentUser()
        val answer = answerRepository.findById(answerId).orElseThrow {
            InvalidRequestException.notFound("Answer not found")
        }

        val vote = voteRepository.findByUserIdAndAnswerId(user.id, answerId)
            ?: throw InvalidRequestException.notFound("You haven't voted this answer")

        answer.removeVote(vote)
        voteRepository.delete(vote)
    }
}
