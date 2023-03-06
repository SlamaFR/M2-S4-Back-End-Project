package com.kamelia.ugeoverflow.tag

import java.util.UUID
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
import javax.validation.constraints.NotEmpty
import org.hibernate.annotations.GenericGenerator

@Entity
@Table(name = "tags")
class Tag(
    var name: String,
    @Id
    @NotEmpty
    @GenericGenerator(name = "uuid", strategy = "uuid4")
    var id: String? = null
) {


//    @field:javax.persistence.Transient
//    var id: UUID
//        get() = UUID.fromString(_id)
//        set(value) {
//            _id = value.toString()
//        }

}
