package com.example.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date

object JwtConfig {
    private const val SECRET = "secret"
    private const val ISSUER = "ktor_issuer"
    private const val AUDIENCE = "ktor_audience"
    private const val VALIDITY = 36_000_00 * 10; // in milliseconds default 10hrs
    private val algorithm = Algorithm.HMAC256(SECRET)

    fun makeToken(email: String): String = JWT.create()
        .withSubject("Authentication")
        .withIssuer(ISSUER)
        .withAudience(AUDIENCE)
        .withClaim("email", email)
        .withExpiresAt(getExpiration())
        .sign(algorithm)

    private fun getExpiration() = Date(System.currentTimeMillis() + VALIDITY)
}