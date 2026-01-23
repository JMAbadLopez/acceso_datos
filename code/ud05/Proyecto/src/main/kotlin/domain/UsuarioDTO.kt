package edu.gva.es.domain

import kotlinx.serialization.Serializable
import java.time.LocalDate

/**
 * DTO Completo de Usuario.
 * Contiene toda la información, incluida la contraseña.
 * Se usa para procesos internos como el Login o el Registro.
 */
@Serializable
data class UsuarioDTO(
    val id: Int,
    val nombre: String,
    val mail: String,
    val password: String,
    @Serializable(with = LocalDateSerializer::class)
    val fechaNacimiento: LocalDate
)

/**
 * DTO Público de Usuario.
 * Esta versión oculta la contraseña (password).
 * Es la que enviaremos a través de la API en las consultas GET.
 */
@Serializable
data class UsuarioPublicoDTO(
    val id: Int,
    val nombre: String,
    val mail: String,
    @Serializable(with = LocalDateSerializer::class)
    val fechaNacimiento: LocalDate
)

/**
 * Función de extensión para convertir un UsuarioDTO (completo)
 * en un UsuarioPublicoDTO (seguro).
 */
fun UsuarioDTO.toPublico() = UsuarioPublicoDTO(
    id = this.id,
    nombre = this.nombre,
    mail = this.mail,
    fechaNacimiento = this.fechaNacimiento
)