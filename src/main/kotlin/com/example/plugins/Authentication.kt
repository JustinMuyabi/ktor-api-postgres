package com.example.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*

fun Application.configureAuthentication() {
    install(Authentication){
        jwt("auth-jwt") {
            realm= "ktor api app"
            verifier(
                JWT
                    .require(Algorithm.HMAC256("secret"))
                    .withAudience("ktor_audience")
                    .withIssuer("ktor_issuer")
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains("ktor_audience")) JWTPrincipal(credential.payload) else null
            }
            challenge { _, _ -> call.respond(HttpStatusCode.Unauthorized, "Invalid Token or Expired")}
        }
    }
}