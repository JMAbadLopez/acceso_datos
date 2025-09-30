# 4. Ficheros de intercambio de información

Los ficheros de texto en los que la información está estructurada y organizada de una manera predecible permiten que distintos sistemas la lean y entiendan. Estos tipos de ficheros se utilizan en el desarrollo de software para **intercambiar información entre aplicaciones** y algunos de los formatos más importantes son **CSV, JSON y XML**.

Para poder llevar a cabo este intercambio de información, hay que extraer la información del fichero origen. Este proceso no se realiza línea por línea, sino que el contenido del fichero se lee (parsea) utilizando la técnica de **serialización/deserialización**:

* **Serialización**: Proceso de convertir un **objeto en memoria** (por ejemplo, una data class) en una representación textual o binaria (como un String en formato JSON o XML) que se puede guardar en un fichero o enviar por red.
* **Deserialización**: Es el proceso inverso de leer un **fichero** (JSON, XML, etc.) y **reconstruir el objeto original** en memoria para poder trabajar con él.

A continuación se muestra una tabla con clases y herramientas que se utilizan para serializar / deserializar:

## 4.1 Métodos de Serialización/Deserialización

| Método | Descripción |
| :--- | :--- |
| `java.io.Serializable` | Marca que un objeto es serializable. |
| `ObjectOutputStream` | Serializa y escribe un objeto. |
| `ObjectInputStream` | Lee un objeto serializado. |
| `@transient` | Excluye atributos de la serialización. |
| `ReadObject` | Lee y reconstruye un objeto binario. |
| `WriteObject` | Guarda un objeto como binario. |
| `@Serializable` | Permite convertir el data class a JSON y viceversa. |

### Ejemplo - Serializar y deserializar un objeto (usando `@Transient`)

```kotlin
import java.io.*
// Clase Persona (serializable completamente)
class Persona(val nombre: String, val edad: Int) : Serializable
// Clase Usuario con un atributo que NO se serializa
class Usuario(
    val nombre: String,
    @Transient val clave: String // Este campo no se guardará
) : Serializable
fun main() {
    val rutaPersona = "multimedia/persona.obj"
    val rutaUsuario = "multimedia/usuario.obj"
    // Asegurar que el directorio exista
    val directorio = File("documentos")
    if (!directorio.exists()) {
        directorio.mkdirs()
    }
    // --- Serializar Persona ---
    val persona = Persona("Pol", 30)
    try {
        ObjectOutputStream(FileOutputStream(rutaPersona)).use { oos ->
            oos.writeObject(persona)
        }
        println ("Persona serializada.")
    } catch (e: IOException) {
        println ("Error al serializar Persona: ${e.message}")
    }
    // --- Deserializar Persona ---
    try {
        val personaLeida = ObjectInputStream(FileInputStream(rutaPersona)).use { ois ->
            ois.readObject() as Persona
        }
        println ("Persona deserializada:")
        println ("Nombre: ${personaLeida.nombre}, Edad: ${personaLeida.edad}")
    } catch (e: Exception) {
        println ("Error al deserializar Persona: ${e.message}")
    }
    // --- Serializar Usuario ---
    val usuario = Usuario("Eli", "1234")
    try {
        ObjectOutputStream(FileOutputStream(rutaUsuario)).use { oos ->
            oos.writeObject(usuario)
        }
        println ("Usuario serializado.")
    } catch (e: IOException) {
        println ("Error al serializar Usuario: ${e.message}")
    }
    // --- Deserializar Usuario ---
    try {
        val usuarioLeido = ObjectInputStream(FileInputStream(rutaUsuario)).use { ois ->
            ois.readObject() as Usuario
        }
        println ("Usuario deserializado:")
        println ("Nombre: ${usuarioLeido.nombre}, Clave: ${usuarioLeido.clave}")
    } catch (e: Exception) {
        println ("Error al deserializar Usuario: ${e.message}")
    }
}
```

🔍 **Ejecuta el ejemplo anterior y comprueba que la salida es la siguiente:**

