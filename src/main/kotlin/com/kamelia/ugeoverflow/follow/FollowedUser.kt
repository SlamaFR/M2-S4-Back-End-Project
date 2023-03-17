package com.kamelia.ugeoverflow.follow

import com.kamelia.ugeoverflow.user.User
import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.io.Serializable

@Entity
@Table(
    name = "followed_user",
    uniqueConstraints = [UniqueConstraint(columnNames = ["follower_id", "followed_id"])]
)
class FollowedUser(
    follower: User,
    followed: User,
) {

    @EmbeddedId
    private var id: Id = Id(follower, followed)

    val follower: User
        get() = id.follower

    val followed: User
        get() = id.followed

    var trust: Int = 1
        set(value) {
            checkTrust(value)
            field = value
        }

    private fun checkTrust(trust: Int) {
        require(trust in -50..50) {
            "Invalid evaluation: $trust. Evaluation must be in range [-50, 50]."
        }
    }

    @Embeddable
    class Id(
        @ManyToOne
        @JoinColumn(name = "follower_id")
        val follower: User,
        @ManyToOne
        @JoinColumn(name = "followed_id")
        val followed: User,
    ) : Serializable

}
