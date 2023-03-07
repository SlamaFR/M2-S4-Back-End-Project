package com.kamelia.ugeoverflow.core

import at.favre.lib.crypto.bcrypt.BCrypt
import at.favre.lib.crypto.bcrypt.LongPasswordStrategies
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.security.SecureRandom

@Service
class Hasher : PasswordEncoder {

    private val version = BCrypt.Version.VERSION_2B
    private val strategy = LongPasswordStrategies.hashSha512(version)
    private val hasher = BCrypt.with(version, SecureRandom(), strategy)
    private val verifier = BCrypt.verifyer(version, strategy)

    companion object {
        private const val STRENGTH = 12
    }

    private fun hash(password: String, strength: Int = STRENGTH): String =
        hasher.hashToString(strength, password.toCharArray())

    private fun verify(password: String, hash: String): BCrypt.Result =
        verifier.verify(password.toCharArray(), hash.toCharArray())

    override fun encode(rawPassword: CharSequence?): String =
        hash(rawPassword.toString())

    override fun matches(rawPassword: CharSequence?, encodedPassword: String?): Boolean =
        verify(rawPassword.toString(), encodedPassword.toString()).verified

}