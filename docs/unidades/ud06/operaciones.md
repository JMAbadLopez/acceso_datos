# Operaciones CRUD con el Driver Kotlin

Con la conexión establecida y el modelo definido, vamos a realizar las cuatro operaciones fundamentales: **C**reate, **R**ead, **U**pdate y **D**elete sobre nuestra colección `peliculas`.

## 1 Obtener la Colección

Antes de cualquier operación necesitamos una referencia a la colección. Con el driver Kotlin, especificamos la data class como tipo genérico para que la serialización sea automática.

```kotlin
import com.mongodb.kotlin.client.MongoClient
import org.bson.types.ObjectId

fun main() {
    val client = MongoClient.create("mongodb://localhost:27017")
    val db = client.getDatabase("tienda_peliculas")

    // La colección se crea automáticamente al insertar el primer documento
    val peliculas = db.getCollection<Pelicula>("peliculas")
    
    // ... operaciones ...

    client.close()
}
```

## 2 Insertar Documentos (Create)

### Insertar uno: `insertOne`

!!! success "🔍 Ejecutar y Analizar"
    Inserta una película con `insertOne`. Observa que el `id` se pasa como `null`: MongoDB asigna el `ObjectId` automáticamente.

```kotlin
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates

// Insertar una película (id=null → MongoDB genera el ObjectId)
val inception = Pelicula(
    id = null,
    titulo = "Inception",
    genero = "Thriller",
    anio = 2010,
    nota = 9.0
)

val resultado = peliculas.insertOne(inception)
println("ID insertado: ${resultado.insertedId}")
```

!!! success "🔍 Salida esperada"
    ```
    ID insertado: BsonObjectId{value=507f1f77bcf86cd799439011}
    ```

### Insertar varios: `insertMany`

!!! success "🔍 Ejecutar y Analizar"
    Inserta una lista de películas de una sola vez con `insertMany`.

```kotlin
val catalogo = listOf(
    Pelicula(null, "The Dark Knight",   "Acción",  2008, 9.0),
    Pelicula(null, "Interstellar",       "Sci-Fi",  2014, 8.6),
    Pelicula(null, "El Padrino",         "Drama",   1972, 9.2),
    Pelicula(null, "Pulp Fiction",       "Thriller",1994, 8.9),
    Pelicula(null, "Parásitos",          "Drama",   2019, 8.5)
)

val resInsert = peliculas.insertMany(catalogo)
println("Insertadas: ${resInsert.insertedIds.size} películas")
```

!!! success "🔍 Salida esperada"
    ```
    Insertadas: 5 películas
    ```

## 3 Leer Documentos (Read)

### Leer uno: `find` + `firstOrNull`

!!! success "🔍 Ejecutar y Analizar"
    Usa `Filters.eq` para buscar por un campo concreto. Compara con el `WHERE` de SQL.

```kotlin
import com.mongodb.client.model.Filters.eq

// Equivale a: SELECT * FROM peliculas WHERE titulo = 'Inception' LIMIT 1
val pelicula = peliculas.find(eq("titulo", "Inception")).firstOrNull()

if (pelicula != null) {
    println("Encontrada: ${pelicula.titulo} (${pelicula.anio}) — nota: ${pelicula.nota}")
} else {
    println("Película no encontrada")
}
```

!!! success "🔍 Salida esperada"
    ```
    Encontrada: Inception (2010) — nota: 9.0
    ```

### Leer todos: `find` sin filtro

!!! success "🔍 Ejecutar y Analizar"
    Sin parámetros, `find()` devuelve todos los documentos de la colección. El resultado es iterable.

```kotlin
// Equivale a: SELECT * FROM peliculas
println("=== Catálogo completo ===")
peliculas.find().forEach { p ->
    println("  [${p.anio}] ${p.titulo} — ${p.genero} — ★ ${p.nota}")
}
```

!!! success "🔍 Salida esperada"
    ```
    === Catálogo completo ===
      [2010] Inception — Thriller — ★ 9.0
      [2008] The Dark Knight — Acción — ★ 9.0
      [2014] Interstellar — Sci-Fi — ★ 8.6
      [1972] El Padrino — Drama — ★ 9.2
      [1994] Pulp Fiction — Thriller — ★ 8.9
      [2019] Parásitos — Drama — ★ 8.5
    ```

