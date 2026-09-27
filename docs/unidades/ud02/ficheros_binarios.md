## 5. Ficheros binarios y formas de acceso

Los ficheros binarios (como `.exe`, `.jpg`, `.mp3`, `.dat` o `.bin`) no son legibles directamente por humanos. La información se guarda en formato binario (ceros y unos), lo que permite un almacenamiento óptimo, rápido y de alta eficiencia.

A continuación tenemos una tabla comparativa con algunos tipos de ficheros vistos en puntos anteriores y algunos tipos binarios:

| Extensión | Contenido típico | Comentario didáctico |
| :--- | :--- | :----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`.txt`** | Texto plano | Legible en cualquier editor de texto. Muy fácil de modificar manualmente por el usuario. |
| **`.csv`** | Valores separados por comas o punto y coma | Formato tabular ligero. Ideal para hojas de cálculo o importaciones iniciales. |
| **`.dat`** | Binario o texto genérico | "Fichero de datos" clásico de sistemas legacy. No aclara directamente por su nombre si contiene texto o bytes crudos. |
| **`.bin`** | Binario puro | Contiene información organizada directamente en bytes. No se puede abrir directamente en texto sin ver caracteres extraños, pero es el formato óptimo para almacenamiento estructurado de alta eficiencia. |

> **IMPORTANTE:** los ficheros binarios no son fichero de texto plano y por tanto no pueden abrirse con editores de código en modo texto normal como Bloc de Notas o TextEdit ya que se verán caracteres extraños, símbolos y espacios.

En los siguientes apartados veremos cómo manejar ficheros de imágenes y de datos. Para estos últimos, aprenderemos a acceder a su información de dos maneras: de forma secuencial (leyendo en orden desde el principio hasta el final del fichero) o de forma aleatoria (saltando directamente a la posición o registro específico que nos interesa).

### 5.1. Ficheros binarios de imágenes

Las imágenes son ficheros binarios con estructuras de metadatos complejas estandarizadas (`.jpg`, `.png`, `.bmp`) que representan píxeles organizados en un plano bidimensional.

Para interactuar con ellas en Java y Kotlin, utilizamos principalmente dos elementos en equipo:

- **`BufferedImage`:** Es una clase que representa la imagen **en la memoria RAM**. Funciona como una "cuadrícula o lienzo" donde cada celda es un píxel con su propio color (en formato RGB o escala de grises). Modificamos o leemos los píxeles directamente sobre este lienzo.
- **`ImageIO`:** Es la clase de utilidad encargada de realizar las operaciones de **entrada/salida (E/S)**. Se encarga de traducir el fichero físico del disco (compreso en JPG o PNG) a un objeto `BufferedImage` en memoria (lectura), o viceversa (escritura).

**Métodos clave para el manejo de imágenes**

| Elemento / Método | Tipo | Descripción | Ejemplo de uso |
| :-------------------------------------------------- | :--- | :------------------------------------------------------------------------------------------------------------------------------------------ | :--- |
| **`ImageIO.read(File)`** | *Lectura* | Carga una imagen desde el disco duro y la transforma en un objeto `BufferedImage` en memoria RAM. | `val img = ImageIO.read(File("hoja.jpg"))` |
| **`ImageIO.write(BufferedImage, format, File)`** | *Escritura* | Guarda el lienzo de píxeles de la memoria en un fichero físico del disco con el formato indicado. | `ImageIO.write(img, "png", File("resultado.png"))` |
| **`BufferedImage(ancho, alto, tipo)`** | *Creación* | Crea un lienzo en blanco en memoria con las dimensiones especificadas y un tipo de color concreto (ej. `TYPE_INT_RGB`). | `val lienzo = BufferedImage(200, 100, BufferedImage.TYPE_INT_RGB)` |
| **`setRGB(x, y, rgb)`** | *Modificación* | Modifica el color de un píxel concreto de la cuadrícula utilizando sus coordenadas cartesianas (X, Y). | `lienzo.setRGB(10, 5, Color.GREEN.rgb)` |
| **`getRGB(x, y)`** | *Consulta* | Obtiene el valor numérico del color del píxel situado en las coordenadas especificadas (X, Y). | `val colorInt = lienzo.getRGB(10, 5)` |
| **`Color(rgb)`** | *Conversión* | Clase que permite decodificar el valor entero del píxel para poder extraer de forma sencilla sus componentes de color rojo, verde y azul. | `val color = Color(lienzo.getRGB(x, y))` <br> `val rojo = color.red` |