```bash
Persona serializada.
Persona deserializada:
Nombre: Pol, Edad: 30
Usuario serializado.
Usuario deserializado:
Nombre: Eli, Clave: null
```

A continuación se describen los 3 tipos de ficheros más comunes para intercambio de información. Se muestran ejemplos de lectura y escritura usando serialización y deserialización utilizando un proyecto con Gradle:

---

## 4.2. CSV (Comma-Separated Values)

Son ficheros de texto plano con valores separados por un delimitador (coma, punto y coma, etc.). Son útiles para exportar/importar datos desde Excel, Google Sheets, o bases de datos. Se manejan con herramientas como OpenCSV (más antigua) o **Kotlin-CSV** (la que utilizaremos).

### Métodos de Kotlin-CSV

| Método | Ejemplo |
| :--- | :--- |
| `readAll(File)` | `val filas = csvReader().readAll(File("alumnos.csv"))` |
| `readAllWithHeader(File)` | `val datos = csvReader().readAllWithHeader(File("alumnos.csv"))` |
| `open { readAllAsSequence() }` | `csvReader().open("alumnos.csv") { readAllAsSequence().forEach { println(it) } }` |
| `writeAll(data, File)` | `csvWriter().writeAll(listOf(listOf("Pol", "9")), File("salida.csv"))` |
| `writeRow(row, File)` | `csvWriter().writeRow(listOf("Ade", "8"), File("salida.csv"))` |
| `writeAllWithHeader(data, File)` | `csvWriter().writeAllWithHeader(listOf(mapOf("nombre" to "Eli", "nota" to "10")), File("salida.csv"))` |
| `delimiter`, `quoteChar`, etc. | `csvReader { delimiter = ';' }` |

### Ejemplo de lectura y escritura de ficheros CSV:

Partimos de un fichero llamado `mis_plantas.csv` con la información siguiente:

```bash
1;Aloe Vera;Aloe barbadensis miller;7;0.6
2;Lavanda;Lavandula angustifolia;3;1.0
3;Helecho de Boston;Nephrolepis exaltata;5;0.9
4;Bambú de la suerte;Dracaena sanderiana;4;1.5
5;Girasol;Helianthus annuus;2;3.0
```

Donde los campos corresponden a:

* `id_planta` (int)
* `nombre_comun` (string)
* `nombre_cientifico` (string)
* `frecuencia_riego` (int)
* `altura_máxima` (double)

Utilizaremos la librería **Kotlin-CSV**. Por tanto habrá que indicarlo en el fichero `build.gradle.kts` añadiendo las siguientes líneas:

* **En `plugins`:**

```bash
kotlin("plugin.serialization") version "1.9.0"
```

* **En `dependencies`:**

```bash
implementation("com.github.doyaaaaaken:kotlin-csv-jvm:1.9.1")
```

