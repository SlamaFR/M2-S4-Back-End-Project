package com.kamelia.ugeoverflow.core

import jakarta.persistence.Column
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import java.util.*
import org.hibernate.annotations.GenericGenerator

@MappedSuperclass
abstract class AbstractIdEntity(id: UUID? = null) {

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private var _id: UUID? = id

    val id: UUID
        get() = _id!!

}
