## 6. Ficheros de acceso aleatorio

A diferencia del acceso secuencial, el **acceso aleatorio** nos permite situarnos (*saltar*) de forma instantánea a cualquier posición física del fichero para leer o modificar un fragmento de datos específico, sin necesidad de procesar todo lo que hay antes. Para poder utilizar esta técnica, nuestros registros en el fichero binario deben tener un **tamaño fijo en bytes**.

> Por ejemplo, si cada registro de nuestra colección botánica ocupa exactamente 32 bytes, para acceder al registro número 100 no tenemos que leer los 99 anteriores; podemos saltar directamente a la **posición de inicio** del registro número 100 calculándola: **Posición = 32 bytes × (100−1) = 3168 bytes**

Para que este cálculo matemático se cumpla con total precisión, el tamaño en bytes de los campos de texto debe cuplir siempre que 1 carácter = 1 byte y, dependiendo del charset que se utilice, esto puede no cumplirse. Por ejemplo, en un charset variable como UTF-8, caracteres como la 'ñ' o las tildes ocuparán más de un byte.

A continuación se muestra una tabla comparativa con las características de algunas codificaciones:

| Característica | `Charset.defaultCharset()` | `Charsets.US_ASCII` | `Charsets.ISO_8859_1` (Latin-1) |
| :--- | :--- | :--- | :--- |
| **Descripción** | La codificación predeterminada de la Máquina Virtual de Java (JVM). | El estándar clásico americano de 7 bits. | Extensión de 8 bits para idiomas de Europa Occidental. |
| **Tamaño en bytes por carácter** | **Variable** (generalmente entre 1 y 4 bytes si la máquina usa UTF-8). | **Estricto: 1 byte** por carácter. | **Estricto: 1 byte** por carácter. |
| **Caracteres soportados** | Depende de la versión de Java:<br>- **Java 18+:** UTF-8 por defecto (soporta casi todo: tildes, emojis, caracteres asiáticos, etc.).<br>- **Java 17 o anterior:** Varía según el S.O. (Windows-1252 en Windows, UTF-8 en macOS/Linux). | Extremadamente limitado. Solo alfabeto inglés básico (A-Z, a-z), números (0-9) y signos estándar. **No soporta tildes ni la "ñ"**. | Alfabeto inglés, caracteres de Europa occidental, tildes (á, é, í...), diéresis y la **"ñ"**. *(Nota: No soporta el símbolo del Euro `€`)*. |
| **Portabilidad** | **Baja** (en Java 17 o inferior, un archivo creado en Windows puede leerse mal en Linux). **Alta** en Java 18+. | **Total**. Es el estándar base universal. | **Alta** en entornos occidentales. |
| **Uso ideal en programación** | Lectura/escritura rápida de archivos de texto locales para el usuario. | Protocolos de red muy básicos, comandos de consola ingleses y optimización extrema. | Estructuras de datos binarias con **registros de tamaño fijo** que requieran soporte para el idioma español (tildes, eñes). |

> En nuestros ejemplos utilizaremos `ISO_8859_1` para asegurarnos que **1 carácter sea siempre estrictamente igual a 1 byte** en el archivo binario, permitiendo de forma segura guardar tildes y eñes sin romper la matemática de los desplazamientos de bytes de tu acceso aleatorio (`canal.position()`).

Para el acceso aleatorio en la API moderna de Java/Kotlin (`java.nio`), trabajamos con tres herramientas en equipo:

1. **`FileChannel`**: Funciona como un "canal o autopista de datos" bidireccional hacia el fichero en el disco. Es el que nos permite modificar la posición del puntero del fichero en tiempo de ejecución mediante `canal.position(long)`.
2. **`ByteBuffer`**: Es un contenedor en la memoria RAM que empaqueta y prepara exactamente los bytes que queremos transferir (escribir) o recibir (leer) a través del canal (`FileChannel`).
3. **`StandardOpenOption`**: Es un enumerado que funciona como el "semáforo de permisos" del canal. Le indica a `FileChannel` cómo debe abrirse el fichero (por ejemplo, si se abre solo para lectura `READ`, para escritura `WRITE`, si debe crear el fichero si no existe `CREATE` o si debe añadir los datos al final `APPEND`). Sin estas opciones de configuración, el canal no sabrá qué operaciones tiene permitido realizar sobre el disco.

A continuación se describen algunos de los métodos que utilizaremos:

**Métodos de `FileChannel`**

