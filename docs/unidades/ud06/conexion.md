# Conexión a MongoDB desde Kotlin

En este apartado configuraremos un proyecto Kotlin con el driver oficial de MongoDB y estableceremos nuestra primera conexión.

## 1 Configuración del Proyecto

### Dependencias en `build.gradle.kts`

Usamos el **BOM** (*Bill of Materials*) de MongoDB para gestionar las versiones de forma coherente. Solo añadimos el BOM con versión; las demás dependencias la heredan automáticamente.

!!! success "🔍 Ejecutar y Analizar"
    Crea un nuevo proyecto Kotlin en IntelliJ (Gradle - Kotlin DSL) y añade estas dependencias a tu `build.gradle.kts`:

```kotlin
dependencies {
    // BOM: gestiona las versiones de forma coherente
    implementation(platform("org.mongodb:mongodb-driver-bom:5.3.0"))

    // Driver síncrono de Kotlin (la versión la fija el BOM)
    implementation("org.mongodb:mongodb-driver-kotlin-sync")

    // Serialización Kotlin ↔ BSON (recomendada)
    implementation("org.mongodb:bson-kotlinx")
    
    // Serialización de Kotlin (requerida por bson-kotlinx)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.7.3")
}

plugins {
    // Añade este plugin si no lo tienes ya
    kotlin("plugin.serialization") version "2.0.21"
}
```

!!! info "¿Por qué `bson-kotlinx`?"
    Este paquete permite que MongoDB **serialice y deserialice automáticamente** las data classes de Kotlin hacia/desde documentos BSON. Sin él, tendríamos que mapear cada campo manualmente usando la clase `Document`.

## 2 Opciones de Conexión

Hay dos formas de tener MongoDB disponible para practicar:

### Opción A — MongoDB Atlas (nube, recomendada)

MongoDB Atlas ofrece un **clúster gratuito** (*Free Tier M0*) sin necesidad de tarjeta de crédito.

