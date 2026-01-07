package edu.gva.es.domain

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val mail: String,
    val password: String
)