| Método | Descripción |
| :--- | :--- |
| `position()` | Devuelve la posición actual del puntero en el fichero (medida en bytes). |
| `position(long)` | Establece una posición exacta en bytes para la próxima lectura o escritura. |
| `truncate(long)` | Recorta o amplía el tamaño del fichero a los bytes indicados. |
| `size()` | Devuelve el tamaño total actual del fichero en bytes. |
| `read(ByteBuffer)` | Lee una secuencia de bytes del canal y los guarda en el buffer proporcionado. |
| `write(ByteBuffer)` | Escribe una secuencia de bytes desde el buffer indicado hacia el canal. |

**Métodos de `ByteBuffer`**

| Método | Descripción |
| :--- | :--- |
| `allocate(capacidad)` | Crea un nuevo buffer con una capacidad fija de bytes en memoria. |
| `wrap(byteArray)` | Crea un buffer que envuelve un array de bytes ya existente (comparten la misma memoria). |
| `put(byte)` | Escribe un byte en la posición actual del buffer. |
| `putInt(int)` | Escribe un valor entero (4 bytes). |
| `putDouble(double)` | Escribe un valor double (8 bytes). |
| `putFloat(float)` | Escribe un valor float (4 bytes). |
| `putChar(char)` | Escribe un carácter (2 bytes). |
| `putLong(long)` | Escribe un valor long (8 bytes). |
| `get()` | Lee un byte desde la posición actual del cursor. |
| `getInt()` | Lee un valor entero (4 bytes). |
| `getDouble()` | Lee un valor double (8 bytes). |
| `get(byteArray)` | Extrae bytes del buffer y los vuelca en un array de bytes de destino. |

**Métodos de control del buffer (`ByteBuffer`)**

| Método | Descripción |
| :--- | :--- |
| `position()` | Devuelve la posición actual del cursor de lectura/escritura dentro del buffer. |
| `position(int)` | Establece la posición del cursor dentro del buffer. |
| `limit()` | Devuelve el límite actual del buffer (hasta dónde se puede leer/escribir). |
| `clear()` | Limpia el buffer: resetea la posición a 0 y pone el límite al máximo (no borra los datos físicos de la memoria). |
| `flip()` | Prepara el buffer para ser leído después de haber escrito en él (establece el límite en la posición actual y devuelve el cursor a 0). |
| `rewind()` | Devuelve la posición a 0 para poder releer el buffer desde el inicio. |
| `hasRemaining()` | Devuelve `true` si aún quedan elementos por procesar entre la posición actual y el límite. |

#### Ejemplo 12: Lectura y escritura en ficheros binarios de tamaño fijo

En este ejemplo utilizaremos `FileChannel` y `ByteBuffer` para crear un fichero binario estructurado para nuestro herbario. Cada registro representará una planta con tres campos y ocupará exactamente **32 bytes** en total:

| Campo | Tipo | Tamaño fijo | Rango de bytes en el registro |
| :--------------- | :--- | :--- | :--- |
| `id_planta` | `Int` | 4 bytes | 0 – 3 |
| `nombre_comun` | `String` | 20 bytes (longitud fija) | 4 – 23 |
| `precio` | `Double` | 8 bytes | 24 – 31 |

