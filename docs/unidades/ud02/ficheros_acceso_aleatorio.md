# 6. Ficheros de acceso aleatorio

Un fichero de acceso aleatorio es un tipo de fichero que permite leer o escribir en cualquier posición del fichero directamente, sin necesidad de procesar secuencialmente todo el contenido previo. El sistema puede “saltar” a una posición concreta (medida en bytes desde el inicio del fichero) y comenzar la lectura o escritura desde ahí. Por ejemplo, si cada registro ocupa 200 bytes, para acceder al registro número 100 hay que saltar 200×99=19.800 bytes desde el inicio.

Las clases **FileChannel**, **ByteBuffer** y **StandardOpenOption** se utilizan juntas para leer y escribir en ficheros binarios y en el acceso aleatorio a ficheros. `ByteBuffer` se utiliza en ficheros de acceso aleatorio porque permite leer y escribir bloques binarios de datos en posiciones específicas del fichero.

## Métodos de FileChannel

| Método | Descripción |
| :--- | :--- |
| `position()` | Devuelve la posición actual del puntero en el fichero y permite saltar a cualquier posición en él (tanto para leer como para escribir). |
| `position(long)` | Establece una posición exacta para lectura/escritura. |
| `truncate(long)` | Recorta o amplía el tamaño del fichero. |
| `size()` | Devuelve el tamaño total actual del fichero. |
| `read(ByteBuffer)`, `write(ByteBuffer)` | Usa `FileChannel` para secuencial o aleatorio. |

## Métodos de ByteBuffer

| Método | Descripción |
| :--- | :--- |
| `allocate(capacidad)` | Crea un buffer con capacidad fija en memoria (no compartida). |
| `wrap(byteArray)` | Crea un buffer que envuelve un array de bytes existente (memoria compartida). |
| `wrap(byteArray, offset, length)` | Crea un buffer desde una porción del array existente. |
| `put(byte)`, `putInt(int)`, `putDouble(double)`, `putFloat(float)`, `putChar(char)`, `putShort(short)`, `putLong(long)` | Escribe un byte, int, double, float, char, short o long en la posición actual. |
| `put(byte[], offset, length)` | Escribe una porción de un array de bytes. |
| `get()`, `getInt()`, `getDouble()`, `getFloat()`, `getChar()`, `getShort()`, `getLong()` | Lee un byte, int, double, float, char, short o long desde la posición actual. |
| `get(byte[], offset, length)` | Lee una porción del buffer a un array. |

## Métodos de control del buffer

| Método | Descripción |
| :--- | :--- |
| `position()` | Devuelve la posición actual del cursor. |
| `position(int)` | Establece la posición del cursor. |
| `limit()` | Devuelve el límite del buffer. |
| `limit(int)` | Establece un nuevo límite. |
| `capacity()` | Devuelve la capacidad total del buffer. |
| `clear()` | Limpia el buffer: posición a 0, límite al máximo (sin borrar contenido). |
| `flip()` | Prepara el buffer para lectura después de escribir. |
| `rewind()` | Posición a 0 para releer desde el inicio. |
| `remaining` | Indica cuántos elementos quedan por procesar. |
| `hasRemaining()` | `true` si aún queda contenido por leer o escribir. |

**IMPORTANTE**: un fichero `.dat` no es un fichero de texto. No se puede abrir con el Bloc de Notas, TextEdit, o un editor de código en modo texto normal. Si se abre con estos programas se ve una mezcla de caracteres extraños, símbolos y espacios ("basura"). Hay herramientas online y plugins para los IDE para poder abrir los ficheros y ver la información en binario que contienen.

### Ejemplo

El siguiente ejemplo utiliza `FileChannel` y `ByteBuffer` para crear y leer un fichero llamado `mediciones.dat` con registros con la siguiente estructura:

* ID del sensor (`Int` - 4 bytes)
* temperatura (`Double` - 8 bytes)
* humedad (`Double` - 8 bytes)

A continuación se muestra el código con las funciones para añadir una medición al final del fichero y leer todas las mediciones que hay en él.

```kotlin
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.io.IOException

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

    // ** Aseguramos que el directorio exista antes de escribir **
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
        println ("El fichero ${ruta.fileName} no existe. No hay nada que leer.")
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
```

🔍 **Ejecuta el ejemplo anterior y comprueba que la salida es la siguiente:**

```bash
Medición (ID: 101) escrita correctamente.
Medición (ID: 102) escrita correctamente.
Medición (ID: 103) escrita correctamente.
Medición (ID: 104) escrita correctamente.

--- Leyendo todas las mediciones ---
  - ID: 101, Nombre: Atenea, Temperatura: 25.5 °C, Humedad: 60.2 %
  - ID: 102, Nombre: Hera, Temperatura: 26.1 °C, Humedad: 58.9 %
  - ID: 103, Nombre: Iris, Temperatura: 28.4 °C, Humedad: 65.9 %
  - ID: 104, Nombre: Selene, Temperatura: 28.4 °C, Humedad: 65.9 %
```

Ahora que ya tenemos la información guardada en nuestro fichero `.dat` y sabemos leerla, vamos a ampliar la aplicación con una función que recoge el ID del sensor a modificar y los nuevos datos de temperatura y humedad. Cuando localiza el registro del sensor cuyo ID coincide con el buscado, escribe los nuevos datos en las posiciones de los bytes correspondientes.

