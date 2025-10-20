package ejemplos

import java.io.File
import java.sql.DriverManager

fun main() {
    val dbPath = "datos/plantas.sqlite" // Asegúrate de que exista este fichero
    val dbFile = File(dbPath)
    println("Ruta absoluta de la BD: ${dbFile.absolutePath}")
    val url = "jdbc:sqlite:${dbFile.absolutePath}"

    try {
        DriverManager.getConnection(url).use { conn ->
            println("Conexión establecida correctamente con SQLite.")
        }
    } catch (e: Exception) {
        println("Error al conectar: ${e.message}")
    }
}