```kotlin
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

data class PlantaBinaria(
    val idPlanta: Int,
    val nombreComun: String,
    val precio: Double
)

// Definimos los tamaños del registro binario
const val TAMANO_ID = Int.SIZE_BYTES // 4 bytes
const val TAMANO_NOMBRE = 20         // 20 bytes para la cadena de texto
const val TAMANO_PRECIO = Double.SIZE_BYTES // 8 bytes
const val TAMANO_REGISTRO = TAMANO_ID + TAMANO_NOMBRE + TAMANO_PRECIO // 32 bytes en total

val archivoPath = Path.of("datos","plantas.bin")


fun main() {
    crearHerbario()
    mostrarInfo()
}


fun crearHerbario(){
    Files.createDirectories(archivoPath.parent)

    val listaSemillas = listOf(
        PlantaBinaria(1, "Rosa", 1.5),
        PlantaBinaria(2, "Girasol", 3.0),
        PlantaBinaria(3, "Margarita", 0.6)
    )

    vaciarCrearFichero()

    for (planta in listaSemillas) {
        anadirPlanta(planta.idPlanta, planta.nombreComun, planta.precio)
    }
}


// Crea el archivo o lo vacía si ya existía
fun vaciarCrearFichero() {
    try {
        FileChannel.open(
            archivoPath,
            StandardOpenOption.WRITE,
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING
        ).close()
        println("--- El fichero '${archivoPath.fileName}' se ha creado y está vacío.")
    } catch (e: Exception) {
        println("Error al vaciar o crear el fichero: ${e.message}")
    }
}

// Añade un registro de planta al final del fichero
fun anadirPlanta( idPlanta: Int, nombre: String, precio: Double) {
    val nuevaPlanta = PlantaBinaria(idPlanta, nombre, precio)

    try {
        FileChannel.open(
            archivoPath,
            StandardOpenOption.WRITE,
            StandardOpenOption.CREATE,
            StandardOpenOption.APPEND
        ).use { canal ->
            val buffer = ByteBuffer.allocate(TAMANO_REGISTRO)

            // 1. Escribimos el ID (4 bytes)
            buffer.putInt(nuevaPlanta.idPlanta)

            // 2. Escribimos el Nombre (20 bytes). Rellenamos con espacios si es más corto.
            val nombreBytes = nuevaPlanta.nombreComun
                .padEnd(TAMANO_NOMBRE, ' ')
                .toByteArray(Charsets.ISO_8859_1)
            buffer.put(nombreBytes, 0, TAMANO_NOMBRE)

            // 3. Escribimos precio (8 bytes)
            buffer.putDouble(nuevaPlanta.precio)

            // Preparamos el buffer para volcar la información al canal
            buffer.flip()
            while (buffer.hasRemaining()) {
                canal.write(buffer)
            }
            println("- Planta '${nuevaPlanta.nombreComun.trim()}' añadida correctamente.")
        }
    } catch (e: Exception) {
        println("Error al añadir la planta: ${e.message}")
    }
}

// Lee todos los registros de manera secuencial de inicio a fin
fun leerPlantas(): List<PlantaBinaria> {
    val plantas = mutableListOf<PlantaBinaria>()

    if (!Files.isReadable(archivoPath)) return emptyList()

    FileChannel.open(archivoPath, StandardOpenOption.READ).use { canal ->
        val buffer = ByteBuffer.allocate(TAMANO_REGISTRO)

        // Cada lectura llena exactamente un registro de 32 bytes
        while (canal.read(buffer) > 0) {
            buffer.flip()

            // 1. Leemos el ID
            val id = buffer.getInt()

            // 2. Leemos los bytes del nombre y los decodificamos limpiando los espacios sobrantes
            val nombreBytes = ByteArray(TAMANO_NOMBRE)
            buffer.get(nombreBytes)
            val nombre = String(nombreBytes, Charsets.ISO_8859_1).trim()

            // 3. Leemos precio
            val precio = buffer.getDouble()

            plantas.add(PlantaBinaria(id, nombre, precio))
            buffer.clear()
        }
    }
    return plantas
}

fun mostrarInfo() {
    // Mostramos la información
    println("\n--- Plantas leídas secuencialmente del fichero .bin: ---")
    val leidas = leerPlantas()
    for (p in leidas) {
        println(" - ID: ${p.idPlanta}, Nombre común: ${p.nombreComun}, ${p.precio}€")
    }
}
```

!!! success "Prueba y analiza el ejemplo"
    Prueba el código de ejemplo y verifica que la salida por consola es:

    ```text
    --- El fichero 'plantas.bin' se ha creado y está vacío.
    - Planta 'Rosa' añadida correctamente.
    - Planta 'Girasol' añadida correctamente.
    - Planta 'Margarita' añadida correctamente.

    --- Plantas leídas secuencialmente del fichero .bin: ---
    - ID: 1, Nombre común: Rosa, 1.5€
    - ID: 2, Nombre común: Girasol, 3.0€
    - ID: 3, Nombre común: Margarita, 0.6€
    ```

**Representación Hexadecimal en Disco**

