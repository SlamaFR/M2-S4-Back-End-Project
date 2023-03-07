package com.kamelia.ugeoverflow.tag

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Transient
import jakarta.validation.constraints.NotBlank
import java.util.*
import org.hibernate.annotations.GenericGenerator

@Entity
@Table(name = "tags")
class Tag(
    @NotBlank
    var name: String,
) {

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private var _id: String? = null

    @delegate:Transient
    val id: UUID by lazy { UUID.fromString(_id) }

    init {
        require(name.isNotBlank()) { "Tag name cannot be blank" }
    }

}
