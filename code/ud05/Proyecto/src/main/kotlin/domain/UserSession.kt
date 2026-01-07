package edu.gva.es.domain

import kotlinx.serialization.Serializable

@Serializable
data class UserSession(val email: String)