package com.kamelia.ugeoverflow.evaluation

import com.kamelia.ugeoverflow.core.AbstractIdEntity
import com.kamelia.ugeoverflow.user.User
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    name = "trust_evaluation",
    uniqueConstraints = [UniqueConstraint(columnNames = ["evaluator_id", "evaluated_id"])]
)
class TrustEvaluation(
    @ManyToOne
    @JoinColumn(name = "evaluated_id")
    var evaluated: User,
    trust: Int,
) : AbstractIdEntity() {

    init {
        checkTrust(trust)
    }

    var trust: Int = trust
        set(value) {
            checkTrust(value)
            field = value
        }

    private fun checkTrust(trust: Int) {
        require(trust in -50..50) {
            "Invalid evaluation: $trust. Evaluation must be in range [-50, 50]."
        }
    }

}