Si abrimos el fichero resultante `plantas.bin` utilizando un visor hexadecimal (como [HexEd.it](https://hexed.it/)), observaremos los registros consecutivos de 32 bytes representados de la siguiente forma:

```text
Offset    00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F   ASCII
-------------------------------------------------------------------------
00000000  00 00 00 01 52 6F 73 61 20 20 20 20 20 20 20 20   ....Rosa        
00000010  20 20 20 20 20 20 3F F8 00 00 00 00 00 00 00 00   ......?.........
00000020  00 00 00 02 47 69 72 61 73 6F 6C 20 20 20 20 20   ....Girasol     
00000030  20 20 20 20 20 20 40 08 00 00 00 00 00 00 00 00   ......@.........
```

- **ID (1):** Representado en los primeros 4 bytes `00 00 00 01`.
- **Nombre ("Rosa"):** Bytes en ASCII `52 6F 73 61`, seguidos de espacios `20` hasta completar los 20 bytes fijos.
- **Precio (1.5):** Representado en formato de doble precisión IEEE 754 ocupando los bytes `3F F8 00 00 00 00 00 00`.

!!! example "Autoevaluación"

    **Pregunta 19: En la función `anadirPlanta` del Ejemplo 12, se preparan los datos de una planta en un `ByteBuffer` de la siguiente manera:**
    
    ```kotlin
    val buffer = ByteBuffer.allocate(TAMANO_REGISTRO)
    
    buffer.putInt(nuevaPlanta.idPlanta)
    buffer.put(nombreBytes, 0, TAMANO_NOMBRE)
    buffer.putDouble(nuevaPlanta.precio)
    
    // Se omite intencionadamente la llamada a: buffer.flip()
    
    while (buffer.hasRemaining()) {
        canal.write(buffer)
    }
    ```
    
    **Si se omite la llamada al método `buffer.flip()` antes de intentar escribir en el canal (`canal.write(buffer)`), ¿cuál será el comportamiento del programa?**
    
    A) El canal detectará automáticamente que el búfer contiene datos nuevos y realizará la escritura física en el archivo de forma normal.
    
    B) Se producirá un error de compilación inmediato porque la función `canal.write` exige sintácticamente que el búfer haya sido "volteado" previamente.
    
    C) No se escribirá ningún dato en el archivo, ya que el puntero de posición del búfer se encuentra al final de los datos introducidos (posición 32 de 32), haciendo que `buffer.hasRemaining()` devuelva `false` y se salte el bucle de escritura.
    
    D) El archivo binario se creará pero se llenará únicamente con bytes de valor cero al forzar la escritura sin haber reseteado el límite máximo.
    
    
    ??? quote "Solución"
    
        ❌ A) El canal de datos de Java NIO no realiza ninguna gestión automática de los punteros internos del búfer; depende por completo del estado en el que se le entregue el objeto `ByteBuffer`.
        
        ❌ B) El compilador de Kotlin no analiza el estado de los punteros del búfer, por lo que compilará el código perfectamente sin mostrar ningún error.
        
        ✅ C) Al introducir datos en el búfer con los métodos `put...`, el cursor de posición se desplaza hacia adelante hasta llegar al final del registro (byte 32). El método `flip()` es fundamental porque "voltea" el búfer: baja la posición a 0 y define el límite de lectura en el byte 32. Si se omite, la posición sigue estando al final, por lo que el búfer considera que no queda nada por procesar (`hasRemaining()` es falso) y el bucle de escritura no llega a ejecutarse, dejando el archivo vacío.
        
        ❌ D) El programa no escribirá bytes a cero ni basura en el archivo; simplemente ignorará la escritura al no cumplirse la condición del bucle `while`.
    
    
    **Pregunta 20: En el diseño de registros binarios de tamaño fijo, se aplica el siguiente proceso de relleno al nombre de la planta antes de escribirlo en el búfer:**
    
    ```kotlin
    val nombreBytes = nuevaPlanta.nombreComun
        .padEnd(TAMANO_NOMBRE, ' ')
        .toByteArray()
    ```
    
    **¿Cuál es la razón práctica para rellenar con espacios en blanco (mediante `.padEnd`) el campo de texto del nombre común de la planta antes de guardarlo en el archivo?**
    
    A) Garantizar que el campo ocupe exactamente los 20 bytes reservados en la estructura del registro, permitiendo mantener la longitud fija total de 32 bytes por cada planta y facilitar cálculos matemáticos para saltar a registros específicos en accesos aleatorios futuros.
    
    B) Evitar que el codificador de caracteres de la máquina virtual de Java lance una excepción de desbordamiento de memoria al encontrarse con nombres excesivamente cortos.
    
    C) Convertir de forma automática los caracteres especiales del idioma español (como tildes o la letra ñ) a caracteres del formato estándar ASCII para que ocupen un solo byte.
    
    D) Encriptar el nombre común de la planta para que ningún visor hexadecimal pueda leer la cadena de texto real en el archivo final.
    
    
    ??? quote "Solución"
    
        ✅ A) En los archivos de acceso aleatorio, cada registro debe medir exactamente lo mismo (en este caso, 32 bytes). Si un nombre es más corto de 20 caracteres (como "Rosa") y no se rellena, el registro mediría menos de 32 bytes, lo que rompería la estructura del archivo e impediría calcular matemáticamente la posición exacta de las siguientes plantas (por ejemplo, buscar la planta número 100 multiplicando `99 * 32 bytes`).
        
        ❌ B) La longitud de las cadenas de texto no genera excepciones de falta de memoria en la máquina virtual por ser cortas; el sistema puede manejar cualquier longitud de texto de forma nativa.
        
        ❌ C) El método `.padEnd` se limita a añadir caracteres de espacio en blanco al final de la cadena de texto, pero no realiza ninguna traducción ni filtrado de caracteres especiales o codificaciones.
        
        ❌ D) El relleno con espacios en blanco no oculta ni encripta la información; cualquier visor hexadecimal mostrará el nombre de la planta seguido de los bytes correspondientes a los espacios (valor hexadecimal `20`).
    

