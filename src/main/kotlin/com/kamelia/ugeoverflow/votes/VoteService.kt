package com.kamelia.ugeoverflow.votes

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

    private fun voteAnswer(answerId: UUID, isUpvote: Boolean) {
        val user = currentUser()
        val answer = answerRepository.findById(answerId).orElseThrow {
            InvalidRequestException.notFound("Answer not found")
        }

        val vote = Vote(user, isUpvote)
        voteRepository.save(vote)
        answer.addVote(vote)
    }

    @Transactional
    fun upvoteAnswer(answerId: UUID) = voteAnswer(answerId, true)

    @Transactional
    fun downvoteAnswer(answerId: UUID) = voteAnswer(answerId, false)

}
