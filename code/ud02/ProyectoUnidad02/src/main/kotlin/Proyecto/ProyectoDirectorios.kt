package org.example.Proyecto
import java.nio.file.Files
import java.nio.file.Path

fun main() {

    val rutaPrincipal = Path.of("proyecto")
    val directorioIni = Path.of("datos", "datos_ini")
    val directorioFin = Path.of("datos", "datos_fin")

    println("CREACIÓN DE RUTAS PROYECTO")

    try {
        /**
         * Creamos las rutas del Proyecto
         */
        Files.list(rutaPrincipal).use {
            streamPath -> streamPath.forEach {
                pathFichero ->

                val rutaIni = rutaPrincipal.resolve(directorioIni)
                val rutaFin = rutaPrincipal.resolve(directorioFin)

                if(pathFichero.toString() == "proyecto/datos") {
                    println("\tCreando rutas...")
                    if(Files.notExists(rutaIni)) {
                        println("\tCreación de ruta para DATOS_INI")
                        Files.createDirectories(rutaIni)
                    }

                    if(Files.notExists(rutaFin)) {
                        println("\tCreación de ruta para DATOS_FIN")
                        Files.createDirectories(rutaFin)
                    }
                }
            }
        }
    } catch (e: Exception) {
        println(e.message)
    }
    
    println("MOSTRANDO ESTRUCTURA DE DIRECTORIOS Y FICHEROS")
    try {
        Files.walk(rutaPrincipal).use { stream ->

            stream.sorted().forEach { path ->
                val profundidad = path.nameCount - rutaPrincipal.nameCount
                val indentacion = "\t".repeat(profundidad)
                val prefijo = if (Files.isDirectory(path)) "[DIR]" else "[FILE]"

                if (profundidad > 0) {
                    println("$indentacion$prefijo ${path.fileName}")
                }
            }
        }
    } catch (e: Exception) {
        println(e.message)
    }
}