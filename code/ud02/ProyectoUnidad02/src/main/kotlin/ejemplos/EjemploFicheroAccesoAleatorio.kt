package org.example.ejemplos

import java.nio.ByteBuffer // "contenedor" de bytes en memoria.
import java.nio.ByteOrder // especificar el orden de los bytes
import java.nio.channels.FileChannel //canal que conecta con el fichero
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
const val TAMANO_ID = Int.SIZE_BYTES // 4 bytes
const val TAMANO_NOMBRE = 20 // String de tamaño fijo 20 bytes
const val TAMANO_TEMPERATURA = Double.SIZE_BYTES // 8 bytes
const val TAMANO_HUMEDAD = Double.SIZE_BYTES // 8 bytes
const val TAMANO_REGISTRO = TAMANO_ID + TAMANO_NOMBRE + TAMANO_TEMPERATURA + TAMANO_HUMEDAD
fun main() {
    val rutaFichero = Path.of("multimedia/bin/mediciones.dat")
    escribirMedicion(rutaFichero, 101, "Atenea",25.5, 60.2)
    escribirMedicion(rutaFichero, 102, "Hera",26.1, 58.9)
    escribirMedicion(rutaFichero, 103, "Iris",28.4, 65.9)
    escribirMedicion(rutaFichero, 104, "Selene",28.4, 65.9)
    leerMediciones(rutaFichero) //leer todas las mediciones

    println("--- ACTULIZANDO LA MEDICIÓN CON ID 102 ---")
    actualizarMedicion(rutaFichero, 102, 21.0, 72.3)
    leerMediciones(rutaFichero)
}
// Función que escribe una medición en el fichero.
fun escribirMedicion(ruta: Path, idSensor: Int, nombre: String, temperatura: Double, humedad: Double) {
    /* .use { ... } abre el canal (se cerrará automáticamente al final del bloque) Escribir con APPEND para añadir el final */
    FileChannel.open(ruta, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.APPEND).use { canal ->
        // Crear un ByteBuffer de nuestro tamaño y especificamos el orden de bytes
        val buffer = ByteBuffer.allocate(TAMANO_REGISTRO)
        buffer.order(ByteOrder.nativeOrder())
        /* Escribimos los datos en el buffer en el orden correcto.
        'put' avanza la "posición" interna del buffer.*/
        buffer.putInt(idSensor) // Escribe 4 bytes
        /* Para escribir el String hay que convertirlo a un array de bytes de tamaño fijo.
        Inicializamos el array de bytes rellenándolo con el carácter espacio.
        ' '.code.toByte() convierte el carácter espacio a su valor de byte.*/
        val nombreCompleto = ByteArray(TAMANO_NOMBRE) { ' '.code.toByte() }
        // Convertimos el String de entrada a un array de bytes temporal.
        val nombreBytes = nombre.toByteArray(Charsets.UTF_8)
        /* Copiamos los bytes del String al principio de nuestro array de tamaño fijo.
        Si 'nombre' ocupa menos de 20 bytes, el resto de 'nombreCompleto' seguirá relleno de espacios.
        Si 'nombre' ocupa más de 20 bytes, solo se copiarán los primeros 20.*/
        nombreBytes.copyInto(nombreCompleto)
        buffer.put(nombreCompleto) // Escribe 20 bytes
        buffer.putDouble(temperatura) // Escribe 8 bytes
        buffer.putDouble(humedad) // Escribe 8 bytes
        /* 'flip()' prepara el buffer para ser leído o escrito
        Resetea la 'posición' a 0 y limita al tamaño total
        El canal escribirá desde la posición 0 hasta la 20 */
        buffer.flip()
        // Escribimos el contenido del buffer en el fichero a través del canal.
        canal.write(buffer)
        println ("Medición (ID: $idSensor) escrita correctamente.")
    }
}
// Función que lee TODAS las mediciones almacenadas en el fichero.
fun leerMediciones(ruta: Path) {
    if (!Files.exists(ruta)) {
        println ("El fichero ${ruta.fileName} no existe. No hay nada que leer.")
    } else {
        println ("\n--- Leyendo todas las mediciones ---")
        FileChannel.open(ruta, StandardOpenOption.READ).use { canal ->
            // Crear buffer
            val buffer = ByteBuffer.allocate(TAMANO_REGISTRO)
            buffer.order(ByteOrder.nativeOrder())
            /* Leer del canal en un bucle hasta que se alcance el final del fichero.
            canal.read(buffer) lee bytes del fichero y los guarda en el buffer.
            Devuelve el número de bytes leídos, o -1 si ya no hay más datos. */
            while (canal.read(buffer) > 0) {
                /* Después de `canal.read()` su posición está al final.
                `flip()` resetear la posición a 0. Para poder leer
                los datos que acabamos de cargar desde el principio del buffer. */
                buffer.flip()
                // Leemos los datos en el mismo orden en que los escribimos.
                val id = buffer.getInt()
                // Crear un array de bytes vacío para guardar los datos del nombre.
                val nombreCompleto = ByteArray(TAMANO_NOMBRE)
                // Leer 20 bytes del buffer y los guardamos en nuestro array.
                buffer.get(nombreCompleto)
                /* Convertir el array de bytes a un String. Usar .trim() para eliminar
                los espacios en blanco que se escribieron al final */
                val nombre = String(nombreCompleto, Charsets.UTF_8).trim()
                val temp = buffer.getDouble()
                val hum = buffer.getDouble()
                println ("  - ID: $id, Nombre: $nombre, Temperatura: $temp °C, Humedad: $hum %")
                // `clear()` resetea la posición a 0 y el límite a la capacidad total.
                buffer.clear()
            }
        }
    }
}

