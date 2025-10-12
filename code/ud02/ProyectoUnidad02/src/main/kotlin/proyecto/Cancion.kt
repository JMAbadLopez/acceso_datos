package org.example.Proyecto

import kotlinx.serialization.*
import kotlinx.serialization.json.*

/** Data Class de Cancion
 * @id_cancion - Identificador único
 * @titulo - Título de la canción
 * @artista - Autor de la canción
 * @duracion - Duración en minutos (decimales)
 */
@Serializable
data class Cancion (val id_cancion: Int, val titulo: String, val artista: String, val duracion: Double)