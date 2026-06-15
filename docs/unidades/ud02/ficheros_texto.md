# 3. Ficheros de texto

Los ficheros de texto son legibles directamente por humanos y son una buena opción para guardar información después de cerrar el programa. A continuación se muestran algunas clases y métodos para leer y escribir información en ellos:

## Métodos de Ficheros de Texto

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

## Ejemplo - Escritura y lectura en fichero de texto plano .txt

```kotlin
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
```

!!! success "🔍 Ejecutar y Analizar"
    Ejecuta el ejemplo anterior y comprueba que la salida es la siguiente:

```bash
Fichero de texto escrito.
Contenido leído con readAllLines:
Primera línea
Segunda línea
¡Hola desde Kotlin!
Contenido leído con readString:
Primera línea
Segunda línea
¡Hola desde Kotlin!

Contenido leído con newBufferedReader:
Primera línea
Segunda línea
¡Hola desde Kotlin!
```

---
