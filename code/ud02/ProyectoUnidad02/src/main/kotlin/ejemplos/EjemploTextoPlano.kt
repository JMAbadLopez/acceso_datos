package ejemplos

import java.nio.file.Files
import java.nio.file.Paths
import java.nio.charset.StandardCharsets

fun main() {
    //Escritura en fichero de texto

    //writeString
    val texto = "Hola, mundo desde Kotlin"
    Files.writeString(Paths.get("documentos/saludo.txt"), texto)

    //write
    val ruta = Paths.get("documentos/texto.txt")
    val lineasParaGuardar = listOf(
        "Primera línea",
        "Segunda línea",
        "¡Hola desde Kotlin!"
    )
    Files.write(ruta, lineasParaGuardar, StandardCharsets.UTF_8)
    println ("Fichero de texto escrito.")

    //newBuffered
    Files.newBufferedWriter(Paths.get("documentos/log.txt")).use { writer ->
        writer.write("Log iniciado...\n")
        writer.write("Proceso completado.\n")
    }

    //Lectura del fichero de texto
    //readAllLines
    val lineasLeidas = Files.readAllLines(ruta)
    println ("Contenido leído con readAllLines:")
    for (lineas in lineasLeidas) {
        println (lineas)
    }
    //readString
    val contenido = Files.readString(ruta)
    println ("Contenido leído con readString:")
    println (contenido)
    //newBufferedReader
    Files.newBufferedReader(ruta).use { reader ->
        println ("Contenido leído con newBufferedReader:")
        reader.lineSequence().forEach { println (it) }
    }
}