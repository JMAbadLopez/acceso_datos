package ejemplos

import java.nio.file.Files
import java.nio.file.Path
fun main() {
    val carpetaPrincipal = Path.of("multimedia")
    println ("--- Mostrando la estructura final con Files.walk() ---")
    try {
        Files.walk(carpetaPrincipal).use { stream ->
            // Ordenar el stream para una visualización más predecible
            stream.sorted().forEach { path ->
                // Calcular profundidad para la indentación
                // Restamos el número de componentes de la ruta base para que el directorio principal no tenga indentación
                val profundidad = path.nameCount - carpetaPrincipal.nameCount
                val indentacion = "\t".repeat(profundidad)
                // Determinamos si es directorio o fichero para el prefijo
                val prefijo = if (Files.isDirectory(path)) "[DIR]" else "[FILE]"
                // No imprimimos la propia carpeta raíz, solo su contenido
                if (profundidad > 0) {
                    println ("$indentacion$prefijo ${path.fileName}")
                }
            }
        }
    } catch (e: Exception) {
        println ("\n--- Ocurrió un error durante el recorrido ---")
        e.printStackTrace()
    }
}