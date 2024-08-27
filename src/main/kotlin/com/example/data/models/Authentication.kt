package com.example.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Authentication(
    val email: String,
    val password: String
)