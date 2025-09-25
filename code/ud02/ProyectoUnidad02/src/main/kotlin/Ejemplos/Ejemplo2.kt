package org.example.Ejemplos

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.io.path.extension // Extensión de Kotlin para obtener la extensión
fun main() {
    // 1. Ruta de la carpeta a organizar
    val carpeta = Path.of("multimedia_test")
    println ("--- Iniciando la organización de la carpeta: " + carpeta + "---")
    try {
        // 2. Recorrer la carpeta desordenada y utilizar .use para asegurar que los recursos del sistema se cierren correctamente
        Files.list(carpeta).use { streamDePaths ->
            streamDePaths.forEach { pathFichero ->
                // 3. Solo interesan los ficheros, ignorar subcarpetas
                if (Files.isRegularFile(pathFichero)) {
                    // 4. Obteners la extensión del fichero (ej: "pdf", "jpg")
                    val extension = pathFichero.extension.lowercase()
                    if (extension.isBlank()) {
                        println ("-> Ignorando: " + pathFichero.fileName)
                        return@forEach // Salta a la siguiente iteración del bucle
                    }
                    // 5. Crear la ruta del directorio de destino
                    val carpetaDestino = carpeta.resolve(extension)
                    // 6. Crear el directorio de destino si no existe
                    if (Files.notExists(carpetaDestino)) {
                        println ("-> Creando nueva carpeta " + extension)
                        Files.createDirectories(carpetaDestino)
                    }
                    // 7. Mover el fichero a su nueva carpeta
                    val pathDestino = carpetaDestino.resolve(pathFichero.fileName)
                    Files.move(pathFichero, pathDestino, StandardCopyOption.REPLACE_EXISTING)
                    println ("-> Moviendo " + pathFichero.fileName + " a " + extension)
                }
            }
        }
        println ("\n--- ¡Organización completada con éxito! ---")
    } catch (e: Exception) {
        println ("\n--- Ocurrió un error durante la organización ---")
        e.printStackTrace()
    }
}