#### Ejemplo 13: Modificar el campo de un registro mediante acceso aleatorio

Ahora aprovecharemos la capacidad de `FileChannel` para posicionarnos directamente sobre una propiedad de un registro concreto utilizando el ID, para actualizarla sin alterar ni leer de forma secuencial el resto del fichero.

```kotlin
fun modificarPrecioPlanta(idPlanta: Int, nuevoPrecio: Double) {
    try {
        // Abrimos el canal con permisos de Lectura y Escritura
        FileChannel.open(archivoPath, StandardOpenOption.READ, StandardOpenOption.WRITE).use { canal ->
            val buffer = ByteBuffer.allocate(TAMANO_REGISTRO)
            var encontrado = false

            while (canal.read(buffer) > 0 && !encontrado) {
                // Al finalizar la lectura de un registro completo, guardamos el puntero actual
                val posicionActual = canal.position()
                buffer.flip()

                val id = buffer.getInt()
                if (id == idPlanta) {
                    encontrado = true
                    // Calculamos la posición del campo precio en bytes dentro del fichero

                    // Calcular el inicio del registro actual
                    val inicioRegistro = posicionActual - TAMANO_REGISTRO
                    // Calcular los bytes que ocupan los campos anteriores (desplazamiento)
                    val desplazamientoPrecio = TAMANO_ID + TAMANO_NOMBRE
                    // "rebobinar" al inicio del registro actual y avanzar el desplazamiento
                    val posicionPrecio = inicioRegistro + desplazamientoPrecio

                    // Nos situamos en el canal exactamente sobre el campo precio
                    canal.position(posicionPrecio)

                    val bufferPrecio = ByteBuffer.allocate(TAMANO_PRECIO)
                    bufferPrecio.putDouble(nuevoPrecio)
                    bufferPrecio.flip()

                    while (bufferPrecio.hasRemaining()) {
                        canal.write(bufferPrecio)
                    }
                }
                buffer.clear()
            }

            if (encontrado) {
                println("\n--- Precio de la planta con ID $idPlanta modificado correctamente a ${nuevoPrecio}€")
            } else {
                println("No se encontró ninguna planta con el ID: $idPlanta")
            }
        }
    } catch (e: Exception) {
        println("Error al modificar el registro: ${e.message}")
    }
}
```

Añadimos a la función `main` las líneas para llamar a la nueva función y volver a mostrar la información después de modificarla:

```kotlin
    modificarPrecioPlanta(2, 5.5)
    mostrarInfo()
```

!!! success "Prueba y analiza el ejemplo"
    Prueba el código de ejemplo y verifica que la salida por consola es:

    ```text
    --- El fichero 'plantas.bin' se ha creado y está vacío.
    - Planta 'Rosa' añadida correctamente.
    - Planta 'Girasol' añadida correctamente.
    - Planta 'Margarita' añadida correctamente.

    --- Plantas leídas secuencialmente del fichero .bin: ---
    - ID: 1, Nombre común: Rosa, 1.5€
    - ID: 2, Nombre común: Girasol, 3.0€
    - ID: 3, Nombre común: Margarita, 0.6€

    --- Precio de la planta con ID 2 modificado correctamente a 5.5€

    --- Plantas leídas secuencialmente del fichero .bin: ---
    - ID: 1, Nombre común: Rosa, 1.5€
    - ID: 2, Nombre común: Girasol, 5.5€
    - ID: 3, Nombre común: Margarita, 0.6€
    ```