#### Ejemplo 8. Generación de una imagen píxel a píxel

```kotlin
import java.nio.file.Files
import java.io.File

import java.awt.Color
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

fun main() {
    crearImagen()
}

fun crearImagen(){
    val ancho = 200
    val alto = 100
    val imagen = BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB)

    // Rellenamos la imagen pixel a pixel simulando un gradiente fotosintético
    for (x in 0 until ancho) {
        for (y in 0 until alto) {
            val rojo = 0                      // Sin canales rojos
            val verde = (x * 255) / ancho     // Gradiente verde horizontal
            val azul = (y * 255) / alto       // Gradiente azul vertical

            val colorPixel = Color(rojo, verde, azul)
            imagen.setRGB(x, y, colorPixel.rgb)
        }
    }

    // Guardamos el mapa térmico resultante en disco
    val archivo = File("datos/sensor_clorofila.png")
    Files.createDirectories(archivo.toPath().parent)
    ImageIO.write(imagen, "png", archivo)
    println("Imagen simulada guardada en: ${archivo.absolutePath}")
}
```

!!! success "Prueba y analiza el ejemplo"
    Prueba el código de ejemplo y verifica que se ha creado la imagen correctamente.

#### Ejemplo 9. Conversión de una imagen a escala de grises

El siguiente ejemplo convierte a escala de grises la imagen generada en el ejemplo anterior.

```kotlin
import java.nio.file.Files
import java.io.File

import java.awt.Color
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

import java.nio.file.Path
import java.nio.file.StandardCopyOption


fun main() {
    grises()
}

fun grises() {
    val originalPath = Path.of("datos","sensor_clorofila.png")
    val copiaPath = Path.of("datos","sensor.jpg")
    val grisPath = Path.of("datos","sensor_gris.png")

    // 1. Comprobamos la disponibilidad de la muestra original
    if (!Files.isReadable(originalPath)) {
        println("No se encuentra la muestra original en: $originalPath")
    } else {
        // 2. Duplicamos la muestra con java.nio para preservar el original intacto
        Files.createDirectories(copiaPath.parent)
        Files.copy(originalPath, copiaPath, StandardCopyOption.REPLACE_EXISTING)
        println("Muestra de respaldo creada en: $copiaPath")

        // 3. Cargamos la imagen en un búfer de memoria
        val imagen: BufferedImage = ImageIO.read(copiaPath.toFile())

        // 4. Transformación de color pixel a pixel
        for (x in 0 until imagen.width) {
            for (y in 0 until imagen.height) {
                // Capturamos el color del pixel actual
                val colorPixel = Color(imagen.getRGB(x, y))

                // Calculamos la escala de grises ponderando por sensibilidad del ojo humano
                val gris = (colorPixel.red * 0.299 + colorPixel.green * 0.587 + colorPixel.blue * 0.114).toInt()

                // Establecemos los mismos valores de brillo para los canales RGB
                val colorGris = Color(gris, gris, gris)
                imagen.setRGB(x, y, colorGris.rgb)
            }
        }

        // 5. Exportamos el resultado
        ImageIO.write(imagen, "png", grisPath.toFile())
        println("Procesamiento terminado. Muestra en gris guardada en: $grisPath")
    }
}
```

!!! success "Prueba y analiza el ejemplo"
    Prueba el código de ejemplo y verifica que se han creado las imagenes correctamente.

### 5.2. Acceso secuencial a ficheros binarios

En el acceso secuencial la información se procesa en orden estricto, byte a byte o registro a registro, desde el inicio del fichero hasta llegar al final.

**Datos no estructurados**

Se utiliza cuando queremos guardar o leer bytes "tal cual", sin que sigan un formato o estándar definido. El programa que los lee debe saber de antemano qué significan. A contiunación se describen algunos métodos útiles:

| Método | Descripción |
| :--- | :--- |
| `Files.readAllBytes(Path)` | Lee todos los bytes del fichero de golpe en un `ByteArray`. |
| `Files.write(Path, ByteArray)` | Escribe un bloque de bytes de una sola vez. |

