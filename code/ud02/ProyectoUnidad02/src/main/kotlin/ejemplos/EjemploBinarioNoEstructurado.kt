package org.example.ejemplos

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
fun main() {
    val ruta = Path.of("multimedia/bin/datos.bin")
    try {
        // Asegura que el directorio 'documentos' existe
        val directorio = ruta.parent
        if (directorio != null && !Files.exists(directorio)) {
            Files.createDirectories(directorio)
            println ("Directorio creado: ${directorio.toAbsolutePath()}")
        }
        // Verifica si se puede escribir
        if (!Files.isWritable(directorio)) {
            println ("No se tienen permisos de escritura en el directorio: $directorio")
        } else {
            // Datos a escribir
            val datos = byteArrayOf(1, 2, 3, 4, 5)
            Files.write(ruta, datos)
            println ("Fichero binario creado: ${ruta.toAbsolutePath()}")
            // Verifica si se puede leer
            if (!Files.isReadable(ruta)) {
                println ("No se tienen permisos de lectura para el fichero: $ruta")
            } else {
                // Lectura del fichero binario
                val bytes = Files.readAllBytes(ruta)
                println ("Contenido leído (byte a byte):")
                for (b in bytes) {
                    print ("$b ")
                }
            }
        }
    } catch (e: IOException) {
        println ("Ocurrió un error de entrada/salida: ${e.message}")
    } catch (e: SecurityException) {
        println ("No se tienen permisos suficientes: ${e.message}")
    } catch (e: Exception) {
        println ("Error inesperado: ${e.message}")
    }
}