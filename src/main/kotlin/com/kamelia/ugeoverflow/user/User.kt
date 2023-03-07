package com.kamelia.ugeoverflow.user

import com.kamelia.ugeoverflow.AbstractIdEntity
import com.kamelia.ugeoverflow.evaluation.TrustEvaluation
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.ManyToMany
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank

@Entity
@Table(name = "user")
class User(
    username: String,
    password: String,
    following: Set<User>,
    trustEvaluations: Set<TrustEvaluation>,
) : AbstractIdEntity() {

    @ManyToMany
    @Column(name = "following")
    private var _following: MutableSet<User> = following.toMutableSet()

    @OneToMany
    @Column(name = "trust_evaluations")
    private var _trustEvaluations: MutableSet<TrustEvaluation> = trustEvaluations.toMutableSet()

    @NotBlank
    var username: String = username
        set(value) {
            require(value.isNotBlank()) { "Username cannot be blank" }
            field = value
        }

    @NotBlank
    var password: String = password
        set(value) {
            require(value.isNotBlank()) { "Password cannot be blank" }
            field = value
        }

    var following: Set<User> = _following
        get() = _following
        set(value) {
            field = value
            _following = value.toMutableSet()
        }

    var trustEvaluations: Set<TrustEvaluation> = _trustEvaluations
        get() = _trustEvaluations
        set(value) {
            field = value
            _trustEvaluations = value.toMutableSet()
        }

}