#### Ejemplo 10: Escritura y lectura de bytes crudos

El siguiente ejemplo simula el guardado de una firma digital de seguridad de un lote de semillas en un fichero llamado `lote.bin` dentro de la carpeta datos

```kotlin
import java.nio.file.Path
import java.nio.file.Files

fun main() {
    lote()
}

fun lote() {
    val ruta = Path.of("datos","lote.bin")

    try {
        // Asegura que el directorio destino existe
        val directorio = ruta.parent
        if (directorio != null && !Files.exists(directorio)) {
            Files.createDirectories(directorio)
            println("Directorio creado: ${directorio.toAbsolutePath()}")
        }

        // Verifica si se tienen permisos de escritura en el directorio
        if (!Files.isWritable(directorio)) {
            println("No se tienen permisos de escritura en el directorio: $directorio")
        } else {
            // Datos en bytes a escribir (por ejemplo, códigos de control del lote)
            val datosDeControl = byteArrayOf(10, 20, 30, 40, 50)
            Files.write(ruta, datosDeControl)
            println("Fichero binario creado: ${ruta.toAbsolutePath()}")

            // Verifica si se puede leer el fichero creado
            if (!Files.isReadable(ruta)) {
                println("No se tienen permisos de lectura para el fichero: $ruta")
            } else {
                // Lectura del fichero binario
                val bytesLeidos = Files.readAllBytes(ruta)
                println("Contenido de seguridad leído (byte a byte):")
                for (b in bytesLeidos) {
                    print("$b ")
                }
                println()
            }
        }
    } catch (e: Exception) {
        println("Ocurrió un error: ${e.message}")
    } catch (e: SecurityException) {
        println("No se tienen permisos suficientes: ${e.message}")
    } 
}
```

!!! success "Prueba y analiza el ejemplo"
    Prueba el código de ejemplo verifica que el fichero se ha creado y que la salida por consola es:

    ```text
    Fichero binario creado: datos\lote.bin
    Contenido de seguridad leído (byte a byte):
    10 20 30 40 50
    ```

