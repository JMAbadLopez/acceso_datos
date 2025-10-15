package org.example.ejemplos

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.io.IOException
import kotlin.io.path.name // Necesaria para usar .name en rutaTemp

// --- CONSTANTES PARA DEFINIR EL TAMAÑO DE LOS REGISTROS (MUY IMPORTANTE) ---

// Definimos el tamaño exacto de cada campo para garantizar que cada registro ocupe lo mismo.
const val TAMANO_ID = Int.SIZE_BYTES        // 4 bytes para el ID (Int)
const val TAMANO_NOMBRE = 20                // 20 bytes fijos para el nombre (String)
const val TAMANO_TEMPERATURA = Double.SIZE_BYTES // 8 bytes para la temperatura (Double)
const val TAMANO_HUMEDAD = Double.SIZE_BYTES     // 8 bytes para la humedad (Double)

// Cada registro (medición) ocupará siempre 4 + 20 + 8 + 8 = 40 bytes en el archivo binario.
const val TAMANO_REGISTRO = TAMANO_ID + TAMANO_NOMBRE + TAMANO_TEMPERATURA + TAMANO_HUMEDAD

// --- FUNCIÓN MAIN: PUNTO DE ENTRADA Y FLUJO DE LA APLICACIÓN ---
fun main() {
    val rutaFichero = Path.of("multimedia/bin/mediciones.dat")

    // ** MEJORA DE ROBUSTEZ: Aseguramos que el directorio exista antes de escribir **
    try {
        Files.createDirectories(rutaFichero.parent)
        println("Directorio creado/verificado: ${rutaFichero.parent}")
    } catch (e: IOException) {
        println("Error al crear el directorio: ${e.message}")
        return // Salir si no podemos asegurar la ruta
    }

    // 1. ESCRITURA: Escribir las 4 mediciones al final del archivo.
    escribirMedicion(rutaFichero, 101, "Atenea", 25.5, 60.2)
    escribirMedicion(rutaFichero, 102, "Hera", 26.1, 58.9)
    escribirMedicion(rutaFichero, 103, "Iris", 28.4, 65.9)
    escribirMedicion(rutaFichero, 104, "Selene", 28.4, 65.9)

    // 2. LECTURA: Leer todos los registros escritos.
    leerMediciones(rutaFichero)

    // 3. ACTUALIZACIÓN: Modificar solo los campos de un registro específico.
    println("\n--- ACTULIZANDO LA MEDICIÓN CON ID 102 ---")
    actualizarMedicion(rutaFichero, 102, 21.0, 72.3)

    // 4. VERIFICACIÓN: Volver a leer todas las mediciones para ver el cambio.
    leerMediciones(rutaFichero)

    // 5. ELIMINACIÓN: Eliminar una medición (esto borra el registro de ID 103).
    println("\n--- ELIMINANDO LA MEDICIÓN CON ID 103 ---")
    eliminarMedicion(rutaFichero, 103)

    // 6. VERIFICACIÓN FINAL: Volver a leer para confirmar que el registro 103 ya no está.
    leerMediciones(rutaFichero)
}

// --- FUNCIÓN 1: ESCRIBIR UNA MEDICIÓN ---
// Escribe un registro de tamaño fijo (40 bytes) al final del fichero binario.
fun escribirMedicion(ruta: Path, idSensor: Int, nombre: String, temperatura: Double, humedad: Double) {
    // Usamos 'APPEND' para añadir al final del archivo. El canal se cierra automáticamente con '.use'.
    FileChannel.open(ruta, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.APPEND).use { canal ->

        // 1. Configurar el Buffer: Se reserva espacio y se define el orden de bytes nativo.
        val buffer = ByteBuffer.allocate(TAMANO_REGISTRO)
        buffer.order(ByteOrder.nativeOrder())

        // 2. Llenar el Buffer (Modo ESCRITURA): Cada 'put' avanza la posición interna del buffer.
        buffer.putInt(idSensor)

        // Manejo del String de tamaño fijo (20 bytes):
        val nombreCompleto = ByteArray(TAMANO_NOMBRE) { ' '.code.toByte() } // 20 bytes rellenos de espacios
        val nombreBytes = nombre.toByteArray(Charsets.UTF_8)
        nombreBytes.copyInto(nombreCompleto) // Copiamos la cadena, dejando los espacios si es más corta

        buffer.put(nombreCompleto) // Escribe los 20 bytes fijos
        buffer.putDouble(temperatura) // Escribe 8 bytes
        buffer.putDouble(humedad) // Escribe 8 bytes

        // 3. Preparar para el Canal: 'flip()' cambia el buffer de ESCRITURA a LECTURA.
        //    Establece la posición a 0 y el límite al final de los datos que acabamos de poner (40 bytes).
        buffer.flip()

        // 4. Escribir al Fichero: El canal escribe desde la posición 0 hasta el límite del buffer.
        canal.write(buffer)
        println ("Medición (ID: $idSensor) escrita correctamente.")
    }
}

