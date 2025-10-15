# 7. 📁 **Entrega y documentación final**

## El proyecto

¡Enhorabuena! Si has seguido _todas las prácticas_, deberás tener un **Proyecto de Acceso a Ficheros** completo con los siguientes apartados.

* **Organización de directorios**
* **Ficheros de intercambio**
* **Ficheros binarios de Acceso Aleatorio**

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
# Gestor de mediciones

Este es un programa de consola desarrollado en Kotlin para gestionar una colección de registros de mediciones de temperatura y humedad registradas por unos sensores.
Los datos se almacenan en un fichero binario de acceso aleatorio llamado *mediciones.dat*

## 1. Estructura de datos

### **Data Class:**

```kotlin
data class Sensor(
    val id_sensor: Int,
    val nombre: String,
    val temperatura: Double
    val humedad: Double
)
```

### **Estructura del registro binario:**

- **ID**: Int - 4 bytes
- **Nombre**: String - 20 bytes (longitud fija)
- **temperatura**: Double - 8 bytes
- **humedad**: Double - 8 bytes
- **Tamaño Total del Registro**: 4 + 20 + 8 + 8 = 40 bytes

## 2. Instrucciones de ejecución

- **Requisitos previos**: Asegúrate de tener un JDK (ej. versión 17 o superior) instalado.
- **Compilación**: Abre el proyecto en IntelliJ IDEA y deja que Gradle sincronice las dependencias.
- **Ejecución**: Ejecuta la función main del fichero Main.kt.
- **Ficheros necesarios**: El programa espera encontrar un fichero *datos_iniciales.csv* en la carpeta *datos_ini* dentro de la raíz del proyecto para la carga inicial de datos.

## 3. Decisiones de diseño

- Elegí CSV para los datos iniciales porque es un formato muy fácil de crear y editar manualmente con cualquier hoja de cálculo.
- Decidí que el campo nombre tuviera 20 bytes porque considero que es suficiente para la mayoría de nombres de sensores sin desperdiciar demasiado espacio.

````
