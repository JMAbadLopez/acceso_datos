## 3. Ficheros de texto

Los ficheros de texto son legibles directamente por humanos y son una buena opción para guardar información después de cerrar el programa. A continuación se muestran algunas clases y métodos para leer y escribir información en ellos:

### Métodos de Ficheros de Texto

| Método | Descripción |
| :--- | :--- |
| `Files.readAllLines(path)` devuelve `List<String>` | Leer ficheros. |
| `Files.exists(path)` | Verificar existencia. |
| `split()`, `trim()`, `toIntOrNull()` | Procesar texto. |
| `Files.write(path, lines)` | Escribe una lista de líneas (`List<String>`) a un fichero. |
| `StandardOpenOption.READ` | Abrir un fichero en modo lectura. |
| `StandardOpenOption.WRITE` | Abrir un fichero en modo escritura. |
| `StandardOpenOption.APPEND` | Agrega contenido al final del fichero sin borrar lo anterior. |
| `StandardOpenOption.CREATE` | Si no existe, lo crea. |
| `StandardOpenOption.TRUNCATE_EXISTING` | Si existe, borra lo anterior. |
| `Files.newBufferedReader(Path)`, `Files.newBufferedWriter(Path)` | Más eficiente para ficheros grandes. |
| `Files.readString(Path)` (Java 11+), `Files.writeString(Path, String)` | Lectura/escritura completa como bloque. |

Dentro de los ficheros de texto existen ficheros de texto plano (sin ningún tipo de estructura) y ficheros de texto en los que la información está estructurada.

!!! tip "¿Qué método usar en cada caso?"
    | Situación | Método recomendado |
    | :--- | :--- |
    | Leer un fichero pequeño de una sola vez | `Files.readAllLines()` o `Files.readString()` |
    | Escribir un bloque de texto de una vez | `Files.writeString()` |
    | Escribir una lista de líneas | `Files.write(path, lines)` |
    | Añadir líneas sin borrar el contenido | `Files.write(path, lines, StandardOpenOption.APPEND)` |
    | Leer un fichero grande línea a línea (eficiencia de memoria) | `Files.newBufferedReader()` |
    | Escribir un fichero grande con control de buffer | `Files.newBufferedWriter()` |

### Ejemplo 4. Escritura y lectura en fichero de texto plano .txt

El siguiente ejemplo muestra como leer y escribir información en un ficheros de texto plano `.txt`.

```kotlin
import java.nio.file.Files
import java.nio.charset.StandardCharsets

fun main() {
    textoPlano()
}


fun textoPlano(){

    // Ruta del fichero con el que vamos a trabajar
    val ruta = Path.of("muestras","anotacion.txt")

    Files.createDirectories(ruta.parent) // Crea la carpeta "muestras" si no existe

    // Escritura de una línea writeString (si no existe lo crea y si existe lo vacía)
    val anotacion = "Cuidados de orquídeas realizados."
    Files.writeString(ruta, anotacion, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)

    // Lectura rápida de todo el bloque con readString
    val contenidoCompleto = Files.readString(ruta)
    println("--- 1. Contenido del fichero leído con readString: ---")
    println(contenidoCompleto)



    // Escritura de múltiples líneas en un fichero de texto con Files.write
    val lineas = listOf(
        "\n1. Regar cuando las raíces se observen de color grisáceo.",
        "2. Mantener en un espacio con luz indirecta.",
        "3. Evitar corrientes de aire."
    )
    Files.write(ruta, lineas, StandardCharsets.UTF_8, StandardOpenOption.APPEND)

    // Lectura con readAllLines (devuelve una lista línea por línea)
    val lineasLeidas = Files.readAllLines(ruta)
    println("\n--- 2. Contenido leído con readAllLines: ---")
    for (linea in lineasLeidas) {
        println(linea)
    }

    

    // Escribir registros de actividad (Logs) con Buffered Writer
    Files.newBufferedWriter(ruta, StandardOpenOption.APPEND).use { writer ->
        writer.write("(SISTEMA) Invernadero automatizado iniciado...\n")
        writer.write("(SENSOR) Nivel de humedad óptimo detectado (75%).\n")
    }

    // Lectura secuencial eficiente con newBufferedReader
    println("\n--- 3. Contenido leído con newBufferedReader: ---")
    Files.newBufferedReader(ruta).use { reader ->
        reader.lineSequence().forEach { linea ->
            println(linea)
        }
    }
}
```