fun actualizarMedicion(ruta: Path, idSensorBuscado: Int, nuevaTemperatura: Double, nuevaHumedad: Double) {
    if (!Files.exists(ruta)) {
        println ("Error: El fichero no existe, no se puede actualizar.")
    } else {
        println ("\nIntentando actualizar medición para ID: $idSensorBuscado...")
        FileChannel.open(ruta, StandardOpenOption.READ, StandardOpenOption.WRITE).use { canal ->
            // Creamos un buffer pequeño, solo para leer el ID en cada iteración.
            // No necesitamos cargar el registro completo solo para buscar.
            val buffer = ByteBuffer.allocate(TAMANO_ID)
            buffer.order(ByteOrder.nativeOrder())
            var posicionActual: Long = 0
            var encontrado = false
            while (canal.position() < canal.size() && !encontrado) {
                // Guardar la posición del inicio del registro que estamos a punto de leer.
                posicionActual = canal.position()

                // Limpiamos y leemos solo los 4 bytes del ID.
                buffer.clear()
                canal.read(buffer)

                // Preparamos el buffer para leer el entero.
                buffer.flip()
                val idActual = buffer.getInt()
                println ("leyendo ID: " + idActual)
                // Comparamos el ID leído con el que estamos buscando.
                if (idActual == idSensorBuscado) {
                    encontrado = true
                    println ("Sensor $idSensorBuscado en posición $posicionActual.")
                    // Posición temperatura = inicio registro + tamaño del ID + tamaño nombre
                    canal.position(posicionActual + TAMANO_ID + TAMANO_NOMBRE)
                    val bufferDatos = ByteBuffer.allocate(TAMANO_TEMPERATURA + TAMANO_HUMEDAD)
                    bufferDatos.order(ByteOrder.nativeOrder())
                    bufferDatos.putDouble(nuevaTemperatura)
                    bufferDatos.putDouble(nuevaHumedad)
                    bufferDatos.flip()
                    canal.write(bufferDatos)
                    println ("Medición actualizada con éxito a Temp: $nuevaTemperatura, Hum: $nuevaHumedad.")
                } else {
                    canal.position(posicionActual + TAMANO_REGISTRO)
                }
            }
            if (!encontrado) {
                println ("Medición con ID: $idSensorBuscado no encontrada")
            }
        }
    }
}