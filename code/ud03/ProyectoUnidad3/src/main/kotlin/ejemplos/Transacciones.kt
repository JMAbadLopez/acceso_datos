package ejemplos

import java.sql.Connection
import java.sql.SQLException

fun main() {
    actualizarAlturasConTransaccion(1, 0.9, 3, 0.95)
    llevarPlantasAJardin(1,1,10)
}

fun actualizarAlturasConTransaccion(id1: Int, nuevaAltura1: Double, id2: Int, nuevaAltura2: Double) {
    var conn: Connection? = null
    try {
        conn = ConexionBD.getConnection()
        conn?.autoCommit = false // 1. Iniciar transacción

        conn?.prepareStatement("UPDATE plantas SET altura = ? WHERE id = ?")?.use { stmt ->
            stmt.setDouble(1, nuevaAltura1);
            stmt.setInt(2, id1);
            stmt.executeUpdate()
        }
        conn?.prepareStatement("UPDATE plantas SET altura = ? WHERE id = ?")?.use { stmt ->
            stmt.setDouble(1, nuevaAltura2);
            stmt.setInt(2, id2);
            stmt.executeUpdate()
        }

        conn?.commit() // 2. Confirmar cambios
        println("Transacción completada.")
    } catch (e: SQLException) {
        println("Error en la transacción, se revierten los cambios: ${e.message}")
        conn?.rollback() // 3. Revertir cambios
    } finally {
        conn?.autoCommit = true
        conn?.close()
    }
}

fun llevarPlantasAJardin(id_jardin: Int, id_planta: Int, cantidad: Int) {
    var conn: Connection? = null
    try {
        conn = ConexionBD.getConnection()
        conn?.autoCommit = false  // Iniciar transacción manual

        // Restar stock a la planta
        conn?.prepareStatement("UPDATE plantas SET stock = stock - ? WHERE id = ?").use { stmt ->
            stmt?.setInt(1, cantidad);
            stmt?.setInt(2, id_planta);
            stmt?.executeUpdate()
        }

        // Añadir línea en tabla jardines_plantas
        conn?.prepareStatement("INSERT INTO jardines_plantas(id_jardin, id_planta, cantidad) VALUES (?, ?, ?)")
            .use { stmt ->
                stmt?.setInt(1, id_jardin);
                stmt?.setInt(2, id_planta);
                stmt?.setInt(3, cantidad);
                stmt?.executeUpdate()
            }

        // Confirmar cambios
        conn?.commit()
        println("Transacción realizada con éxito.")
    } catch (e: SQLException) {
        if (e.message?.contains("UNIQUE constraint failed") == true) {
            println("Intento de insertar clave duplicada")
            conn?.rollback()
            println("Transacción revertida.")
        } else {
            throw e // otros errores, relanzamos
        }
    } finally {
        // Código que se ejecuta siempre
        println("Fin del programa.")
    }
}

