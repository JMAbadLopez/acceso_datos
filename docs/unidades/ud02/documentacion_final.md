# Entrega y Documentación Final

!!! danger "📁 Entrega Final"
    Esta es la entrega definitiva de la UD2. El proyecto entregado deberá ejecutarse sin errores; si no compila o da error de ejecución no podrá ser calificado.

## El proyecto

¡Enhorabuena! Si has seguido _todas las prácticas_, deberás tener un **Proyecto de Acceso a Ficheros** completo con los siguientes apartados.

* **Organización de directorios**
* **Ficheros de intercambio**
* **Ficheros binarios de Acceso Aleatorio**
* **Programa de conversión entre formatos**

## Programa de conversión entre formatos

Una aplicación real a menudo necesita intercambiar datos con otros sistemas que usan formatos distintos. En esta parte del proyecto añadirás un fichero `Conversor.kt` que encadene tres transformaciones usando tu `data class` de videojuegos como modelo intermedio:

```
videojuegos.csv  →  videojuegos.json  →  videojuegos.dat  →  videojuegos_export.csv
```

El patrón es siempre el mismo que ya conoces: **Formato Origen → Objetos Kotlin → Formato Destino**.

### Lo que debes implementar

Crea en `Conversor.kt` las siguientes funciones y un `main` que las ejecute en orden:

* **`csvAJson(origen: Path, destino: Path)`**: Lee `videojuegos.csv` usando Kotlin-CSV, deserializa los datos en objetos de tu `data class` y los serializa a `videojuegos.json` con `kotlinx.serialization`.
* **`jsonABinario(origen: Path, destino: Path)`**: Lee `videojuegos.json`, reconstruye la lista de objetos y escribe cada uno como un registro de tamaño fijo en `videojuegos.dat` usando `FileChannel` y `ByteBuffer`.
* **`binarioACsv(origen: Path, destino: Path)`**: Lee `videojuegos.dat` registro a registro, reconstruye los objetos y los escribe en `videojuegos_export.csv`.

Tras las tres conversiones, el `main` debe comprobar que `videojuegos_export.csv` contiene los mismos datos que `videojuegos.csv` original e imprimir un mensaje de verificación.

```kotlin
fun main() {
    val csv     = Path.of("datos_ini/videojuegos.csv")
    val json    = Path.of("datos_fin/videojuegos.json")
    val binario = Path.of("datos_fin/videojuegos.dat")
    val export  = Path.of("datos_fin/videojuegos_export.csv")

    println("=== CSV → JSON ===")
    csvAJson(csv, json)

    println("=== JSON → Binario ===")
    jsonABinario(json, binario)

    println("=== Binario → CSV ===")
    binarioACsv(binario, export)

    println("=== Verificación ===")
    // Compara el CSV original con el exportado e imprime si coinciden
}
```

### Aspectos Técnicos Obligatorios

1. Reutiliza las funciones de lectura y escritura que ya implementaste en las prácticas anteriores.
2. Añade manejo de errores en cada función (`try-catch` y comprobación de existencia del fichero).
3. Las tres funciones deben estar en `Conversor.kt` y la `data class` en su propio fichero.

## El fichero README.md

En un proyecto de software el código fuente por sí solo no cuenta toda la historia y es fundamental crear documentación adicional. La forma estándar y más extendida de hacerlo es a través de un fichero `LEEME.md` (o `README.md`). Un proyecto sin un `LEEME.md` se considera incompleto o poco profesional.

El fichero `LEEME.md` es lo primero que verá cualquier persona (incluido nuestro "yo" del futuro) que quiera entender nuestro código. Es buena práctica explicar qué hace el proyecto, cómo se utiliza y por qué se tomaron algunas decisiones, por ejemplo ¿por qué elegimos un registro de 36 bytes?” o “¿por qué el nombre del fichero es registros.dat?".

Un buen fichero `LEEME.md` debería contener, como mínimo, las siguientes secciones:

* **Nombre del proyecto y breve descripción**.
* **Estructura de Datos**: En esta sección se explica el diseño de los datos.
* **Instrucciones de Ejecución**: Pasos claros y sencillos para que otra persona pueda ejecutar nuestro programa.
* **Decisiones de Diseño** (Opcional pero Recomendado): Un pequeño apartado para explicar brevemente por qué tomamos ciertas decisiones.

La extensión `.md` significa **Markdown** que es un lenguaje de marcado ligero que permite dar formato a un texto plano usando caracteres simples. Podemos crearlo con cualquier editor de texto (IntelliJ, VSCode, Bloc de notas...) y guardarlo con la extensión `.md`. Plataformas como GitHub, GitLab y otros sistemas de documentación convierten estos ficheros en páginas web.

## Sintaxis básica de Markdown para empezar

```markdown
# Título de Nivel 1
## Título de Nivel 2
### Título de Nivel 3
**Texto en negrita**
*Texto en cursiva*
- Elemento de una lista
1. Elemento de una lista numerada
```

Para bloques de código, rodearlos con tres comillas invertidas (```) y especificar el lenguaje:

````markdown
```kotlin
fun main() {
    println("Hola, Markdown!")
}
```
````

## Ejemplo Markdown

````markdown
# Catálogo de Videojuegos

Programa de consola en Kotlin para gestionar un catálogo de videojuegos.
Permite leer datos desde CSV, convertirlos a JSON y a un fichero binario de acceso aleatorio, y exportarlos de nuevo a CSV.

## 1. Estructura de datos

### **Data Class:**

```kotlin
data class Videojuego(
    val id: Int,
    val titulo: String,
    val genero: String,
    val anio: Int,
    val nota: Double
)
```

### **Estructura del registro binario:**

- **ID**: Int - 4 bytes
- **titulo**: String - 40 bytes (longitud fija)
- **genero**: String - 20 bytes (longitud fija)
- **anio**: Int - 4 bytes
- **nota**: Double - 8 bytes
- **Tamaño Total del Registro**: 4 + 40 + 20 + 4 + 8 = 76 bytes

## 2. Instrucciones de ejecución

- **Requisitos previos**: JDK 17 o superior instalado.
- **Compilación**: Abre el proyecto en IntelliJ IDEA y deja que Gradle sincronice las dependencias.
- **Fichero de datos inicial**: Coloca `videojuegos.csv` en la carpeta `datos_ini` antes de ejecutar.
- **Ejecución principal**: Ejecuta `Main.kt` para el catálogo con acceso aleatorio.
- **Conversión entre formatos**: Ejecuta `Conversor.kt` para encadenar las transformaciones CSV → JSON → Binario → CSV.

## 3. Decisiones de diseño

- Elegí CSV como formato inicial porque es sencillo de crear y editar manualmente con cualquier hoja de cálculo.
- Reservé 40 bytes para el título porque algunos títulos largos como "The Legend of Zelda: Breath of the Wild" tienen 38 caracteres.
- Reservé 20 bytes para el género porque los géneros más largos que manejo ("Plataformas") tienen 11 caracteres y quiero margen para futuros géneros.

````