```kotlin
import java.nio.file.Files
import java.nio.file.Path
import java.io.File
// Librería específica de Kotlin para leer y escribir ficheros CSV.
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
//Usamos una 'data class' para representar la estructura de una planta.
data class Planta(val id_planta: Int, val nombre_comun: String, val nombre_cientifico: String, val riego: Int, val altura: Double)
fun main() {
    val entradaCSV = Path.of("datos_ini/mis_plantas.csv")
    val salidaCSV = Path.of("datos_ini/mis_plantas2.csv")
    val datos: List<Planta>
    datos = leerDatosInicialesCSV(entradaCSV)
    for (dato in datos) {
        println ("  - ID: ${dato.id_planta}, Nombre común: ${dato.nombre_comun}, Nombre científico: ${dato.nombre_cientifico}, Frecuencia de riego: ${dato.riego} días, Altura: ${dato.altura} metros")
    }
    escribirDatosCSV(salidaCSV, datos)
}
fun leerDatosInicialesCSV(ruta: Path): List<Planta> {
    var plantas: List<Planta> = emptyList()
    // Comprobar si el fichero es legible antes de intentar procesarlo.
    if (!Files.isReadable(ruta)) {
        println ("Error: No se puede leer el fichero en la ruta: $ruta")
    } else {
        // Configuramos el lector de CSV con el delimitador
        val reader = csvReader {
            delimiter = ';'
        }
        /* Leemos TODO el fichero CSV.
        El resultado es una lista de listas de Strings (`List<List<String>>`),
        donde cada lista interna representa una fila del fichero.*/
        val filas: List<List<String>> = reader.readAll(ruta.toFile())
        /* Convertir la lista de texto plano en una lista de objetos 'Planta'.
        `mapNotNull` funciona como un `map` y descartando todos los `null` de la lista final.
        Si una fila del CSV es inválida, devolvemos `null`
        y `mapNotNull` se encarga de ignorarla. */
        plantas = filas.mapNotNull { columnas ->
            // Validar si La fila tiene al menos 4 columnas.
            if (columnas.size >= 5) {
                try {
                    val id_planta = columnas[0].toInt()
                    val nombre_comun = columnas[1]
                    val nombre_cientifico = columnas[2]
                    val riego = columnas[3].toInt()
                    val altura = columnas[4].toDouble()
                    Planta(id_planta,nombre_comun, nombre_cientifico, riego, altura) //crear el objeto Planta
                } catch (e: Exception) {
                    /* Si ocurre un error en la conversión (ej: NumberFormatException),
                    capturamos la excepción, imprimimos un aviso (opcional)
                    y devolvemos `null` para que `mapNotNull` descarte esta fila. */
                    println ("Fila inválida ignorada: $columnas -> Error: ${e.message}")
                    null
                }
            } else {
                // Si la fila no tiene suficientes columnas, es inválida. Devolvemos null.
                println ("Fila con formato incorrecto ignorada: $columnas")
                null
            }
        }
    }
    return plantas
}
fun escribirDatosCSV(ruta: Path,plantas: List<Planta>){
    try {
        val fichero: File = ruta.toFile()
        csvWriter {
            delimiter = ';'
        }.writeAll(
            plantas.map { planta ->
                listOf (planta.id_planta.toString(),
                    planta.nombre_comun,
                    planta.nombre_cientifico,
                    planta.riego.toString(),
                    planta.altura.toString())
            } ,
            fichero
        )
        println ("\nInformación guardada en: $fichero")
    } catch (e: Exception) {
        println ("Error: ${e.message}")
    }
}
```

🔍 **Ejecuta el ejemplo anterior, comprueba que la salida es la siguiente, que se ha creado el fichero `mis_plantas2.csv` y que su contenido es correcto:**

```bash
  - ID: 1, Nombre común: Aloe Vera, Nombre científico: Aloe barbadensis miller, Frecuencia de riego: 7 días, Altura: 0.6 metros
  - ID: 2, Nombre común: Lavanda, Nombre científico: Lavandula angustifolia, Frecuencia de riego: 3 días, Altura: 1.0 metros
  - ID: 3, Nombre común: Helecho de Boston, Nombre científico: Nephrolepis exaltata, Frecuencia de riego: 5 días, Altura: 0.9 metros
  - ID: 4, Nombre común: Bambú de la suerte, Nombre científico: Dracaena sanderiana, Frecuencia de riego: 4 días, Altura: 1.5 metros
  - ID: 5, Nombre común: Girasol, Nombre científico: Helianthus annuus, Frecuencia de riego: 2 días, Altura: 3.0 metros

Información guardada en: datos_ini\mis_plantas2.csv
```

---

## 4.3. XML (eXtensible Markup Language)

Los ficheros XML son muy estructurados y extensibles. Se basan en etiquetas anidadas similar a HTML. Permiten la validación de datos (mediante esquemas XSD) y es ideal para integración con sistemas empresariales (legacy). Se manejan con librerías como JAXB, DOM, JDOM2 o **Jackson XML (XmlMapper)** que es la que utilizaremos.

### Métodos de Jackson XML

