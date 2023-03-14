package com.kamelia.ugeoverflow.follow

import com.kamelia.ugeoverflow.core.AbstractIdEntity
import com.kamelia.ugeoverflow.user.User
import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.io.Serializable
import java.util.*

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

    var trust: Int = 0
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
        follower: User,
        followed: User,
    ) : Serializable {

        @ManyToOne
        @JoinColumn(name = "follower_id")
        private var _follower: User = follower

        @ManyToOne
        @JoinColumn(name = "followed_id")
        private var _followed: User = followed

        val follower: User
            get() = _follower

        val followed: User
            get() = _followed

    }

}
