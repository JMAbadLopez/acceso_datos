## 6. Ficheros de acceso aleatorio
Un fichero de acceso aleatorio es un tipo de fichero que permite leer o escribir en cualquier posición del fichero directamente, sin necesidad de procesar secuencialmente todo el contenido previo. El sistema puede “saltar” a una posición concreta (medida en bytes desde el inicio del fichero) y comenzar la lectura o escritura desde ahí. Por ejemplo, si cada registro ocupa 200 bytes, para acceder al registro número 100 hay que saltar 200×99=19.800 bytes desde el inicio.

Las clases **FileChannel**, **ByteBuffer** y **StandardOpenOption** se utilizan juntas para leer y escribir en ficheros binarios y en el acceso aleatorio a ficheros. `ByteBuffer` se utiliza en ficheros de acceso aleatorio porque permite leer y escribir bloques binarios de datos en posiciones específicas del fichero.

### Métodos de FileChannel
| Método | Descripción |
| :--- | :--- |
| `position()` | Devuelve la posición actual del puntero en el fichero y permite saltar a cualquier posición en él (tanto para leer como para escribir). |
| `position(long)` | Establece una posición exacta para lectura/escritura. |
| `truncate(long)` | Recorta o amplía el tamaño del fichero. |
| `size()` | Devuelve el tamaño total actual del fichero. |
| `read(ByteBuffer)`, `write(ByteBuffer)` | Usa `FileChannel` para secuencial o aleatorio. |

### Métodos de ByteBuffer
| Método | Descripción |
| :--- | :--- |
| `allocate(capacidad)` | Crea un buffer con capacidad fija en memoria (no compartida). |
| `wrap(byteArray)` | Crea un buffer que envuelve un array de bytes existente (memoria compartida). |
| `wrap(byteArray, offset, length)` | Crea un buffer desde una porción del array existente. |
| `put(byte)`, `putInt(int)`, `putDouble(double)`, `putFloat(float)`, `putChar(char)`, `putShort(short)`, `putLong(long)` | Escribe un byte, int, double, float, char, short o long en la posición actual. |
| `put(byte[], offset, length)` | Escribe una porción de un array de bytes. |
| `get()`, `getInt()`, `getDouble()`, `getFloat()`, `getChar()`, `getShort()`, `getLong()` | Lee un byte, int, double, float, char, short o long desde la posición actual. |
| `get(byte[], offset, length)` | Lee una porción del buffer a un array. |

### Métodos de control del buffer
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

### Ejemplo:
El siguiente ejemplo utiliza `FileChannel` y `ByteBuffer` para crear y leer un fichero llamado `mediciones.dat` con registros con la siguiente estructura:

* ID del sensor (`Int` - 4 bytes)
* temperatura (`Double` - 8 bytes)
* humedad (`Double` - 8 bytes)

A continuación se muestra el código con las funciones para añadir una medición al final del fichero y leer todas las mediciones que hay en él.
```kotlin
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
    val rutaFichero = Path.of("mediciones.dat")
    escribirMedicion(rutaFichero, 101, "Atenea",25.5, 60.2)
    escribirMedicion(rutaFichero, 102, "Hera",26.1, 58.9)
    escribirMedicion(rutaFichero, 103, "Iris",28.4, 65.9)
    escribirMedicion(rutaFichero, 104, "Selene",28.4, 65.9)
    leerMediciones(rutaFichero) //leer todas las mediciones
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
```
🔍 **Ejecuta el ejemplo anterior y comprueba que la salida es la siguiente:**
```
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
```
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
fun eliminarMedicion(ruta: Path, idSensorAEliminar: Int) {
    val rutaTemp = Path.of("temp.dat")
    if (!Files.exists(ruta)) {
        println ("Error: El fichero no existe, no se puede actualizar.")
    } else {
        println ("\nIntentando eliminar medición para el sensor con ID: $idSensorAEliminar...")
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
                if (id!=idSensorAEliminar) {
                    // Usar nuestra función par escribir en el fichero temporal
                    escribirMedicion(rutaTemp, id, nombre, temp, hum)
                }
                buffer.clear()
            }
        }
        Files.delete(ruta) //borrar fichero original
        Files.move(rutaTemp, ruta) // renombrar temporal
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
```
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

### 🎯 Práctica 5: Modificar y eliminar registros en ficheros .dat
Realiza lo siguiente:
* **Crea la función `modificarReg()`**: Pedirá al usuario el ID del registro a modificar y buscará ese registro en el fichero. Si lo encuentra, pedirá los nuevos datos. Utilizará acceso aleatorio (`FileChannel.position()`) para saltar a la posición exacta de ese registro y sobrescribir únicamente los campos modificados, sin alterar el resto del fichero.
* **Crea la función `eliminarReg()`**: Debe recibir un ID y eliminar el registro correspondiente. Implementa la técnica de streaming (leer el fichero original registro a registro, escribir los que se conservan en un fichero temporal, borrar el original y renombrar el temporal).
* **Comprueba**: Prueba estas funciones desde `main`, llamando a `mostrarTodo()` antes y después de cada operación para verificar los resultados.

---