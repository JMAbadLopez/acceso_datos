## Entrega y Documentación Final

!!! danger "📁 Entrega Final"
    Esta es la entrega definitiva de la UD2. El proyecto entregado deberá ejecutarse sin errores; si no compila o da error de ejecución no podrá ser calificado.

### 🎯 Proyecto final de Gestión de Ficheros

!!! warning "🎯 Proyecto final de Gestión de Ficheros"

    En esta última práctica ampliarás tu proyecto con un CRUD para gestionar la información de tu aplicación en un fichero binario de acceso aleatorio.


    1. Añade al menú de tu aplicación una opción más llamada `5. Gestión fichero BIN`:

        ```text
        ===== CATÁLOGO DE VIDEOJUEGOS =====
        1. Gestión CSV
        2. Leer catálogo desde XML
        3. Leer catálogo desde JSON
        4. Conversiones entre formatos
        5. Gestión fichero BIN
        0. Salir
        ====================================
        ```


    2. Crea un submenú para gestionar la información del fichero binario con las opciones siguientes:

        ```text
        ===== CRUD fichero BIN =====
        1. Importar información de un fichero de intercambio.
        2. Leer información del fichero binario.
        3. Añadir un registro nuevo
        4. Modificar un registro existente (por ID)
        5. Eliminar un registro existente (por ID)
        0. Volver
        ```

    3. Define las longitudes en bytes de los datos de tu registro (Int = 4 bytes, Double = 8 bytes, String = longitud fija rellenada con espacios, etc) para que coincida con la `data class` que has utilizado en las prácticas anteriores.



    **Requisitos de funcionamiento:** mismos que en la práctica anterior. Además:

    - Opción **IMPORTAR**: Vacía el fichero binario (o lo crea si no existe) e importa los datos desde un CSV, XML o JSON (elige el que prefieras).
    - Opción **LEER**: Muestra por consola la información del fichero binario.
    - Opción **AÑADIR**: Pide el ID y comprueba se quea válido (para ser válido ha de ser un número y no existir en el fichero binario), si no es válido lo vuelve a pedir hasta que lo sea. Después pide el resto de campos (los campos numéricos se pedirán hasta que sean válidos, es decir, ser número y ser del tipo correcto). Por último añade un registro al final del fichero con toda la información.
    - Opción **MODIFICAR**: Pide ID hasta que sea válido (debe ser un número entero) y recorre el fichero binario para ver si existe, si no lo encuentra informa con un mensaje y no realiza ningún cambio pero si lo encuentra muestra el nombre o algún otro campo representativo, pide alguno de los otros campos (comprobando que es correcto) y actualiza la información en el fichero informando con un mensaje.
    - Opción **ELIMINAR**: Pide ID hasta que sea válido (debe ser un número entero) y recorre el fichero binario para ver si existe, si no lo encuentra informa con un mensaje pero si lo encuentra muestra el nombre o algún otro campo representativo y pide confirmación para eliminar, entonces, si se confirma el borrado se elimina el registro y en caso contrario no se elimina (en ambos casos se informa con un mensaje).


    **Aspectos técnicos:** mismos que en la páctica anterior.

### El fichero README.md

En un proyecto de software el código fuente por sí solo no cuenta toda la historia y es fundamental crear documentación adicional. La forma estándar y más extendida de hacerlo es a través de un fichero `LEEME.md` (o `README.md`). Un proyecto sin un `LEEME.md` se considera incompleto o poco profesional.

El fichero `LEEME.md` es lo primero que verá cualquier persona (incluido nuestro "yo" del futuro) que quiera entender nuestro código. Es buena práctica explicar qué hace el proyecto, cómo se utiliza y por qué se tomaron algunas decisiones, por ejemplo ¿por qué elegimos un registro de 36 bytes?” o “¿por qué el nombre del fichero es registros.dat?".

Un buen fichero `LEEME.md` debería contener, como mínimo, las siguientes secciones:

* **Nombre del proyecto y breve descripción**.
* **Estructura de Datos**: En esta sección se explica el diseño de los datos.
* **Instrucciones de Ejecución**: Pasos claros y sencillos para que otra persona pueda ejecutar nuestro programa.
* **Decisiones de Diseño** (Opcional pero Recomendado): Un pequeño apartado para explicar brevemente por qué tomamos ciertas decisiones.

La extensión `.md` significa **Markdown** que es un lenguaje de marcado ligero que permite dar formato a un texto plano usando caracteres simples. Podemos crearlo con cualquier editor de texto (IntelliJ, VSCode, Bloc de notas...) y guardarlo con la extensión `.md`. Plataformas como GitHub, GitLab y otros sistemas de documentación convierten estos ficheros en páginas web.

#### Sintaxis básica de Markdown para empezar

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

#### Ejemplo Markdown

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
