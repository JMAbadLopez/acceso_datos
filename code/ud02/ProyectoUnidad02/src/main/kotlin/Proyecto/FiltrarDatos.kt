package org.example.Proyecto

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

fun main() {

    val entradaJSON = Path.of("proyecto/datos/datos_ini/canciones.json")
    val salidaJSON  = Path.of("proyecto/datos/datos_fin/canciones_filtradas.json")

    val datos: List<Cancion> = leerDatosIniciales(entradaJSON)

    for (dato in datos) {
        println("  - ID: ${dato.id_cancion}, Título: ${dato.titulo}, Artista: ${dato.artista}, Duración (mins.): ${dato.duracion} días")
    }

    filtrarDatos(salidaJSON, datos)

}

fun leerDatosIniciales(ruta: Path): List<Cancion> {

    var canciones: List<Cancion> = emptyList()
    val jsonString = Files.readString(ruta)

    canciones = Json.decodeFromString<List<Cancion>>(jsonString)
    return canciones
}

/**
 * Filtra canciones con una duración mayor de 6 y almacena
 * en un fichero de salida
 */
fun filtrarDatos(ruta: Path, canciones: List<Cancion>) {

    val cancionesFiltradas = mutableListOf<Cancion>()

    for (cancion in canciones) {
        if( cancion.duracion > 6 ) cancionesFiltradas.add(cancion)
    }

    try {

        val json = Json { prettyPrint = true }.encodeToString(cancionesFiltradas)
        Files.writeString(ruta, json)

        println("\nInformación guardada en: $ruta")

    } catch (e: Exception) {

        println("Error: ${e.message}")
    }
}