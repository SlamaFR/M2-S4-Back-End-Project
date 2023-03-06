package com.kamelia.ugeoverflow.tag

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotEmpty
import org.hibernate.annotations.GenericGenerator

@Entity
@Table(name = "tags")
open class Tag(
    var name: String,

    @Id
    @NotEmpty
    @GenericGenerator(name = "uuid", strategy = "uuid4")
    var id: String? = null
) {

    constructor(): this("")

//    @field:javax.persistence.Transient
//    var id: UUID
//        get() = UUID.fromString(_id)
//        set(value) {
//            _id = value.toString()
//        }

}
