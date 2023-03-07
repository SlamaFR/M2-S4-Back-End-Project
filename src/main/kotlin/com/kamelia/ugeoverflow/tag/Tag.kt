package com.kamelia.ugeoverflow.tag

import com.kamelia.ugeoverflow.core.AbstractIdEntity
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank

@Entity
@Table(name = "tag")
class Tag(
    name: String,
) : AbstractIdEntity() {

    init {
        require(name.isNotBlank()) { "Tag name cannot be blank" }
    }

    @NotBlank
    var name: String = name
        set(value) {
            require(value.isNotBlank()) { "Tag name cannot be blank" }
            field = value
        }

}
