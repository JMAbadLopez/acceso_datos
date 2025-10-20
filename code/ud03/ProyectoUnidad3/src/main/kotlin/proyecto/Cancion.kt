package proyecto

data class Cancion(
    val idCancion: Int ?= null,
    val titulo: String,
    val idArtista: Int,
    val duracion: Double
)