!!! success "Prueba y analiza el ejemplo"
    Prueba el código de ejemplo y verifica que la salida por consola es:

    ```text
    --- 1. Contenido del fichero leído con readString: ---
    Cuidados de orquídeas realizados.

    --- 2. Contenido leído con readAllLines: ---
    Cuidados de orquídeas realizados.
    1. Regar cuando las raíces se observen de color grisáceo.
    2. Mantener en un espacio con luz indirecta.
    3. Evitar corrientes de aire.

    --- 3. Contenido leído con newBufferedReader: ---
    Cuidados de orquídeas realizados.
    1. Regar cuando las raíces se observen de color grisáceo.
    2. Mantener en un espacio con luz indirecta.
    3. Evitar corrientes de aire.
      (SISTEMA) Invernadero automatizado iniciado...
      (SENSOR) Nivel de humedad óptimo detectado (75%).
    ```

!!! example "Autoevaluación"

    **Pregunta 7: Se quiere registrar el stock de una planta al final del archivo diario sin borrar las anotaciones anteriores, y se escribe el siguiente código. ¿Cuál será el comportamiento del programa?**

    ```kotlin
    import java.nio.file.Files
    import java.nio.file.Path
    import java.nio.file.StandardOpenOption
    
    fun main() {
        val ruta = Path.of("documentos/registro_diario.txt")
        val nuevaAnotacion = "14:00 - Queda al menos una orquídea."
    
        Files.writeString(ruta, nuevaAnotacion, StandardOpenOption.WRITE)
    }
    ```

    A) La nueva anotación se añadirá limpiamente al final del fichero de texto, respetando todo lo que ya estuviera escrito anteriormente.
    
    B) El código generará un error de compilación porque el método `writeString` requiere obligatoriamente que la ruta se defina con la clase clásica `java.io.File`.
    
    C) Se lanzará una excepción en tiempo de ejecución porque la opción `WRITE` exige que el archivo esté completamente vacío antes de poder escribir en él.
    
    D) Si el archivo ya existía y contenía texto, la nueva anotación se escribirá al principio del fichero, sobrescribiendo (pisando) únicamente los caracteres iniciales que ocupen su misma longitud.

    ??? quote "Solución"
    
        ❌ A) Para añadir texto al final de un fichero existente sin destruir el contenido previo se debe utilizar obligatoriamente la opción `StandardOpenOption.APPEND`.
        
        ❌ B) El método `Files.writeString` pertenece a la API moderna de NIO (`java.nio.file.Files`) y está diseñado específicamente para trabajar con objetos de tipo `Path`.
        
        ❌ C) La opción `WRITE` simplemente abre el archivo con permisos de escritura. No lanza ninguna excepción por el hecho de que el archivo contenga datos previos, a menos que existan problemas de permisos del sistema operativo.
        
        ✅ D) Al abrir el archivo únicamente con `StandardOpenOption.WRITE` (sin combinarlo con `APPEND` ni con `TRUNCATE_EXISTING`), el canal de escritura posiciona su puntero en el byte 0. Al escribir la nueva frase, esta sobrescribirá directamente los primeros caracteres del texto existente, dejando intacto el resto del archivo a partir de esa posición.



    **Pregunta 8: ¿Qué ocurrirá al ejecutar el siguiente fragmento de código si el archivo de texto `cuidados_orquideas.txt` existe, pero está completamente vacío?**

    ```kotlin
    import java.nio.file.Files
    import java.nio.file.Path
    
    fun main() {
        val ruta = Path.of("documentos/cuidados_orquideas.txt")
        
        val lineas = Files.readAllLines(ruta)
        println("Líneas leídas: ${lineas.size}")
    }
    ```

    A) El programa se ejecutará sin errores y mostrará por consola: `Líneas leídas: 0`.
    
    B) El método `Files.readAllLines` lanzará una excepción en tiempo de ejecución (`NoSuchFileException`) al detectar que el archivo no tiene líneas de texto que leer.
    
    C) El programa se quedará en un bucle infinito intentando buscar la primera línea de texto del archivo.
    
    D) Se producirá un error de compilación porque `Files.readAllLines` no puede devolver una lista vacía.


    ??? quote "Solución"
    
        ✅ A) El método `Files.readAllLines` lee correctamente archivos de texto vacíos sin lanzar excepciones. Al no encontrar líneas físicas, devuelve una lista mutable de cadenas (`List<String>`) con un tamaño (`.size`) igual a 0, ejecutando el programa con éxito.
        
        ❌ B) El error `NoSuchFileException` solo se lanza si el archivo físico no existe en la ruta especificada. Si el archivo existe pero está vacío, el sistema lo trata como una lectura válida de cero caracteres.
        
        ❌ C) La API de NIO controla internamente el final del archivo (EOF) al leer los flujos de caracteres, por lo que el método termina la lectura de inmediato y devuelve la colección sin generar ningún bloqueo de ejecución.
        
        ❌ D) El tipo de retorno de `Files.readAllLines` es `MutableList<String>`. En programación, las listas pueden crearse y gestionarse con un tamaño de cero elementos perfectamente, lo cual es totalmente compatible con la sintaxis del lenguaje.
