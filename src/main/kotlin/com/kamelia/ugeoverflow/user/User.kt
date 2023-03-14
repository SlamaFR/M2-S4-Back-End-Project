package com.kamelia.ugeoverflow.user

import com.kamelia.ugeoverflow.core.AbstractIdEntity
import com.kamelia.ugeoverflow.follow.FollowedUser
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank

@Entity
@Table(name = "user")
class User(
    username: String,
    password: String,
    followedUsers: Set<FollowedUser> = emptySet(),
) : AbstractIdEntity() {

    init {
        require(username.isNotBlank()) { "Username cannot be blank" }
        require(password.isNotBlank()) { "Password cannot be blank" }
    }

    @OneToMany
    @JoinColumn(name = "follower_id")
    private var _following: MutableSet<FollowedUser> = followedUsers.toMutableSet()

    @NotBlank
    @Column(name = "username", unique = true)
    private var _username: String = username

    val username: String
        get() = _username

    @NotBlank
    var password: String = password
        set(value) {
            require(value.isNotBlank()) { "Password cannot be blank" }
            field = value
        }

    val followed: Set<FollowedUser>
        get() = _following

}
