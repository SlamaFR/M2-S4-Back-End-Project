package com.kamelia.ugeoverflow.core

import at.favre.lib.crypto.bcrypt.BCrypt
import at.favre.lib.crypto.bcrypt.LongPasswordStrategies
import org.springframework.stereotype.Service
import java.security.SecureRandom

@Service
class Hasher {

    private val version = BCrypt.Version.VERSION_2B
    private val strategy = LongPasswordStrategies.hashSha512(version)
    private val hasher = BCrypt.with(version, SecureRandom(), strategy)
    private val verifier = BCrypt.verifyer(version, strategy)

    companion object {
        private const val STRENGTH = 12
    }

    fun hash(password: String, strength: Int = STRENGTH): String =
        hasher.hashToString(strength, password.toCharArray())

    fun verify(password: String, hash: String): BCrypt.Result =
        verifier.verify(password.toCharArray(), hash.toCharArray())

}