!!! example "Autoevaluación"

    **Pregunta 21: En la función `modificarPrecioPlanta`, una vez localizado el registro con el ID buscado, se calcula la posición física en bytes del campo del precio (`posicionPrecio`) mediante la siguiente fórmula:**
    
    ```kotlin
    val posicionPrecio = posicionActual - TAMANO_REGISTRO + TAMANO_ID + TAMANO_NOMBRE
    ```
    
    **¿Cuál es la explicación lógica detrás de esta operación matemática para situar correctamente el puntero del canal de datos?**
    
    A) Se realiza para vaciar los datos del búfer de la memoria RAM y notificar al sistema operativo que el archivo va a incrementar su tamaño físico en el disco duro.
    
    B) Como la lectura completa del registro avanza el puntero hasta el final del mismo, se resta el tamaño del registro para retroceder al inicio de este, y se suman los tamaños del ID y del Nombre para saltar sobre ellos y situarse exactamente al principio del campo precio.
    
    C) Es un cálculo arbitrario exigido por la sintaxis de Kotlin para evitar que la máquina virtual de Java genere un error de desbordamiento de enteros durante la modificación del archivo.
    
    D) Sirve para avanzar el puntero del canal directamente hasta el final del archivo binario y añadir el nuevo valor del precio de forma secuencial.
    
    
    ??? quote "Solución"
    
        ❌ A) La operación matemática trabaja exclusivamente con índices de posiciones en bytes; no tiene relación con la gestión de la memoria RAM ni modifica el tamaño del archivo en disco.
        
        ✅ B) Al terminar de leer un registro de 32 bytes (`canal.read(buffer)`), el puntero del canal se queda posicionado justo al final de dicho registro (`posicionActual`). Para modificar el precio de esa planta concreta sin tocar el resto, se debe retroceder al principio del registro (`posicionActual - TAMANO_REGISTRO`). Desde ahí, para llegar al campo precio, se deben ignorar los bytes correspondientes al ID (4 bytes) y al Nombre (20 bytes), de ahí que se sumen ambas constantes (`+ TAMANO_ID + TAMANO_NOMBRE`).
        
        ❌ C) El compilador de Kotlin no exige ninguna fórmula específica para modificar archivos; se trata de una lógica puramente matemática diseñada por el desarrollador para navegar por la estructura de bytes fijos.
        
        ❌ D) El objetivo del acceso aleatorio es precisamente lo contrario: no escribir al final del archivo de manera secuencial, sino posicionarse y reescribir un campo específico en medio del archivo sin alterar el resto de la información.
    


    **Pregunta 22: Para abrir el canal que permite modificar el precio de una planta en el archivo binario mediante acceso aleatorio, se utiliza la siguiente instrucción:**
    
    ```kotlin
    FileChannel.open(archivoPath, StandardOpenOption.READ, StandardOpenOption.WRITE)
    ```
    
    **¿Qué ocurriría si se añadiese accidentalmente la opción `StandardOpenOption.TRUNCATE_EXISTING` dentro de los argumentos de configuración de apertura de este canal?**
    
    A) El canal funcionaría de manera normal, pero la escritura del nuevo dato se realizaría de forma más eficiente al optimizarse el almacenamiento en disco.
    
    B) Se producirá un error de compilación inmediato porque la clase `FileChannel` no admite la opción de truncado de archivos.
    
    C) El archivo binario se vaciaría por completo (quedando con un tamaño de 0 bytes) en el instante exacto de abrir el canal, perdiéndose de forma irreversible toda la información guardada en él antes de poder realizar la búsqueda del ID.
    
    D) El sistema de archivos del sistema operativo bloquearía el archivo impidiendo que el canal realice operaciones de lectura y provocando una excepción de acceso denegado.
    
    
    ??? quote "Solución"
    
        ❌ A) El truncado de un archivo no es una técnica de optimización de velocidad; consiste en la eliminación física de todos los datos que contiene el archivo.
        
        ❌ B) El código compilaría perfectamente, ya que `StandardOpenOption.TRUNCATE_EXISTING` es una opción de configuración totalmente válida y soportada por la API de canales de Java/Kotlin.
        
        ✅ C) La opción `TRUNCATE_EXISTING` indica al canal que, si el archivo ya existe, debe vaciar su contenido por completo (reducir su tamaño a 0 bytes) al abrirse. Al intentar buscar el ID del registro que se desea modificar en las líneas siguientes, el programa se encontrará con un archivo vacío, haciendo que la búsqueda falle y perdiendo toda la información del herbario de manera accidental.
        
        ❌ D) El programa no fallará por un bloqueo de seguridad del sistema operativo, sino por un error lógico de diseño del flujo de datos al haber eliminado voluntariamente la información con la opción de truncado.
    

