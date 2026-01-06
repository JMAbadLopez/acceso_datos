package edu.gva.es.domain

import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class UsuarioDTO(
    val id: Int,
    val nombre: String,
    val mail: String,
    val password: String,
    @Serializable(with = LocalDateSerializer::class) // Indicamos el traductor
    val fechaNacimiento: LocalDate
)