!!! example "Autoevaluación"

    **Pregunta 15: Se intenta escribir un bloque de bytes crudos en un archivo utilizando la función `Files.write` de la siguiente manera:**
    
    ```kotlin
    import java.nio.file.Files
    import java.nio.file.Path
    
    fun main() {
        val ruta = Path.of("datos_nuevos", "lote.bin")
        val datos = byteArrayOf(10, 20, 30, 40)
        
        // Intento de escritura directa sin realizar comprobaciones previas
        Files.write(ruta, datos)
        println("Fichero creado con éxito.")
    }
    ```
    
    **Sabiendo que la carpeta llamada `datos_nuevos` no existe físicamente en el disco duro en el momento de iniciar el programa, ¿cuál será el comportamiento de la aplicación al ejecutarse?**
    
    A) La función `Files.write` detectará la ausencia de la ruta y creará automáticamente tanto la carpeta `datos_nuevos` como el archivo `lote.bin` antes de escribir los bytes.
    
    B) Se lanzará una excepción de tipo `NoSuchFileException` (o similar de Entrada/Salida) en la línea de `Files.write`, deteniendo el programa e impidiendo la creación del archivo.
    
    C) El programa finalizará con éxito mostrando el mensaje por consola, pero los bytes se guardarán únicamente de forma temporal en la memoria RAM del sistema.
    
    D) Se producirá un error de compilación en Kotlin porque el constructor de `Path.of` exige que todas las carpetas especificadas existan físicamente en el disco para poder compilar.
    
    
    ??? quote "Solución"
    
        ❌ A) A diferencia de otras operaciones de más alto nivel, la función de bajo nivel `Files.write` no tiene la capacidad de crear de forma automática las carpetas intermedias de la ruta si estas no existen previamente en el sistema de archivos.
        
        ✅ B) Para poder escribir un archivo, el sistema operativo necesita que el directorio contenedor ya exista físicamente en el disco. Si la carpeta `datos_nuevos` no existe, se lanzará una excepción de Entrada/Salida (`NoSuchFileException`) al intentar abrir el canal de escritura. Por esta razón, en el Ejemplo 10 se incluye el bloque de seguridad para crear el directorio padre mediante `Files.createDirectories(directorio)` antes de escribir.
        
        ❌ C) El programa no completará su ejecución de forma normal; la excepción interrumpirá el flujo antes de llegar a la línea del `println`.
        
        ❌ D) La clase `Path` representa únicamente una dirección o ruta lógica en memoria; crear un objeto `Path` no interactúa con el disco físico y es totalmente válido en tiempo de compilación existan o no las carpetas reales.


    
    **Pregunta 16: Se dispone de un archivo binario llamado `lote.bin` que contiene únicamente una secuencia de bytes de control crudos (no legibles directamente como texto humano). Si en lugar de utilizar el método apropiado se intenta leer dicho archivo de la siguiente forma:**
    
    ```kotlin
    import java.nio.file.Files
    import java.nio.file.Path
    
    fun main() {
        val ruta = Path.of("datos", "lote.bin")
        
        // Intento de lectura usando un método diseñado para texto plano
        val lineas = Files.readAllLines(ruta) 
        println("Líneas leídas: ${lineas.size}")
    }
    ```
    
    **¿Cuál será el comportamiento más probable del programa al intentar procesar este archivo binario?**
    
    A) El programa compilará y se ejecutará correctamente, interpretando cada byte individual como si fuera una línea de texto independiente en la consola.
    
    B) Se producirá un error de compilación ya que `Files.readAllLines` solo permite como argumento archivos que tengan explícitamente la extensión `.txt`.
    
    C) Se lanzará una excepción en tiempo de ejecución (como `MalformedInputException`) debido a que el archivo contiene secuencias de bytes crudos que no corresponden a caracteres de texto válidos bajo la codificación de caracteres por defecto (UTF-8).
    
    D) El archivo se borrará automáticamente del disco duro debido a un mecanismo de protección del sistema de archivos al detectar una lectura de tipo incompatible.
    
    
    ??? quote "Solución"
    
        ❌ A) Los bytes crudos de un archivo binario arbitrario (como valores de control, metadatos de imágenes o ejecutables) no representan texto válido estructurado en líneas y no se procesarán de forma transparente como cadenas de texto convencionales.
        
        ❌ B) El método `Files.readAllLines` acepta cualquier objeto `Path` válido independientemente de la extensión que tenga el archivo físico en el disco duro; el compilador no realiza comprobaciones de extensiones de archivos.
        
        ✅ C) El método `Files.readAllLines` intenta decodificar el contenido del archivo utilizando el juego de caracteres estándar (UTF-8 por defecto). Si encuentra bytes arbitrarios que no representan caracteres válidos en dicha codificación (algo muy común en archivos de bytes crudos), el decodificador interno fallará lanzando una excepción de tipo `MalformedInputException`. Por este motivo, los datos puramente binarios deben leerse siempre utilizando `Files.readAllBytes`.
        
        ❌ D) El sistema operativo o el entorno de ejecución de Java jamás eliminarán un archivo de forma automática debido a un error de lectura por incompatibilidad de formatos; el archivo permanecerá intacto en el disco duro.

#### Datos estructurados (tipos primitivos)

Se utiliza cuando guardamos registros que contienen una estructura combinada de tipos primitivos (enteros, booleanos, decimales o texto) de manera consecutiva. El orden y los tamaños en bytes están estrictamente definidos, lo que permite a cualquier programa compatible leer el formato correctamente.

Las clases **`DataOutputStream`** y **`DataInputStream`** de `java.io` son las herramientas básicas para leer y escribir estos tipos de datos primitivos en ficheros binarios de forma estructurada. A continuación se describen algunos de sus métodos:

**Métodos de `DataOutputStream`**

| Método | Descripción | Tamaño en memoria |
| :--- | :--- | :--- |
| `writeInt(int)` | Escribe un entero con signo. | 4 bytes |
| `writeDouble(double)` | Escribe un número en coma flotante de precisión doble. | 8 bytes |
| `writeFloat(float)` | Escribe un número en coma flotante de precisión simple. | 4 bytes |
| `writeLong(long)` | Escribe un entero largo. | 8 bytes |
| `writeBoolean(boolean)` | Escribe un valor de verdadero o falso. | 1 byte |
| `writeChar(char)` | Escribe un carácter Unicode. | 2 bytes |
| `writeUTF(String)` | Escribe una cadena de texto precededida por su longitud en 2 bytes. | Cadena codificada en UTF-8 |
| `writeByte(int)` | Escribe un solo byte. | 1 byte |
| `writeShort(int)` | Escribe un entero corto. | 2 bytes |