#### Ejemplo 14: Eliminación de un registro binario

Para eliminar un registro de un fichero binario estructurado secuencial, la técnica estándar consiste en leer el fichero de inicio a fin escribiendo en un fichero temporal `.tmp` únicamente aquellos registros que **no coincidan** con el ID a eliminar. Al terminar, borramos el original y sustituimos el fichero original por el temporal.

Esta técnica se utiliza porque eliminar físicamente un registro del centro de un fichero binario obligaría a desplazar todos los bytes posteriores y eso sería muy costoso.

Para poder sustituir el fichero original por el temporal añadimos un import a nuestro código:

```kotlin
import java.nio.file.StandardCopyOption
```

El código de la función de eliminación es el siguiente:

```kotlin
fun eliminarPlanta(idPlanta: Int) {
    val pathTemporal = Path.of(archivoPath.toString() + ".tmp")
    var plantaEncontrada = false

    try {
        FileChannel.open(archivoPath, StandardOpenOption.READ).use { canalLectura ->
            FileChannel.open(
                pathTemporal,
                StandardOpenOption.WRITE,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
            ).use { canalEscritura ->
                val buffer = ByteBuffer.allocate(TAMANO_REGISTRO)

                // Cada lectura llena exactamente un registro de 32 bytes
                while (canalLectura.read(buffer) > 0) {
                    buffer.flip()
                    val id = buffer.getInt()

                    if (id == idPlanta) {
                        plantaEncontrada = true
                        // Si coincide con el ID a eliminar, lo ignoramos (no se escribe en el temporal)
                    } else {
                        // Rebobinamos el puntero del buffer para escribir el registro completo original
                        buffer.rewind()
                        canalEscritura.write(buffer)
                    }
                    buffer.clear()
                }
            }
        }

        if (plantaEncontrada) {
            // Reemplazamos el fichero original por el limpio temporal
            Files.move(pathTemporal, archivoPath, StandardCopyOption.REPLACE_EXISTING)
            println("\n**** Planta con ID $idPlanta eliminada con éxito.")
        } else {
            Files.deleteIfExists(pathTemporal)
            println("No se encontró la planta con ID: $idPlanta")
        }
    } catch (e: Exception) {
        println("Error durante la eliminación: ${e.message}")
    }
}
```

Añadimos a la función `main` las líneas para llamar a la nueva función y volver a mostrar la información después de eliminar la planta:

```kotlin
    eliminarPlanta(3)
    mostrarInfo()
```

!!! success "Prueba y analiza el ejemplo"
    Prueba el código de ejemplo y verifica que la salida por consola es:

    ```text
    --- El fichero 'plantas.bin' se ha creado y está vacío.
    - Planta 'Rosa' añadida correctamente.
    - Planta 'Girasol' añadida correctamente.
    - Planta 'Margarita' añadida correctamente.

    --- Plantas leídas secuencialmente del fichero .bin: ---
    - ID: 1, Nombre común: Rosa, 1.5€
    - ID: 2, Nombre común: Girasol, 3.0€
    - ID: 3, Nombre común: Margarita, 0.6€

    --- Precio de la planta con ID 2 modificada correctamente a 5.5€

    --- Plantas leídas secuencialmente del fichero .bin: ---
    - ID: 1, Nombre común: Rosa, 1.5€
    - ID: 2, Nombre común: Girasol, 5.5€
    - ID: 3, Nombre común: Margarita, 0.6€

    **** Planta con ID 3 eliminada con éxito.
    --- Plantas leídas secuencialmente del fichero .bin: ---
    - ID: 1, Nombre común: Rosa, 1.5€
    - ID: 2, Nombre común: Girasol, 5.5€
    ```