// --- FUNCIÓN 2: LEER TODAS LAS MEDICIONES ---
// Recorre el fichero y lee los registros de 40 bytes uno por uno.
fun leerMediciones(ruta: Path) {
    if (!Files.exists(ruta)) {
        println ("El fichero ${ruta.name} no existe. No hay nada que leer.")
        return
    }

    println ("\n--- Leyendo todas las mediciones ---")
    FileChannel.open(ruta, StandardOpenOption.READ).use { canal ->

        // 1. Configurar el Buffer: Buffer del tamaño exacto de un registro.
        val buffer = ByteBuffer.allocate(TAMANO_REGISTRO)
        buffer.order(ByteOrder.nativeOrder())

        /* 2. Bucle de Lectura: Continúa leyendo mientras el canal logre cargar datos al buffer.
        `canal.read(buffer)` devuelve la cantidad de bytes leídos (o -1 al final del archivo). */
        while (canal.read(buffer) > 0) {

            // 3. Preparar para la Lectura: 'flip()' cambia el buffer de ESCRITURA a LECTURA.
            //    Esto nos permite leer los datos que acabamos de cargar desde el inicio.
            buffer.flip()

            // 4. Extraer Datos: Las llamadas 'get*' leen los bytes y AVANZAN la posición.
            val id = buffer.getInt()

            val nombreCompleto = ByteArray(TAMANO_NOMBRE)
            buffer.get(nombreCompleto) // Leemos los 20 bytes del nombre al array.

            // Convertir a String y usar '.trim()' para quitar los espacios de relleno.
            val nombre = String(nombreCompleto, Charsets.UTF_8).trim()
            val temp = buffer.getDouble()
            val hum = buffer.getDouble()

            println ("  - ID: $id, Nombre: $nombre, Temperatura: $temp °C, Humedad: $hum %")

            // 5. Preparar para el Siguiente Registro: 'clear()' resetea la posición a 0 y el límite a la capacidad total.
            //    Esto deja el buffer listo para el siguiente `canal.read()`.
            buffer.clear()
        }
    }
}

// --- FUNCIÓN 3: ACTUALIZAR UNA MEDICIÓN EXISTENTE ---
// Busca un registro por ID y sobrescribe solo los campos de Temperatura y Humedad.
fun actualizarMedicion(ruta: Path, idSensorBuscado: Int, nuevaTemperatura: Double, nuevaHumedad: Double) {
    if (!Files.exists(ruta)) {
        println ("Error: El fichero ${ruta.name} no existe, no se puede actualizar.")
        return
    }

    println ("\nIntentando actualizar medición para ID: $idSensorBuscado...")
    // Abrimos el canal para LECTURA (búsqueda) y ESCRITURA (actualización).
    FileChannel.open(ruta, StandardOpenOption.READ, StandardOpenOption.WRITE).use { canal ->

        // Buffer pequeño, solo para leer el ID de 4 bytes en cada iteración.
        val bufferID = ByteBuffer.allocate(TAMANO_ID)
        bufferID.order(ByteOrder.nativeOrder())

        var posicionActual: Long = 0
        var encontrado = false

        // Bucle que recorre el archivo, leyendo solo el ID en cada registro.
        while (canal.position() < canal.size() && !encontrado) {

            posicionActual = canal.position() // Guardamos el byte de inicio del registro.

            // 1. Leer solo el ID:
            bufferID.clear()
            canal.read(bufferID)
            bufferID.flip()
            val idActual = bufferID.getInt()

            // 2. Comprobar ID:
            if (idActual == idSensorBuscado) {
                encontrado = true
                println ("Sensor $idSensorBuscado encontrado en la posición: $posicionActual.")

                // 3. Posicionar para la Escritura: Saltamos el ID y el Nombre para ir directo a la Temperatura.
                // Posición = (Inicio del Registro) + (Tamaño ID) + (Tamaño Nombre)
                canal.position(posicionActual + TAMANO_ID + TAMANO_NOMBRE)

                // 4. Crear Buffer con nuevos datos (Temperatura y Humedad):
                val bufferDatos = ByteBuffer.allocate(TAMANO_TEMPERATURA + TAMANO_HUMEDAD)
                bufferDatos.order(ByteOrder.nativeOrder())
                bufferDatos.putDouble(nuevaTemperatura)
                bufferDatos.putDouble(nuevaHumedad)
                bufferDatos.flip()

                // 5. Sobrescribir: Escribimos 16 bytes (Temp + Hum) justo encima de los datos antiguos.
                canal.write(bufferDatos)
                println ("Medición actualizada con éxito a Temp: $nuevaTemperatura °C, Hum: $nuevaHumedad %.")

            } else {
                // 6. Si no es el ID, saltar al inicio del siguiente registro completo.
                canal.position(posicionActual + TAMANO_REGISTRO)
            }
        }

        if (!encontrado) {
            println ("Medición con ID: $idSensorBuscado no encontrada.")
        }
    }
}