**Métodos de `DataInputStream`**

| Método | Descripción |
| :--- | :--- |
| `readInt()` | Lee un entero con signo. |
| `readDouble()` | Lee un número de precisión doble (`Double`). |
| `readFloat()` | Lee un número de precisión simple (`Float`). |
| `readLong()` | Lee un entero largo (`Long`). |
| `readBoolean()` | Lee un valor booleano. |
| `readChar()` | Lee un carácter Unicode. |
| `readUTF()` | Lee una cadena de texto en formato UTF-8 modificado. |
| `readByte()` | Lee un byte. |
| `readShort()` | Lee un entero corto. |

#### Ejemplo 11. Escritura y lectura estructurada con tipos primitivos

El siguiente ejemplo simula el registro de la temperatura mínima, ph del suelo y código de lote en binario estructurado.

```kotlin
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Path

fun main() {
    registro()
}

fun registro() {
    val ruta = Path.of("datos","registro.dat")
    Files.createDirectories(ruta.parent)

    // --- Escritura binaria estructurada ---
    val fos = FileOutputStream(ruta.toFile())
    val out = DataOutputStream(fos)

    out.writeInt(42)               // ID de la parcela (4 bytes)
    out.writeDouble(6.8)           // Nivel de pH del suelo (8 bytes)
    out.writeUTF("ZONA-NORTE")     // Código identificador (Cadena UTF-8)

    out.close()
    fos.close()
    println("--- Fichero binario estructurado guardado correctamente.")

    // --- Lectura binaria estructurada ---
    val fis = FileInputStream(ruta.toFile())
    val input = DataInputStream(fis)

    // Leemos estrictamente en el mismo orden de escritura para no corromper la lectura
    val idParcela = input.readInt()
    val phSuelo = input.readDouble()
    val zonaLabel = input.readUTF()

    input.close()
    fis.close()

    println("Datos leídos del suelo:")
    println(" - ID Parcela: $idParcela")
    println(" - pH del Suelo: $phSuelo")
    println(" - Ubicación: $zonaLabel")
}
```

!!! success "Prueba y analiza el ejemplo"
    Prueba el código de ejemplo verifica que el fichero se ha creado y que la salida por consola es:

    ```text
    --- Fichero binario estructurado guardado correctamente.
    Datos leídos del suelo:
    - ID Parcela: 42
    - pH del Suelo: 6.8
    - Ubicación: ZONA-NORTE
    ```