!!! example "Autoevaluación"

    **Pregunta 23: En la función `eliminarPlanta`, al procesar un registro que no coincide con el ID que se desea borrar, se ejecuta el siguiente bloque de código antes de guardarlo en el archivo temporal:**
    
    ```kotlin
    } else {
        // Rebobinamos el puntero del buffer para escribir el registro completo original
        buffer.rewind()
        canalEscritura.write(buffer)
    }
    ```
    
    **¿Qué problema de corrupción de datos ocurriría en el archivo temporal si se omitiera la llamada al método `buffer.rewind()` antes de realizar la escritura?**
    
    A) Se omitirían los primeros 4 bytes del registro (el campo del ID) al escribir en el canal temporal, guardando un registro incompleto de 28 bytes y corrompiendo la estructura del archivo, debido a que el puntero del búfer se quedó desplazado tras haber leído el entero con `buffer.getInt()`.
    
    B) El canal escribiría el registro de 32 bytes de forma correcta, pero duplicaría el identificador de la planta al final de la cadena de texto del nombre común.
    
    C) Se producirá un error de compilación inmediato porque el compilador de Kotlin detecta que el búfer ha sido leído y exige que sea reiniciado obligatoriamente.
    
    D) El archivo temporal se corrompería por completo al llenarse con caracteres extraños e ilegibles generados automáticamente por el sistema de archivos.
    
    
    ??? quote "Solución"
    
        ❌ A) El compilador de Kotlin no analiza el estado de los punteros internos de los búferes de Java NIO, por lo que compilará el código de forma completamente normal sin advertencias de error.
        
        ✅ B) Al leer el identificador del registro mediante `buffer.getInt()`, el puntero de posición del búfer se desplaza automáticamente hacia adelante 4 bytes (los que ocupa el entero). Si se escribe el búfer en el canal temporal sin rebobinarlo (`buffer.rewind()`), solo se transferirán los bytes restantes (los 28 bytes del nombre y precio). Esto provocará que los registros en el archivo temporal dejen de medir 32 bytes, desalineando todo el fichero y corrompiendo las lecturas posteriores.
        
        ❌ C) El programa compilará perfectamente, pero el fallo de lógica se manifestará en tiempo de ejecución al analizar el archivo resultante.
        
        ❌ D) El archivo temporal no se llenará de caracteres extraños generados por el sistema; simplemente contendrá los registros originales recortados (sin el ID), lo cual desmorona la estructura de tamaño fijo del archivo.
    


    **Pregunta 24: Para eliminar un registro de un archivo binario de tamaño fijo, se utiliza la técnica estándar de copiar los registros que se desean conservar a un archivo temporal (`.tmp`) para luego reemplazar el original. ¿Cuál es el motivo técnico por el que no se realiza la eliminación directamente sobre el propio archivo original (in-situ)?**
    
    A) Los sistemas operativos actuales tienen prohibido por motivos de seguridad realizar modificaciones físicas en la zona central de cualquier archivo binario una vez escrito.
    
    B) La clase `FileChannel` y la API de NIO carecen de la capacidad técnica de situar el puntero en posiciones intermedias del archivo original, limitando las escrituras únicamente al final de este.
    
    C) Eliminar físicamente un bloque de bytes de la mitad de un archivo obligaría a reescribir y desplazar hacia adelante en el disco duro todos los bytes posteriores del archivo para tapar el hueco vacío, lo cual es una operación de Entrada/Salida extremadamente lenta y costosa para el rendimiento.
    
    D) El uso de un archivo temporal es un requisito obligatorio impuesto por la máquina virtual de Java para poder liberar la memoria caché del procesador antes de cerrar el canal.
    
    
    ??? quote "Solución"
    
        ❌ A) Los sistemas operativos permiten realizar cualquier operación de lectura y escritura en cualquier posición de un archivo físico si el programa cuenta con los permisos de usuario correspondientes.
        
        ❌ B) `FileChannel` es perfectamente capaz de situarse y escribir en cualquier posición intermedia utilizando el método `.position(long)`, tal y como se demuestra en la función de modificación de registros.
        
        ✅ C) Físicamente, los archivos se almacenan en bloques de disco de manera consecutiva. No existe una instrucción en los sistemas de archivos que permita "recortar" un bloque intermedio de un archivo y juntar los extremos de forma instantánea. Para simular esto en el archivo original, se tendría que leer y desplazar un byte hacia atrás toda la información posterior al hueco, lo cual consume una gran cantidad de tiempo y recursos de disco. Copiar la información filtrada a un nuevo archivo secuencial temporal resulta mucho más eficiente y seguro.
        
        ❌ D) La máquina virtual de Java no exige el uso de archivos temporales para la gestión de su memoria caché ni para la liberación de recursos del sistema.