## 4 Actualizar Documentos (Update)

### Actualizar uno: `updateOne`

!!! success "🔍 Ejecutar y Analizar"
    Usa `Updates.set` para modificar un campo concreto. El primer parámetro es el filtro (*qué documentos*); el segundo es la modificación (*qué hacer*).

```kotlin
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Updates.set

// Equivale a: UPDATE peliculas SET nota = 9.3 WHERE titulo = 'Inception'
val resUpdate = peliculas.updateOne(
    eq("titulo", "Inception"),
    set("nota", 9.3)
)

println("Documentos modificados: ${resUpdate.modifiedCount}")

// Verificamos el resultado
val actualizada = peliculas.find(eq("titulo", "Inception")).firstOrNull()
println("Nueva nota de Inception: ${actualizada?.nota}")
```

!!! success "🔍 Salida esperada"
    ```
    Documentos modificados: 1
    Nueva nota de Inception: 9.3
    ```

!!! info "Operadores de actualización más comunes"

    | Operador | Descripción | Ejemplo |
    |---|---|---|
    | `Updates.set("campo", valor)` | Establece el valor de un campo | `set("nota", 9.5)` |
    | `Updates.inc("campo", cantidad)` | Incrementa un campo numérico | `inc("stock", 1)` |
    | `Updates.unset("campo")` | Elimina un campo del documento | `unset("descatalogada")` |
    | `Updates.combine(...)` | Combina varios operadores | `combine(set("nota",9.5), inc("votos",1))` |

## 5 Eliminar Documentos (Delete)

### Eliminar uno: `deleteOne`

!!! success "🔍 Ejecutar y Analizar"
    `deleteOne` elimina el **primer** documento que cumpla el filtro.

```kotlin
import com.mongodb.client.model.Filters.eq

// Equivale a: DELETE FROM peliculas WHERE titulo = 'Parásitos' LIMIT 1
val resDelete = peliculas.deleteOne(eq("titulo", "Parásitos"))
println("Documentos eliminados: ${resDelete.deletedCount}")
```

!!! success "🔍 Salida esperada"
    ```
    Documentos eliminados: 1
    ```

!!! warning "¿`deleteOne` o `deleteMany`?"
    - `deleteOne` elimina **como máximo un** documento (el primero que encuentre).
    - `deleteMany` elimina **todos** los documentos que cumplan el filtro.
    - Para borrar **toda** la colección usa `peliculas.drop()`.

## 6 Programa Completo de Ejemplo

!!! success "🔍 Ejecutar y Analizar"
    Aquí tienes un programa completo que integra todas las operaciones CRUD. Ejecútalo paso a paso y observa cómo evoluciona la colección.

```kotlin
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Updates.set
import com.mongodb.kotlin.client.MongoClient

fun main() {
    val client = MongoClient.create("mongodb://localhost:27017")
    val db = client.getDatabase("tienda_peliculas")
    val peliculas = db.getCollection<Pelicula>("peliculas")

    // Limpiar colección al inicio (para poder relanzar el programa)
    peliculas.drop()

    // CREATE — insertMany
    peliculas.insertMany(listOf(
        Pelicula(null, "Inception",       "Thriller", 2010, 9.0),
        Pelicula(null, "The Dark Knight", "Acción",   2008, 9.0),
        Pelicula(null, "Interstellar",    "Sci-Fi",   2014, 8.6),
        Pelicula(null, "El Padrino",      "Drama",    1972, 9.2)
    ))
    println("Insertadas ${peliculas.countDocuments()} películas")

    // READ — buscar una
    val p = peliculas.find(eq("titulo", "Inception")).firstOrNull()
    println("Buscada: ${p?.titulo} ★${p?.nota}")

    // UPDATE — modificar nota
    peliculas.updateOne(eq("titulo", "Inception"), set("nota", 9.3))
    val actualizada = peliculas.find(eq("titulo", "Inception")).firstOrNull()
    println("Nota actualizada: ${actualizada?.nota}")

    // DELETE — eliminar
    peliculas.deleteOne(eq("titulo", "The Dark Knight"))
    println("Quedan ${peliculas.countDocuments()} películas")

    // READ ALL — listar las que quedan
    println("\nCatálogo final:")
    peliculas.find().forEach { println("  ${it.titulo} (${it.anio}) ★${it.nota}") }

    client.close()
}
```