1. Crea una cuenta en [mongodb.com/cloud/atlas](https://www.mongodb.com/cloud/atlas/register).
2. Crea un clúster gratuito (M0).
3. En **Security > Database Access** crea un usuario con contraseña.
4. En **Security > Network Access** añade tu IP (o `0.0.0.0/0` para pruebas).
5. En **Deployment > Connect > Drivers**, selecciona Kotlin y copia el URI.

El URI tiene este formato:
```
mongodb+srv://usuario:<contraseña>@cluster0.xxxxx.mongodb.net/?retryWrites=true&w=majority
```

### Opción B — MongoDB Community Edition (local)

Si tienes MongoDB instalado localmente, la cadena de conexión es simplemente:
```
mongodb://localhost:27017
```

!!! tip "Instalar MongoDB localmente en macOS"
    ```bash
    brew tap mongodb/brew
    brew install mongodb-community@8.0
    brew services start mongodb-community@8.0
    ```

## 3 Primera Conexión: El `MongoClient`

El punto de entrada al driver es `MongoClient`. Desde él accedemos a bases de datos y colecciones.

!!! success "🔍 Ejecutar y Analizar"
    Crea el archivo `Main.kt` y ejecuta el código. Si la conexión es exitosa, verás el nombre de la base de datos y la lista de colecciones.

```kotlin
import com.mongodb.kotlin.client.MongoClient

fun main() {
    // Sustituye por tu URI (Atlas o local)
    val uri = "mongodb://localhost:27017"

    // 1. Crear el cliente (gestiona el pool de conexiones)
    val client = MongoClient.create(uri)

    // 2. Seleccionar la base de datos (se crea automáticamente si no existe)
    val db = client.getDatabase("tienda_peliculas")

    // 3. Listar las colecciones existentes
    println("Base de datos: ${db.name}")
    println("Colecciones: ${db.listCollectionNames().toList()}")

    // 4. Cerrar el cliente al terminar
    client.close()
}
```

!!! success "🔍 Salida esperada"
    ```
    Base de datos: tienda_peliculas
    Colecciones: []
    ```
    La lista está vacía porque la base de datos se acaba de crear. Las colecciones se crean automáticamente cuando insertamos el primer documento.

!!! warning "Recuerda"
    El `MongoClient` mantiene un **pool de conexiones** interno. En aplicaciones reales se crea **una única instancia** al arrancar y se reutiliza durante toda la vida de la aplicación. Llamar a `client.close()` libera todas las conexiones del pool.

## 4 El Modelo de Datos: `Pelicula`

Para las prácticas de esta unidad usaremos una data class sencilla que representa películas de nuestro catálogo.

!!! success "🔍 Ejecutar y Analizar"
    Crea el archivo `Pelicula.kt`. Observa la anotación `@Serializable` y el tipo `ObjectId` para el identificador.

```kotlin
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@Serializable
data class Pelicula(
    @Contextual
    @SerialName("_id")
    val id: ObjectId? = null,   // null → MongoDB genera el ObjectId automáticamente
    val titulo: String,
    val genero: String,
    val anio: Int,
    val nota: Double
)
```

| Campo | Tipo Kotlin | Tipo BSON | Notas |
|---|---|---|---|
| `id` | `ObjectId?` | `ObjectId` | `@SerialName("_id")` mapea al campo `_id` de MongoDB |
| `titulo` | `String` | `String` | — |
| `genero` | `String` | `String` | — |
| `anio` | `Int` | `Int32` | — |
| `nota` | `Double` | `Double` | — |

!!! info "¿Por qué `ObjectId?` y no `Int`?"
    En MongoDB el identificador de documento no es un número entero autoincremental como en SQL. Es un **ObjectId**, un tipo de 12 bytes único globalmente. Declararlo como `ObjectId?` (nullable) permite que MongoDB lo genere automáticamente cuando sea `null`.

---

## 🎯 Práctica 1. Conectando con MongoDB

!!! warning "🎯 Práctica 1. Conectando con MongoDB"

    1. Crea un proyecto Kotlin en IntelliJ con las dependencias indicadas.
    2. Elige tu opción de conexión (Atlas o local) y anota tu URI de conexión.
    3. Crea `Main.kt` con el código de conexión del apartado 3.
    4. Crea `Pelicula.kt` con la data class del apartado 4.
    5. Ejecuta el programa y verifica que no hay errores de conexión y que la salida muestra el nombre de la base de datos.

---

!!! example "Autoevaluación"

    **Pregunta 3: ¿Qué hace `client.getDatabase("tienda_peliculas")` si la base de datos aún no existe en MongoDB?**

    A) Lanza una excepción `DatabaseNotFoundException` porque no puede referenciarse una base de datos inexistente.

    B) Devuelve `null`, indicando que la base de datos no existe todavía.

    C) Devuelve un objeto `MongoDatabase` que referencia la base de datos; MongoDB la creará físicamente cuando se inserte el primer documento en ella.

    D) Crea inmediatamente la base de datos vacía en MongoDB y devuelve la referencia.

    ??? quote "Solución"
        ❌ A) MongoDB no lanza excepciones por referenciar bases de datos o colecciones inexistentes. Prefiere el enfoque *lazy*: crear las estructuras cuando realmente se necesiten.

        ❌ B) `getDatabase` siempre devuelve un objeto `MongoDatabase` válido (nunca `null`), independientemente de si la base de datos existe o no.

        ✅ C) Esta es la filosofía de MongoDB: **creación implícita o *lazy***. `getDatabase` solo devuelve un objeto en memoria. La base de datos real en MongoDB no se crea hasta que se inserta el primer documento en alguna de sus colecciones. Por eso la lista de colecciones está vacía al principio.

        ❌ D) La base de datos no se crea al llamar `getDatabase`; se crea cuando se produce la primera escritura real (insertOne, etc.).

    **Pregunta 4: ¿Por qué se usa `@SerialName("_id")` en el campo `id` de la data class `Pelicula`?**

    A) Porque Kotlin no permite que los campos comiencen con guion bajo, así que se usa `id` en Kotlin y `_id` en el documento BSON.

    B) Para indicarle al serializador que el campo `id` de Kotlin se corresponde con el campo `_id` de MongoDB, ya que el driver no puede inferirlo automáticamente.

    C) Porque MongoDB rechaza cualquier documento que no tenga un campo llamado exactamente `id` (sin guion bajo).

    D) `@SerialName` es una anotación decorativa sin efecto real; el driver usa el nombre del campo Kotlin directamente.

    ??? quote "Solución"
        ✅ A) Correcto en la motivación práctica: Kotlin permite campos con guion bajo, pero la convención es no usarlos. La anotación `@SerialName("_id")` es la forma estándar de mapear el nombre del campo Kotlin al nombre del campo BSON cuando son distintos. Sin ella, el driver buscaría un campo llamado `id` en el documento, no `_id`, y la deserialización fallaría.

        ❌ B) Aunque la explicación del motivo es acertada (el nombre Kotlin `id` ≠ nombre BSON `_id`), la frase "no puede inferirlo automáticamente" es correcta — el driver no cambia `id` a `_id` por sí solo, por eso necesitamos la anotación. Esta opción es la más precisa.

        ❌ C) MongoDB no requiere que el campo se llame `id`. El campo de clave primaria en MongoDB siempre se llama `_id` (con guion bajo). Si el documento no lo incluye, MongoDB lo genera.

        ❌ D) `@SerialName` tiene efecto real y directo: le dice al serializador qué nombre usar en el BSON resultante. Sin ella, el campo `id` de Kotlin se escribiría como `"id"` en el documento, no como `"_id"`.
