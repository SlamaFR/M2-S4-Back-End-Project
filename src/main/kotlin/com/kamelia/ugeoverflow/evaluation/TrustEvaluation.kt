package com.kamelia.ugeoverflow.evaluation

import com.kamelia.ugeoverflow.core.AbstractIdEntity
import com.kamelia.ugeoverflow.user.User
import jakarta.persistence.Entity
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "trust_evaluation")
class TrustEvaluation(
    @ManyToOne
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
