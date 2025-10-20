package ejemplos

// Fichero: PlantasDAO.kt

object PlantasDAO {

    fun listarPlantas(): List<Planta> {
        val lista = mutableListOf<Planta>()
        val sql = "SELECT * FROM plantas"
        ConexionBD.getConnection()?.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                val rs = stmt.executeQuery()
                while (rs.next()) {
                    lista.add(Planta(
                        id = rs.getInt("id"),
                        nombreComun = rs.getString("nombre_comun"),
                        nombreCientifico = rs.getString("nombre_cientifico"),
                        frecuenciaRiego = rs.getInt("frecuencia_riego"),
                        altura = rs.getDouble("altura")
                    ))
                }
            }
        }
        return lista
    }

    fun consultarPlantaPorId(id: Int): Planta? {
        var planta: Planta? = null
        val sql = "SELECT * FROM plantas WHERE id = ?"
        ConexionBD.getConnection()?.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, id)
                val rs = stmt.executeQuery()
                if (rs.next()) {
                    planta = Planta(
                        id = rs.getInt("id"),
                        nombreComun = rs.getString("nombre_comun"),
                        nombreCientifico = rs.getString("nombre_cientifico"),
                        frecuenciaRiego = rs.getInt("frecuencia_riego"),
                        altura = rs.getDouble("altura")
                    )
                }
            }
        }
        return planta
    }

    fun insertarPlanta(planta: Planta) {
        val sql = "INSERT INTO plantas(nombre_comun, nombre_cientifico, frecuencia_riego, altura) VALUES (?, ?, ?, ?)"
        ConexionBD.getConnection()?.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, planta.nombreComun)
                stmt.setString(2, planta.nombreCientifico)
                stmt.setInt(3, planta.frecuenciaRiego)
                stmt.setDouble(4, planta.altura)
                if (stmt.executeUpdate() > 0) {
                    println("${planta.nombreComun} insertada con éxito.")
                }
            }
        }
    }

    fun actualizarPlanta(planta: Planta) {
        if (planta.id == null) {
            println("Error: No se puede actualizar una planta sin ID.")
            return
        }
        val sql = "UPDATE plantas SET nombre_comun = ?, nombre_cientifico = ?, frecuencia_riego = ?, altura = ? WHERE id = ?"
        ConexionBD.getConnection()?.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, planta.nombreComun)
                stmt.setString(2, planta.nombreCientifico)
                stmt.setInt(3, planta.frecuenciaRiego)
                stmt.setDouble(4, planta.altura)
                stmt.setInt(5, planta.id)
                if (stmt.executeUpdate() > 0) {
                    println("Planta con ID ${planta.id} actualizada.")
                } else {
                    println("No se encontró ninguna planta con ID ${planta.id} para actualizar.")
                }
            }
        }
    }

    fun eliminarPlanta(id: Int) {
        val sql = "DELETE FROM plantas WHERE id = ?"
        ConexionBD.getConnection()?.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, id)
                if (stmt.executeUpdate() > 0) {
                    println("Planta con ID $id eliminada.")
                } else {
                    println("No se encontró ninguna planta con ID $id para eliminar.")
                }
            }
        }
    }
}