| Método | Descripción |
| :--- | :--- |
| `readValue(File, Class<T>)` | Lee un fichero XML y lo convierte en un objeto Kotlin/Java. |
| `readValue(String, Class<T>)` | Lee un String XML y lo convierte en un objeto. |
| `writeValue(File, Object)` | Escribe un objeto como XML en un fichero. |
| `writeValueAsString(Object)` | Convierte un objeto en una cadena XML. |
| `writeValueAsBytes(Object)` | Convierte un objeto en un array de bytes XML. |
| `registerModule(Module)` | Registra un módulo como `KotlinModule` o `JavaTimeModule`. |
| `enable(SerializationFeature)` | Activa una opción de serialización (por ejemplo, indentado). |
| `disable(DeserializationFeature)` | Desactiva una opción de deserialización. |
| `configure(MapperFeature, boolean)` | Configura opciones generales del mapeo. |
| `setDefaultPrettyPrinter(...)` | Establece un formateador personalizado. |

### Ejemplo de lectura y escritura de ficheros XML:

Partimos de un fichero llamado `mis_plantas.xml` con la información siguiente:

```xml
<plantas>
  <planta>
    <id_planta>1</id_planta>
    <nombre_comun>Aloe Vera</nombre_comun>
    <nombre_cientifico>Aloe barbadensis miller</nombre_cientifico>
    <frecuencia_riego>7</frecuencia_riego>
    <altura_maxima>0.6</altura_maxima>
  </planta>
  <planta>
    <id_planta>2</id_planta>
    <nombre_comun>Lavanda</nombre_comun>
    <nombre_cientifico>Lavandula angustifolia</nombre_cientifico>
    <frecuencia_riego>3</frecuencia_riego>
    <altura_maxima>1.0</altura_maxima>
  </planta>
  <planta>
    <id_planta>3</id_planta>
    <nombre_comun>Helecho de Boston</nombre_comun>
    <nombre_cientifico>Nephrolepis exaltata</nombre_cientifico>
    <frecuencia_riego>5</frecuencia_riego>
    <altura_maxima>0.9</altura_maxima>
  </planta>
  <planta>
    <id_planta>4</id_planta>
    <nombre_comun>Bambú de la suerte</nombre_comun>
    <nombre_cientifico>Dracaena sanderiana</nombre_cientifico>
    <frecuencia_riego>4</frecuencia_riego>
    <altura_maxima>1.5</altura_maxima>
  </planta>
  <planta>
    <id_planta>5</id_planta>
    <nombre_comun>Girasol</nombre_comun>
    <nombre_cientifico>Helianthus annuus</nombre_cientifico>
    <frecuencia_riego>2</frecuencia_riego>
    <altura_maxima>3.0</altura_maxima>
  </planta>
</plantas>
```

Utilizaremos la librería **Jackson XML**. Por tanto habrá que indicarlo en el fichero `build.gradle.kts` añadiendo las siguientes líneas:

```bash
implementation ("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.17.0")
implementation ("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.0")
```

