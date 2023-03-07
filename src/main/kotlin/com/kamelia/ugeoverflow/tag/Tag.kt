package com.kamelia.ugeoverflow.tag

import com.kamelia.ugeoverflow.AbstractIdEntity
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank

@Entity
@Table(name = "tag")
class Tag(
    @NotBlank
    var name: String,
) : AbstractIdEntity() {

    init {
        require(name.isNotBlank()) { "Tag name cannot be blank" }
    }

}
