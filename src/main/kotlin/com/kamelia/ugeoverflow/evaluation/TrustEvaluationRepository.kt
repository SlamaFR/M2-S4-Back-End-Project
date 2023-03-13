package com.kamelia.ugeoverflow.evaluation

import com.kamelia.ugeoverflow.user.User
import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface TrustEvaluationRepository : JpaRepository<TrustEvaluation, UUID> {

    @Modifying
    @Query("update trust_evaluation set trust = :trust where evaluator_id = :authorId and evaluated_id = :userId", nativeQuery = true)
    fun updateTrustEvaluation(authorId: UUID, userId: UUID, trust: Int): TrustEvaluation

    @Query("select exists(select 1 from trust_evaluation where evaluator_id = :evaluatorId and evaluated_id = :evaluatedId)", nativeQuery = true)
    fun existsByEvaluatorAndEvaluated(evaluator: User, evaluated: User): Boolean

    @Modifying
    @Query("delete from trust_evaluation where evaluator_id = :evaluatorId and evaluated_id = :evaluatedId", nativeQuery = true)
    fun deleteByEvaluatorIdAndEvaluatedId(evaluatorId: UUID, evaluatedId: UUID): Boolean

}