```kotlin
import java.nio.file.Path
import java.io.File
// Anotaciones y clases de la librería Jackson para el mapeo a XML.
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
/*Representa la estructura de una única planta. La propiedad 'id_planta' será la etiqueta <id_planta>...</id_planta> (así todas) */
data class Planta(
    @JacksonXmlProperty(localName = "id_planta")
    val id_planta: Int,
    @JacksonXmlProperty(localName = "nombre_comun")
    val nombre_comun: String,
    @JacksonXmlProperty(localName = "nombre_cientifico")
    val nombre_cientifico: String,
    @JacksonXmlProperty(localName = "frecuencia_riego")
    val frecuencia_riego: Int,
    @JacksonXmlProperty(localName = "altura_maxima")
    val altura_maxima: Double
)
//nombre del elemento raíz
@JacksonXmlRootElement(localName = "plantas")
// Data class que representa el elemento raíz del XML.
data class Plantas(
    @JacksonXmlElementWrapper(useWrapping = false) // No necesitamos la etiqueta <plantas> aquí
    @JacksonXmlProperty(localName = "planta")
    val listaPlantas: List<Planta> = emptyList()
)
fun main() {
    val entradaXML = Path.of("datos_ini/mis_plantas.xml")
    val salidaXML = Path.of("datos_ini/mis_plantas2.xml")
    val datos: List<Planta>
    datos = leerDatosInicialesXML(entradaXML)
    for (dato in datos) {
        println ("  - ID: ${dato.id_planta}, Nombre común: ${dato.nombre_comun}, Nombre científico: ${dato.nombre_cientifico}, Frecuencia de riego: ${dato.frecuencia_riego} días, Altura: ${dato.altura_maxima} metros")
    }
    escribirDatosXML(salidaXML, datos)
}
fun leerDatosInicialesXML(ruta: Path): List<Planta> {
    val fichero: File = ruta.toFile()
    // Deserializar el XML a objetos Kotlin
    val xmlMapper = XmlMapper().registerKotlinModule()
    // 'readValue' convierte el contenido XML en una instancia de la clase 'Plantas'
    val plantasWrapper: Plantas = xmlMapper.readValue(fichero)
    return plantasWrapper.listaPlantas
}
fun escribirDatosXML(ruta: Path,plantas: List<Planta>) {
    try {
        val fichero: File = ruta.toFile()
        // Creamos instancia de la clase 'Plantas' (raíz del XML).
        val contenedorXml = Plantas(plantas)
        // Configuramos el 'XmlMapper' (motor de Jackson) para la conversión a XML.
        val xmlMapper = XmlMapper().registerKotlinModule()
        // Convertimos 'contenedorXml' en un String con formato XML.
        // .writerWithDefaultPrettyPrinter() formatea con indentación y saltos de línea
        val xmlString = xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(contenedorXml)
        // escribir un String en un fichero con 'writeText'
        fichero.writeText(xmlString)
        println ("\nInformación guardada en: $fichero")
    } catch (e: Exception) {
        println ("Error: ${e.message}")
    }
}
```

🔍 **Ejecuta el ejemplo anterior, comprueba que la salida es la siguiente, que se ha creado el fichero `mis_plantas2.xml` y que su contenido es correcto:**

```bash
  - ID: 1, Nombre común: Aloe Vera, Nombre científico: Aloe barbadensis miller, Frecuencia de riego: 7 días, Altura: 0.6 metros
  - ID: 2, Nombre común: Lavanda, Nombre científico: Lavandula angustifolia, Frecuencia de riego: 3 días, Altura: 1.0 metros
  - ID: 3, Nombre común: Helecho de Boston, Nombre científico: Nephrolepis exaltata, Frecuencia de riego: 5 días, Altura: 0.9 metros
  - ID: 4, Nombre común: Bambú de la suerte, Nombre científico: Dracaena sanderiana, Frecuencia de riego: 4 días, Altura: 1.5 metros
  - ID: 5, Nombre común: Girasol, Nombre científico: Helianthus annuus, Frecuencia de riego: 2 días, Altura: 3.0 metros

Información guardada en: datos_ini\mis_plantas2.xml
```

---

## 4.4. JSON (JavaScript Object Notation)

Son ficheros ligeros, fáciles de leer y con una estructura de pares clave-valor y listas. Ideales para APIs REST, ficheros de configuración y bases de datos NoSQL (como MongoDB). Se maneja con librerías como Jackson & Gson (Java) o **kotlinx.serialization** (la que utilizaremos en Kotlin).

### Métodos de kotlinx.serialization