```kotlin
// --- FUNCIÓN 3: ACTUALIZAR UNA MEDICIÓN EXISTENTE ---
// Busca un registro por ID y sobrescribe solo los campos de Temperatura y Humedad.
fun actualizarMedicion(ruta: Path, idSensorBuscado: Int, nuevaTemperatura: Double, nuevaHumedad: Double) {
    if (!Files.exists(ruta)) {
        println ("Error: El fichero no existe, no se puede actualizar.")
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
```

La llamada a esta nueva función en el main podría ser:

```kotlin
actualizarMedicion(rutaFichero, 102, 21.0, 72.3)
```

Se vuelve a llamar a `leerMediciones` para comprobar que la información del sensor se ha modificado correctamente:

```kotlin
leerMediciones(rutaFichero)
```

🔍 **Realiza los siguientes pasos:**

* Añade el código de la función `actualizarMedicion()` al proyecto del ejemplo anterior.
* Comenta en el `main` las llamadas a la función `escribirMedicion()`.
* Añade al `main` las llamadas a `actualizarMedicion()` y a `leerMediciones()`.
* **Ejecuta la aplicación y comprueba que la salida es la siguiente:**

```bash
--- Leyendo todas las mediciones ---
  - ID: 101, Nombre: Atenea, Temperatura: 25.5 °C, Humedad: 60.2 %
  - ID: 102, Nombre: Hera, Temperatura: 26.1 °C, Humedad: 58.9 %
  - ID: 103, Nombre: Iris, Temperatura: 28.4 °C, Humedad: 65.9 %
  - ID: 104, Nombre: Selene, Temperatura: 28.4 °C, Humedad: 65.9 %

Intentando actualizar medición para ID: 102...
leyendo ID: 101
leyendo ID: 102
Sensor 102 en posición 40.
Medición actualizada con éxito a Temp: 21.0, Hum: 72.3.

--- Leyendo todas las mediciones ---
  - ID: 101, Nombre: Atenea, Temperatura: 25.5 °C, Humedad: 60.2 %
  - ID: 102, Nombre: Hera, Temperatura: 21.0 °C, Humedad: 72.3 %
  - ID: 103, Nombre: Iris, Temperatura: 28.4 °C, Humedad: 65.9 %
  - ID: 104, Nombre: Selene, Temperatura: 28.4 °C, Humedad: 65.9 %
```

Por último, ampliaremos la aplicación para poder eliminar los datos de un sensor a partir de su ID. El programa recorre todo los registros comprobando si el ID coincide con el buscado. En caso de que no coincida escribe el registro en un fichero temporal y si coincide, no hace nada. Al finalizar el fichero temporal contendrá los registros que no se quieren eliminar. Por último, se elimina el fichero original y se renombra el fichero temporal con el nombre original.

```kotlin
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
```

La llamada a esta nueva función en el main podría ser:

```kotlin
eliminarMedicion(rutaFichero, 102)
```

Se vuelve a llamar a `leerMediciones` para comprobar que la información del sensor se ha modificado correctamente:

```kotlin
leerMediciones(rutaFichero)
```

🔍 **Realiza los siguientes pasos:**

* Añade el código de la función `eliminarMedicion()` al ejemplo anterior.
* Comenta en el `main` la llamada a la función `actualizarMedicion()`.
* Añade al `main` la llamada a `eliminarMedicion()`.
* **Ejecuta la aplicación y comprueba que la salida es la siguiente:**

```bash
--- Leyendo todas las mediciones ---
  - ID: 101, Nombre: Atenea, Temperatura: 25.5 °C, Humedad: 60.2 %
  - ID: 102, Nombre: Hera, Temperatura: 21.0 °C, Humedad: 72.3 %
  - ID: 103, Nombre: Iris, Temperatura: 28.4 °C, Humedad: 65.9 %
  - ID: 104, Nombre: Selene, Temperatura: 28.4 °C, Humedad: 65.9 %

Intentando eliminar medición para el sensor con ID: 102...
Medición (ID: 101) escrita correctamente.
Medición (ID: 103) escrita correctamente.
Medición (ID: 104) escrita correctamente.

--- Leyendo todas las mediciones ---
  - ID: 101, Nombre: Atenea, Temperatura: 25.5 °C, Humedad: 60.2 %
  - ID: 103, Nombre: Iris, Temperatura: 28.4 °C, Humedad: 65.9 %
  - ID: 104, Nombre: Selene, Temperatura: 28.4 °C, Humedad: 65.9 %
```

---

## 🎯 Práctica 4: Modificar y eliminar registros en ficheros .dat

Realiza lo siguiente:

* **Siguiendo con tu Proyecto anterior**: Crea una nueva estructura para registrar los datos que has definido pero en modo **Fichero de Acceso Aleatorio**.
* **Crea las funciones** `escribirMediciones()`y `leerMediciones()`. Adaptlo a tus datos.
* **Crea la función `modificarReg()`**: Pedirá al usuario el ID del registro a modificar y buscará ese registro en el fichero. Si lo encuentra, pedirá los nuevos datos. Utilizará acceso aleatorio (`FileChannel.position()`) para saltar a la posición exacta de ese registro y sobrescribir únicamente los campos modificados, sin alterar el resto del fichero.
* **Crea la función `eliminarReg()`**: Debe recibir un ID y eliminar el registro correspondiente. Implementa la técnica de streaming (leer el fichero original registro a registro, escribir los que se conservan en un fichero temporal, borrar el original y renombrar el temporal).
* **Comprueba**: Prueba estas funciones desde `main`, llamando a `leerMediciones()` antes y después de cada operación para verificar los resultados.