package com.kamelia.ugeoverflow.question

import com.kamelia.ugeoverflow.answer.toDTO
import com.kamelia.ugeoverflow.core.InvalidRequestException
import com.kamelia.ugeoverflow.follow.FollowedUserRepository
import com.kamelia.ugeoverflow.user.User
import jakarta.transaction.Transactional
import java.util.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import kotlin.math.max
import kotlin.streams.asSequence

@Service
class QuestionPageAnswerSortingService(
    @Value("\${ugeoverflow.answer-algorithm.circle-max-depth}")
    private val circlesMaxDepth: Int,
    private val questionRepository: QuestionRepository,
    private val followedUserRepository: FollowedUserRepository,
) {

    @Transactional
    fun getQuestionAsAnonymous(questionId: UUID): QuestionDTO = questionRepository
        .findById(questionId)
        .orElseThrow { InvalidRequestException.notFound("Question not found") }
        .toDTO { set ->
            set.asSequence()
                .map { it.toDTO() }
                .sortedByDescending { it.note }
                .toList()
        }

    @Transactional
    fun getQuestion(user: User, questionId: UUID): QuestionDTO = questionRepository
        .findById(questionId)
        .orElseThrow { InvalidRequestException.notFound("Question not found") }
        .toDTO { set ->
            val userCoefficientMap = createUserCoefficientMap(user)
            println(userCoefficientMap)
            set.asSequence()
                .map {
                    val finalScore = it.votes.fold(.0) { acc, v ->
                        val voterInfo = userCoefficientMap[v.voter.id]
                            ?: return@fold acc + v.isUpvote.voteToInt() // the voter is not in the map, so we use the default trust score

                        // score calculation according to the algorithm
                        acc + v.isUpvote.voteToInt() * max(
                            1.0,
                            voterInfo.trust * (circlesMaxDepth - voterInfo.depth) / circlesMaxDepth
                        )
                    }

                    it to finalScore
                }
                .sortedByDescending { it.second } // sort by the final score before converting the score to an int to avoid rounding errors
                .map { it.first.toDTO(it.second.toInt()) }
                .toList()
        }

    private fun createUserCoefficientMap(user: User): Map<UUID, UserInformation> {
        val finalMap = mutableMapOf(
            user.id to UserInformation(0, 20.0 to 20.0)
        )

        var currentCircle = setOf(user) // sets containing all the users of a circle
        for (depthLevel in 0 until circlesMaxDepth) {
            currentCircle = currentCircle
                .asSequence()
                .flatMap { // gets all users of the next circle
                    followedUserRepository
                        .findFollowedByUserId(
                            it.id,
                            finalMap.keys
                        ) // only get the followed users that are not already in the map
                        .asSequence()
                }
                .onEach { // add the new users to the final map
                    val followerInfo = finalMap[it.follower.id]!!
                    val followed = it.followed
                    finalMap.compute(followed.id) { _, info ->
                        // update the trust score or create a new one
                        info?.updatedTrustScore(it.trust.toDouble(), followerInfo.trust)
                            ?: UserInformation(depthLevel, it.trust.toDouble() to followerInfo.trust)
                    }
                }
                .run {
                    if (depthLevel < circlesMaxDepth - 1) { // avoid constructing the set if it's the last iteration
                        map { it.followed }.toSet()
                    } else {
                        emptySet()
                    }
                }
        }

        finalMap -= user.id // remove the user from the map
        return finalMap
    }

    private fun Boolean.voteToInt() = if (this) 1 else -1

}

private class UserInformation(
    val depth: Int,
    firstTrustScore: Pair<Double, Double>,
) {

    private var trustList: ArrayList<Pair<Double, Double>>? = ArrayList()

    val trust: Double by lazy {
        val result = trustList!!
            .fold(TrustPairAccumulator()) { acc, e -> acc.update(e) }
            .result()
        trustList = null
        result
    }

    init {
        updatedTrustScore(firstTrustScore.first, firstTrustScore.second)
    }

    fun updatedTrustScore(givenTrust: Double, userTrust: Double) = apply {
        require(trustList != null) { "Trust list is already calculated" }
        trustList!!.add(givenTrust to userTrust)
    }

    override fun toString(): String = "UserInformation(depth=$depth, trust=$trust)"

    /**
     * Object employed to easily calculate the average of trust scores.
     */
    private class TrustPairAccumulator {

        private var sum: Double = .0 // sum of all trust * their coefficient
        private var coefficientList = ArrayList<Double>() // sum of all coefficient

        fun update(scoreToCoefficient: Pair<Double, Double>) = apply {
            val (trust, coefficient) = scoreToCoefficient
            sum += trust * coefficient
            coefficientList += coefficient
        }

        fun result(): Double = if (sum > 0) {
            val coefficientSum = coefficientList.sum()
            val weighting = coefficientList.average() / 20.0
            (sum / coefficientSum) * weighting
        } else {
            0.0
        }

    }

}