| Método / Ejemplo | Descripción |
| :--- | :--- | :---|
| `Json.encodeToString(objeto)` | `Json.encodeToString(persona)` | Convierte un objeto Kotlin a una cadena JSON. |
| `Json.encodeToString(serializer, obj)` | `Json.encodeToString(Persona.serializer(), persona)` | Igual que el anterior pero especificando el serializador. |
| `Json.decodeFromString(json)` | `Json.decodeFromString<Persona>(json)` | Convierte una cadena JSON a un objeto Kotlin. |
| `Json.decodeFromString(serializer, s)` | `Json.decodeFromString(Persona.serializer(), json)` | Igual que el anterior pero con el serializador explícito. |
| `Json.encodeToJsonElement(objeto)` | `val elem = Json.encodeToJsonElement(persona)` | Convierte un objeto a un árbol `JsonElement`. |
| `Json.decodeFromJsonElement(elem)` | `val persona = Json.decodeFromJsonElement<Persona>(elem)` | Convierte un `JsonElement` a objeto Kotlin. |
| `Json.parseToJsonElement(string)` | `val elem = Json` | Parsea una cadena JSON a un árbol `JsonElement` sin mapear. |

### Ejemplo de lectura y escritura de ficheros JSON

Partimos de un fichero llamado `mis_plantas.json` con la información siguiente:

```json
[
  {
    "id_planta": 1,
    "nombre_comun": "Aloe Vera",
    "nombre_cientifico": "Aloe barbadensis miller",
    "frecuencia_riego": 7,
    "altura_maxima": 0.6
  },
  {
    "id_planta": 2,
    "nombre_comun": "Lavanda",
    "nombre_cientifico": "Lavandula angustifolia",
    "frecuencia_riego": 3,
    "altura_maxima": 1.0
  },
  {
    "id_planta": 3,
    "nombre_comun": "Helecho de Boston",
    "nombre_cientifico": "Nephrolepis exaltata",
    "frecuencia_riego": 5,
    "altura_maxima": 0.9
  },
  {
    "id_planta": 4,
    "nombre_comun": "Bambú de la suerte",
    "nombre_cientifico": "Dracaena sanderiana",
    "frecuencia_riego": 4,
    "altura_maxima": 1.5
  },
  {
    "id_planta": 5,
    "nombre_comun": "Girasol",
    "nombre_cientifico": "Helianthus annuus",
    "frecuencia_riego": 2,
    "altura_maxima": 3.0
  }
]
```

Utilizaremos la librería **kotlinx.serialization**. Por tanto habrá que indicarlo en el fichero `build.gradle.kts` añadiendo las siguientes líneas:

* **En `plugins`:**

```bash
kotlin("plugin.serialization") version "1.9.0"
```

* **En `dependencies`:**

```bash
implementation ("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
```

Llamaremos a `Json.encodeToString()` para serializar una instancia de esta clase y a `Json.decodeFromString()` para deserializarla.

```kotlin
import java.nio.file.Files
import java.nio.file.Path
import java.io.File
// Clases de la librería oficial de Kotlin para la serialización/deserialización.
import kotlinx.serialization.*
import kotlinx.serialization.json.*
//Usamos una 'data class' para representar la estructura de una planta e indicamos que es serializable
@Serializable
data class Planta(val id_planta: Int, val nombre_comun: String, val nombre_cientifico: String, val frecuencia_riego: Int, val altura_maxima: Double)
fun main() {
    val entradaJSON = Path.of("datos_ini/mis_plantas.json")
    val salidaJSON = Path.of("datos_ini/mis_plantas2.json")
    val datos: List<Planta>
    datos = leerDatosInicialesJSON(entradaJSON)
    for (dato in datos) {
        println ("  - ID: ${dato.id_planta}, Nombre común: ${dato.nombre_comun}, Nombre científico: ${dato.nombre_cientifico}, Frecuencia de riego: ${dato.frecuencia_riego} días, Altura: ${dato.altura_maxima} metros")
    }
    escribirDatosJSON(salidaJSON, datos)
}
fun leerDatosInicialesJSON(ruta: Path): List<Planta> {
    var plantas: List<Planta> = emptyList()
    val jsonString = Files.readString(ruta)
    /* A `Json.decodeFromString` le pasamos el String con el JSON.
    Con `<List<Planta>>`, le indicamos que debe interpretarlo como
    una lista de objetos de tipo planta".
    La librería usará la anotación @Serializable de la clase Planta para saber cómo mapear los campos del JSON ("id_planta", "nombre_comun", etc.)
    a las propiedades del objeto. */
    plantas = Json.decodeFromString<List<Planta>>(jsonString)
    return plantas
}
fun escribirDatosJSON(ruta: Path,plantas: List<Planta>) {
    try {
        /* La librería `kotlinx.serialization`
        toma la lista de objetos `Planta` (`List<Planta>`) y la convierte en una
        única cadena de texto con formato JSON.
        `prettyPrint` formatea el JSON para que sea legible. */
        val json = Json { prettyPrint = true }.encodeToString(plantas)
        // Con `Files.writeString` escribimos el String JSON en el fichero de salida
        Files.writeString(ruta, json)
        println ("\nInformación guardada en: $ruta")
    } catch (e: Exception) {
        println ("Error: ${e.message}")
    }
}
```

