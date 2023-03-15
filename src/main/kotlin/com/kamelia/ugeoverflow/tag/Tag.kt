package com.kamelia.ugeoverflow.tag

import com.kamelia.ugeoverflow.core.AbstractIdEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Index
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank
import java.util.UUID

@Entity
@Table(
    name = "tag",
    indexes = [
        Index(name = "tag_name_idx", columnList = "name", unique = true),
    ],
)
class Tag(
    name: String,
) : AbstractIdEntity() {

    init {
        require(name.isNotBlank()) { "Tag name cannot be blank" }
    }

    @NotBlank
    @Column(name = "name", unique = true)
    private var _name: String = name

    val name: String
        get() = _name

}