!!! example "Autoevaluación"

    **Pregunta 17: Se dispone del siguiente código en Kotlin que escribe tres datos primitivos en un archivo binario utilizando la clase `DataOutputStream`:**
    
    ```kotlin
    import java.io.DataOutputStream
    import java.io.FileOutputStream
    
    fun main() {
        val out = DataOutputStream(FileOutputStream("datos/registro.dat"))
        
        out.writeInt(42)            // 1. ID de la parcela (4 bytes)
        out.writeDouble(6.8)        // 2. pH del suelo (8 bytes)
        out.writeUTF("ZONA-NORTE")  // 3. Código (Cadena UTF-8)
        
        out.close()
    }
    ```
    
    **Posteriormente, al intentar recuperar la información con `DataInputStream`, se altera accidentalmente el orden de lectura de los dos primeros campos numéricos de la siguiente forma:**
    
    ```kotlin
    import java.io.DataInputStream
    import java.io.FileInputStream
    
    fun main() {
        val input = DataInputStream(FileInputStream("datos/registro.dat"))
        
        // Se intenta leer primero el double y luego el int (orden inverso al escrito)
        val phSuelo = input.readDouble() 
        val idParcela = input.readInt()  
        val zonaLabel = input.readUTF()
        
        input.close()
    }
    ```
    
    **¿Cuál será el comportamiento del programa al intentar ejecutar la lectura con este orden alterado?**
    
    A) El entorno de ejecución de Java detectará automáticamente el conflicto de tipos en el flujo de bytes y reordenará la lectura para asignar los valores correctos a cada variable.
    
    B) Se producirá un error de compilación inmediato porque el compilador de Kotlin asocia de forma estricta los métodos de lectura con el orden exacto de los métodos de escritura empleados en el archivo.
    
    C) El programa se ejecutará sin lanzar excepciones de forma inmediata, pero las variables numéricas se leerán completamente corrompidas (mostrando valores numéricos extremos o sin sentido) al interpretarse erróneamente los bytes en el flujo de memoria.
    
    D) Se lanzará una excepción de tipo `EOFException` de forma instantánea al intentar leer el primer dato debido a que el archivo se bloqueará por seguridad al no coincidir los tipos de datos.
    
    
    ??? quote "Solución"
    
        ❌ A) Los flujos de datos binarios estructurados no contienen ningún tipo de metadato o etiqueta que indique qué tipo de dato se escribió en cada posición. El programa procesa bytes crudos de forma consecutiva y no puede reordenar nada de manera automática.
        
        ❌ B) El compilador no tiene forma de analizar el archivo físico del disco duro ni sabe qué se escribió previamente en él, por lo que compilará el código de lectura sin mostrar ningún aviso de error.
        
        ✅ C) Al escribir con `writeInt` y `writeDouble`, se guardan consecutivamente 4 bytes y luego 8 bytes. Al intentar leer primero un `double` (`readDouble()`), el programa consumirá los primeros 8 bytes del flujo (los 4 del entero más la mitad del double), interpretándolos erróneamente como un número decimal. La lectura continuará de forma desalineada y todos los datos leídos a partir de ese punto quedarán completamente corrompidos.
        
        ❌ D) La excepción `EOFException` (fin de archivo) solo se lanzará si se intenta leer más allá del tamaño total del archivo físico en bytes, pero no por leer los bytes en un orden de tipos incorrecto.
    
        
    **Pregunta 18: Al trabajar con flujos de datos binarios en Kotlin, se observa habitualmente la siguiente combinación de clases para abrir un archivo de lectura:**
    
    ```kotlin
    val fis = FileInputStream(ruta.toFile())
    val input = DataInputStream(fis)
    ```
    
    **¿Cuál es la función o propósito de envolver la clase `FileInputStream` dentro de un objeto de la clase `DataInputStream`?**
    
    A) `FileInputStream` se limita a realizar la lectura física de bytes crudos desde el disco duro, mientras que `DataInputStream` actúa como un filtro que permite interpretar esos bytes directamente como tipos de datos primitivos de Java/Kotlin (`readInt()`, `readDouble()`, etc.).
    
    B) `DataInputStream` es una clase obligatoria para convertir automáticamente el archivo binario a un formato de texto estructurado XML en memoria antes de poder procesarlo en el código.
    
    C) Sirve únicamente para duplicar de manera automática la velocidad de transferencia del disco duro mediante el uso de búferes físicos integrados en el hardware.
    
    D) `FileInputStream` busca el archivo en una red local o servidor en la nube, mientras que `DataInputStream` se encarga de descifrar la clave de seguridad del archivo utilizando criptografía.
    
    
    ??? quote "Solución"
    
        ✅ A) `FileInputStream` es un flujo básico orientado a bytes que solo sabe leer bytes individuales o bloques de bytes de forma genérica. Al envolverlo con `DataInputStream`, se le dota de métodos especializados de más alto nivel que permiten reconstruir directamente tipos de datos complejos y primitivos leyendo el número exacto de bytes que requiere cada tipo (por ejemplo, 4 bytes para un entero con `readInt()`).
        
        ❌ B) Ninguna de estas dos clases interactúa con estructuras de texto formateado como XML o JSON; su ámbito de trabajo está limitado estrictamente a la lectura y escritura de bytes en formato binario.
        
        ❌ C) El aumento de rendimiento por almacenamiento en búfer es responsabilidad de otra clase especializada llamada `BufferedInputStream`, la cual se puede añadir de manera opcional en la cadena de flujos.
        
        ❌ D) Ambas clases están diseñadas para trabajar con archivos locales de manera estándar y no realizan tareas de red ni descifrado de seguridad criptográfica por defecto.
