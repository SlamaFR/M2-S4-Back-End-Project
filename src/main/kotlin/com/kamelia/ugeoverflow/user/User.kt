package com.kamelia.ugeoverflow.user

import com.kamelia.ugeoverflow.AbstractIdEntity
import com.kamelia.ugeoverflow.evaluation.TrustEvaluation
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
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

    @JoinTable(
        name = "user_following",
        joinColumns = [JoinColumn(name = "follower_id")],
        inverseJoinColumns = [JoinColumn(name = "followed_id")]
    )
    @ManyToMany
    @Column(name = "following")
    private var _following: MutableSet<User> = following.toMutableSet()

    @OneToMany
    @JoinColumn(name = "evaluator_id")
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

    var following: Set<User>
        get() = _following
        set(value) {
            _following = value.toMutableSet()
        }

    var trustEvaluations: Set<TrustEvaluation>
        get() = _trustEvaluations
        set(value) {
            _trustEvaluations = value.toMutableSet()
        }

}