!!! success "🔍 Salida esperada"
    ```
    Insertadas 4 películas
    Buscada: Inception ★9.0
    Nota actualizada: 9.3
    Quedan 3 películas

    Catálogo final:
      Inception (2010) ★9.3
      Interstellar (2014) ★8.6
      El Padrino (1972) ★9.2
    ```

---

## 🎯 Práctica 2. CRUD sobre el Catálogo de Películas

!!! warning "🎯 Práctica 2. CRUD sobre el Catálogo de Películas"

    Partiendo del programa completo anterior, amplíalo para que:

    1. Inserte al menos **8 películas** de tu elección (géneros variados: Acción, Drama, Comedia, Sci-Fi...).
    2. Muestre **todos los documentos** de la colección tras la inserción.
    3. **Actualice la nota** de al menos dos películas usando `updateOne`.
    4. **Elimine** al menos una película con `deleteOne`.
    5. Muestre el catálogo **final** con las películas restantes.

    Asegúrate de que el programa sea **idempotente**: si lo lanzas varias veces, el resultado debe ser el mismo. Pista: usa `peliculas.drop()` al inicio.

---

!!! example "Autoevaluación"

    **Pregunta 5: Al llamar a `peliculas.insertOne(pelicula)` con `id = null`, ¿qué ocurre con el campo `_id` en el documento almacenado en MongoDB?**

    A) MongoDB rechaza el documento porque `_id` no puede ser nulo.

    B) MongoDB almacena el documento sin campo `_id`, lo que puede provocar duplicados.

    C) MongoDB genera automáticamente un `ObjectId` único y lo asigna al campo `_id` del documento insertado.

    D) El driver Kotlin convierte `null` en `0` y lo usa como `_id`.

    ??? quote "Solución"
        ❌ A) MongoDB no rechaza documentos con `_id` null en el sentido de que el driver interpreta `null` como "déjame generar el `_id`". Si explícitamente pasas `_id: null` en el BSON, sí lo almacena como null, pero el patrón con el driver Kotlin (campo nullable + `@SerialName("_id")`) indica al serializador que omita el campo cuando es null, permitiendo la generación automática.

        ❌ B) MongoDB siempre garantiza que cada documento tenga `_id`. Si no se proporciona, lo genera automáticamente.

        ✅ C) Este es el comportamiento estándar de MongoDB. Cuando el campo `_id` no está presente en el documento insertado, MongoDB genera automáticamente un `ObjectId` único (12 bytes: 4 de timestamp, 5 de identificador de host, 3 de contador). El resultado del `insertOne` contiene el `insertedId` generado.

        ❌ D) El driver no convierte `null` en `0`. Los enteros y los `ObjectId` son tipos BSON distintos e incompatibles.

    **Pregunta 6: ¿Qué diferencia hay entre `updateOne` y `updateMany` en MongoDB?**

    A) `updateOne` solo puede actualizar campos de texto; `updateMany` actualiza cualquier tipo de campo.

    B) `updateOne` actualiza el primer documento que coincide con el filtro; `updateMany` actualiza todos los documentos que coinciden.

    C) `updateOne` modifica el documento y devuelve el documento original; `updateMany` modifica y devuelve el documento actualizado.

    D) No hay diferencia: ambos actualizan todos los documentos que coincidan con el filtro.

    ??? quote "Solución"
        ❌ A) Ambas operaciones pueden actualizar cualquier tipo de campo (String, Int, Double, Array, etc.). La distinción no es por tipo de dato.

        ✅ B) Esta es la diferencia fundamental. `updateOne` para en el primer documento que coincide con el filtro y lo modifica. `updateMany` recorre toda la colección y actualiza todos los documentos que cumplan el filtro. Usar `updateOne` cuando quieres modificar un único registro (p. ej., por `_id`) y `updateMany` cuando quieres aplicar un cambio masivo.

        ❌ C) Tanto `updateOne` como `updateMany` devuelven un `UpdateResult` con estadísticas (`matchedCount`, `modifiedCount`), no los documentos modificados. Si necesitas el documento actualizado, usa `findOneAndUpdate` con la opción `ReturnDocument.AFTER`.

        ❌ D) Esta descripción corresponde a `updateMany`, no a `updateOne`.
