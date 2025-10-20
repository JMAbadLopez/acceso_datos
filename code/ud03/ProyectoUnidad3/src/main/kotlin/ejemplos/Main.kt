package ejemplos

import proyecto.ConexionBD

fun main() {

    // Listar todas las plantas
    println("Lista de plantas:")
    PlantasDAO.listarPlantas().forEach {
        println(" - [${it.id}] ${it.nombreComun} (${it.nombreCientifico}), riego cada ${it.frecuenciaRiego} días, altura: ${it.altura} m")
    }

    // Consultar planta por ID
    val planta = PlantasDAO.consultarPlantaPorId(3)
    if (planta != null) {
        println("Planta encontrada: [${planta.id}] ${planta.nombreComun} (${planta.nombreCientifico}), riego cada ${planta.frecuenciaRiego} días, altura: ${planta.altura} m")
    } else {
        println("No se encontró ninguna planta con ese ID.")
    }

    // Insertar plantas
    PlantasDAO.insertarPlanta(
        Planta(
            nombreComun = "Palmera",
            nombreCientifico = "Arecaceae",
            frecuenciaRiego = 2,
            altura = 8.5
        )
    )

    // Actualizar planta con id=1
    PlantasDAO.actualizarPlanta(
        Planta(
            id = 1,
            nombreComun = "Aloe Arborescens",
            nombreCientifico = "Aloe barbadensis miller",
            frecuenciaRiego = 5,
            altura = 0.8
        )
    )

    // Eliminar planta con id=2
    PlantasDAO.eliminarPlanta(2)
}