// --- FUNCIÓN 4: ELIMINAR UNA MEDICIÓN ---
/* La eliminación en ficheros binarios de tamaño fijo se hace recreando el fichero.
Se lee el archivo original registro por registro, y se copia todo excepto el registro a eliminar. */
fun eliminarMedicion(ruta: Path, idSensorAEliminar: Int) {
    // 1. Definir ruta temporal en el mismo directorio que el original.
    val rutaTemp = Path.of(ruta.parent.toString(), "temp_mediciones.dat")
    var registroEliminado = false

    if (!Files.exists(ruta)) {
        println ("Error: El fichero ${ruta.name} no existe, no se puede eliminar.")
        return
    }

    println ("\n--- Intentando eliminar medición para el sensor con ID: $idSensorAEliminar... ---")

    // 2. Nos aseguramos de que el archivo temporal esté limpio antes de empezar a escribir.
    Files.deleteIfExists(rutaTemp)

    // Abrimos el canal del archivo original para LECTURA.
    FileChannel.open(ruta, StandardOpenOption.READ).use { canalOrigen ->

        // Buffer de tamaño de registro completo (40 bytes).
        val buffer = ByteBuffer.allocate(TAMANO_REGISTRO)
        buffer.order(ByteOrder.nativeOrder())

        // 3. Recorremos el archivo original, leyendo cada registro completo.
        while (canalOrigen.read(buffer) > 0) {

            buffer.flip() // Poner en modo lectura

            // Leemos el ID (sin avanzar el buffer para no estropearlo)
            val id = buffer.getInt(0) // Usamos getInt(0) para leer sin mover la posición

            if (id != idSensorAEliminar) {

                // Si el ID NO coincide, copiamos el registro completo.
                // Rebobinamos el buffer (posición a 0, límite se mantiene)
                buffer.rewind()

                // El registro completo (40 bytes) se escribe en el archivo temporal.
                // La función escribirMedicion() que usa APPEND se encargará de esto.

                // --- Extracción y Escritura en temporal ---
                val nombreCompleto = ByteArray(TAMANO_NOMBRE)
                // Ahora sí leemos los datos, ya que estamos listos para pasar al siguiente registro.
                buffer.getInt() // Consumimos el ID
                buffer.get(nombreCompleto)
                val nombre = String(nombreCompleto, Charsets.UTF_8).trim()
                val temp = buffer.getDouble()
                val hum = buffer.getDouble()

                escribirMedicion(rutaTemp, id, nombre, temp, hum) // Escribimos en el nuevo fichero
                // ----------------------------------------

            } else {
                // Si el ID COINCIDE, la función simplemente NO llama a escribirMedicion().
                registroEliminado = true
                println("-> Eliminado registro con ID: $idSensorAEliminar.")
            }

            buffer.clear() // Dejar el buffer limpio para la siguiente carga del canal.
        }
    } // El canal de lectura se cierra automáticamente aquí.

    // 4. Finalización: Reemplazar el archivo original con el temporal si se encontró algo.
    if (registroEliminado) {
        Files.delete(ruta) // Borramos el fichero original (que aún tiene el registro a eliminar).
        Files.move(rutaTemp, ruta) // Renombramos el temporal (que NO tiene el registro).
        println("--- Eliminación completada. Archivo ${ruta.name} actualizado ---")
    } else {
        // Si no se encontró el ID, eliminamos el archivo temporal (vacío o incompleto) y mantenemos el original.
        Files.deleteIfExists(rutaTemp)
        println("--- Medición con ID: $idSensorAEliminar no encontrada. El archivo original no fue modificado. ---")
    }
}
