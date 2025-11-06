package ejemplos

import java.sql.Connection
import java.sql.SQLException

fun main() {
    println("--- LISTAR CON USE ---")
    listarConUse()
    println("\n--- LISTAR CON FINALLY---")
    listarConFinally()
}

// 1. Cierre automático con .use (Recomendado)
fun listarConUse() {
    ConexionBD.getConnection()?.use { conn ->
        conn.prepareStatement("SELECT * FROM plantas").use { stmt ->
            val rs = stmt.executeQuery()
            while (rs.next()) {
                println("- ${rs.getString("nombre_comun")}")
            }
        }
    }
}

// 2. Cierre manual con try-catch-finally
fun listarConFinally() {
    var conn: Connection? = null
    var stmt: java.sql.PreparedStatement? = null
    var rs: java.sql.ResultSet? = null
    try {
        conn = ConexionBD.getConnection()
        stmt = conn?.prepareStatement("SELECT * FROM plantas")
        rs = stmt?.executeQuery()
        while (rs?.next() == true) {
            println("- ${rs.getString("nombre_comun")}")
        }
    } catch (e: SQLException) {
        e.printStackTrace()
    } finally {
        rs?.close()
        stmt?.close()
        conn?.close()
    }
}

