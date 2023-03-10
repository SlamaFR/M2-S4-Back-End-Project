package com.kamelia.ugeoverflow.util

import java.util.*

fun UUID.toBase64(): String = Base64.getEncoder().encodeToString(this.toString().toByteArray())

fun String.toUUIDFromBase64OrNull(): UUID? = try {
    UUID.fromString(String(Base64.getDecoder().decode(this)))
} catch (_: IllegalArgumentException) {
    null
}

fun String.toUUIDOrNull(): UUID? = try {
    UUID.fromString(this)
} catch (_: IllegalArgumentException) {
    null
}
