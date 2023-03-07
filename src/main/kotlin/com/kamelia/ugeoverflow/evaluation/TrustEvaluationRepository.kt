package com.kamelia.ugeoverflow.evaluation

import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TrustEvaluationRepository : JpaRepository<TrustEvaluation, UUID>