🔍 **Ejecuta el ejemplo anterior, comprueba que la salida es la siguiente, que se ha creado el fichero `mis_plantas2.json` y que su contenido es correcto:**

```bash
  - ID: 1, Nombre común: Aloe Vera, Nombre científico: Aloe barbadensis miller, Frecuencia de riego: 7 días, Altura: 0.6 metros
  - ID: 2, Nombre común: Lavanda, Nombre científico: Lavandula angustifolia, Frecuencia de riego: 3 días, Altura: 1.0 metros
  - ID: 3, Nombre común: Helecho de Boston, Nombre científico: Nephrolepis exaltata, Frecuencia de riego: 5 días, Altura: 0.9 metros
  - ID: 4, Nombre común: Bambú de la suerte, Nombre científico: Dracaena sanderiana, Frecuencia de riego: 4 días, Altura: 1.5 metros
  - ID: 5, Nombre común: Girasol, Nombre científico: Helianthus annuus, Frecuencia de riego: 2 días, Altura: 3.0 metros

Información guardada en: datos_ini\mis_plantas2.json
```

---

## 4.5. Conversiones entre ficheros

Una vez vistas las características de los ficheros de intercambio de información más comunes podemos llegar a la conclusión que en programación y gestión de datos, no todos los formatos sirven igual para todos los casos. **Convertir entre CSV, JSON y XML** permite aprovechar las ventajas de cada uno.

El patrón para convertir datos de un formato a otro es casi siempre el mismo. En lugar de intentar una conversión directa, utilizamos nuestras clases de Kotlin (`data class`) como un paso intermedio universal: **Formato Origen → Objetos Kotlin en Memoria → Formato Destino**

> 🔍 **Realiza algunas conversiones entre ficheros CSV, JSON y XML para practicar la lectura / escritura y la serialización / deserialización. Puedes reutilizar el código de los ejemplos.**

---

## 🎯 Práctica 3: Creación y lectura de un fichero de datos

Realiza lo siguiente:

* **Diseña tu data class**: Define la `data class` de Kotlin que represente un único elemento de tu colección de datos. Debe tener un ID único de tipo `Int`, un nombre de tipo `String` y, al menos, otros dos campos (al menos uno de tipo `Double`).
* **Crea tu fichero de datos**: (.csv, .json o .xml) con al menos 5 registros de tu colección dentro de la carpeta `datos_ini`.
* **Añade dependencias necesarias**: Añade las librerías necesarias para leer tu fichero y _serializar / deserializar_ datos en `build.gradle.kts`.
* **Crea la función de lectura**: La función debe leer el fichero de texto y devolver una lista de objetos `leerDatosIniciales(): List<DataClass>`.
* **Verifica que funciona**: Imprime por consola la información leída.
* **Aspectos Técnicos Obligatorios**:
* Se debe incluir un manejo básico de errores (ej: comprobar si el fichero existe antes de leerlo, try-catch para conversiones numéricas, etc.).

---

## 📁 Entrega parcial

Entrega el código fuente del proyecto comprimido en un fichero `.zip` para que el profesor te dé sugerencias de mejora (el programa entregado deberá ejecutarse, si da error de ejecución, no se podrá revisar).
