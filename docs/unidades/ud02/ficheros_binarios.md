# 5. Ficheros binarios

Los ficheros binarios no son legibles directamente por humanos (.exe, .jpg, .mp3, .dat). En ellos los datos pueden estar no estructurados o estructurados.

## 5.1. Ficheros binarios no estructurados

En los ficheros binarios no estructurados los datos se escriben “tal cual” en bytes, sin un formato estructurado definido por un estándar. El programa que los lee necesita saber cómo interpretar esos bytes.

### Métodos de Ficheros Binarios No Estructurados

| Método | Descripción |
| :--- | :--- |
| `Files.readAllBytes(Path)`, `Files.write(Path, ByteArray)` | Lee y escribe bytes puros. |
| `Files.newInputStream(Path)`, `Files.newOutputStream(Path)` | Flujo de bytes directo. |

### Ejemplo binario no estructurado

Escribir bit a bit los datos `1 2 3 4 5` en un fichero llamado `datos.bin`.

```kotlin
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
```

🔍 **Ejecuta el ejemplo anterior y comprueba que la salida es la siguiente:**

```bash
Directorio creado: F:\kot\1-ficheros\ejemplos1\multimedia\bin
Fichero binario creado: F:\kot\1-ficheros\ejemplos1\multimedia\bin\datos.bin
Contenido leído (byte a byte):
1 2 3 4 5
```

---

## 5.2. Ficheros binarios estructurados

En los ficheros binarios estructurados los datos se guardan de forma estructurada siguiendo una organización predefinida, con campos y tipos de datos, a veces con un formato estándar (ej. PNG, ZIP, MP3, etc.) que pueden incluir cabeceras (con información como versión, tamaño, etc.) y registros (con campos fijos o delimitadores). El orden de bytes y los tamaños están definidos, lo que permite a cualquier programa que conozca el formato leerlo correctamente. Las clases `DataOutputStream` y `DataInputStream` de java.io sirven para leer y escribir ficheros binarios estructurados.

### Métodos de DataOutputStream

| Método | Descripción |
| :--- | :--- |
| `writeInt(int)` | Escribe un entero con signo. Entero (4 bytes). |
| `writeDouble(double)` | Escribe un número en coma flotante. Decimal (8 bytes). |
| `writeFloat(float)` | Escribe un número float. Decimal (4 bytes). |
| `writeLong(long)` | Escribe un long. Entero largo (8 bytes). |
| `writeBoolean(boolean)` | Escribe un valor verdadero/falso. Booleano (1 byte). |
| `writeChar(char)` | Escribe un carácter Unicode. Carácter (2 bytes). |
| `writeUTF(String)` | Escribe una cadena precedida por su longitud en 2 bytes. Cadena UTF-8. |
| `writeByte(int)` | Escribe un solo byte. Byte (1 byte). |
| `writeShort(int)` | Escribe un short. Entero corto (2 bytes). |

### Métodos de DataInputStream

| Método | Descripción |
| :--- | :--- |
| `readInt()` | Lee un entero con signo (Entero). |
| `readDouble()` | Lee un número double (Decimal). |
| `readFloat()` | Lee un número float (Decimal). |
| `readLong()` | Lee un long (Entero largo). |
| `readBoolean()` | Lee un valor verdadero/falso (Booleano). |
| `readChar()` | Lee un carácter Unicode (Carácter). |
| `readUTF()` | Lee una cadena UTF-8 (Cadena UTF-8). |
| `readByte()` | Lee un byte (Byte). |
| `readShort()` | Lee un short (Entero corto). |

### Ejemplo

Lectura y escritura en ficheros binarios estructurados (con tipos primitivos):

```kotlin
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Path
fun main() {
    val ruta = Path.of("multimedia/binario.dat")
    Files.createDirectories(ruta.parent)
    // Escritura binaria
    val fos = FileOutputStream(ruta.toFile())
    val out = DataOutputStream(fos)
    out.writeInt(42) // int (4 bytes)
    out.writeDouble(3.1416) // double (8 bytes)
    out.writeUTF("K") // char (2 bytes)
    out.close()
    fos.close()
    println ("Fichero binario escrito con DataOutputStream.")
    // Lectura binaria
    val fis = FileInputStream(ruta.toFile())
    val input = DataInputStream(fis)
    val entero = input.readInt()
    val decimal = input.readDouble()
    val caracter = input.readUTF()
    input.close()
    fis.close()
    println ("Contenido leído:")
    println ("  Int: $entero")
    println ("  Double: $decimal")
    println ("  Char: $caracter")
}
```

🔍 **Ejecuta el ejemplo anterior y comprueba que la salida es la siguiente:**

```bash
Fichero binario escrito con DataOutputStream.
Contenido leído:
  Int: 42
  Double: 3.1416
  Char: K
```
