# Consultas Avanzadas con Filtros

En el apartado anterior usamos `Filters.eq` para buscar por igualdad exacta. MongoDB ofrece un conjunto rico de filtros y opciones de consulta que permiten construir búsquedas mucho más expresivas.

## 1 Filtros Básicos

Todos los filtros se importan desde `com.mongodb.client.model.Filters`.

!!! success "🔍 Ejecutar y Analizar"
    Antes de ejecutar estos ejemplos, asegúrate de tener la colección con los datos de la Práctica 2 (o inserta de nuevo el catálogo de 8+ películas).

### Comparación numérica

```kotlin
import com.mongodb.client.model.Filters.*

// Películas con nota mayor o igual a 9.0
// Equivale a: SELECT * FROM peliculas WHERE nota >= 9.0
println("=== Nota ≥ 9.0 ===")
peliculas.find(gte("nota", 9.0)).forEach {
    println("  ${it.titulo} — ★${it.nota}")
}

// Películas anteriores a 2000
// Equivale a: SELECT * FROM peliculas WHERE anio < 2000
println("\n=== Anteriores a 2000 ===")
peliculas.find(lt("anio", 2000)).forEach {
    println("  ${it.titulo} (${it.anio})")
}
```

!!! success "🔍 Salida esperada (con el catálogo del ejemplo anterior)"
    ```
    === Nota ≥ 9.0 ===
      Inception — ★9.3
      El Padrino — ★9.2

    === Anteriores a 2000 ===
      El Padrino (1972)
      Pulp Fiction (1994)
    ```

### Tabla de operadores de comparación

| Filtro | SQL equivalente | Descripción |
|---|---|---|
| `eq("campo", valor)` | `WHERE campo = valor` | Igualdad exacta |
| `ne("campo", valor)` | `WHERE campo != valor` | Distinto de |
| `gt("campo", valor)` | `WHERE campo > valor` | Mayor que |
| `gte("campo", valor)` | `WHERE campo >= valor` | Mayor o igual |
| `lt("campo", valor)` | `WHERE campo < valor` | Menor que |
| `lte("campo", valor)` | `WHERE campo <= valor` | Menor o igual |
| `in("campo", lista)` | `WHERE campo IN (...)` | Está en la lista |
| `nin("campo", lista)` | `WHERE campo NOT IN (...)` | No está en la lista |

## 2 Filtros Lógicos: `and` y `or`

Los filtros se pueden combinar con `and` y `or`, igual que en SQL.

!!! success "🔍 Ejecutar y Analizar"
    Busca películas del género Thriller con nota superior a 8.5.

```kotlin
import com.mongodb.client.model.Filters.*

// Equivale a: SELECT * FROM peliculas WHERE genero = 'Thriller' AND nota > 8.5
println("=== Thriller con nota > 8.5 ===")
peliculas.find(
    and(
        eq("genero", "Thriller"),
        gt("nota", 8.5)
    )
).forEach {
    println("  ${it.titulo} ★${it.nota}")
}

// Drama o Sci-Fi
println("\n=== Drama o Sci-Fi ===")
peliculas.find(
    or(
        eq("genero", "Drama"),
        eq("genero", "Sci-Fi")
    )
).forEach {
    println("  ${it.titulo} — ${it.genero}")
}
```

!!! success "🔍 Salida esperada"
    ```
    === Thriller con nota > 8.5 ===
      Inception ★9.3
      Pulp Fiction ★8.9

    === Drama o Sci-Fi ===
      Interstellar — Sci-Fi
      El Padrino — Drama
    ```

## 3 Ordenar y Limitar Resultados

### Ordenar: `sort`

!!! success "🔍 Ejecutar y Analizar"
    Usa `Sorts.descending` o `Sorts.ascending` para ordenar los resultados.

```kotlin
import com.mongodb.client.model.Sorts

// Las 3 películas con mayor nota (TOP 3)
// Equivale a: SELECT * FROM peliculas ORDER BY nota DESC LIMIT 3
println("=== TOP 3 por nota ===")
peliculas.find()
    .sort(Sorts.descending("nota"))
    .limit(3)
    .forEach {
        println("  ${it.titulo} ★${it.nota}")
    }

// Películas ordenadas por año ascendente
println("\n=== Por año (ASC) ===")
peliculas.find()
    .sort(Sorts.ascending("anio"))
    .forEach {
        println("  ${it.anio} — ${it.titulo}")
    }
```

!!! success "🔍 Salida esperada"
    ```
    === TOP 3 por nota ===
      El Padrino ★9.2
      Inception ★9.3
      Pulp Fiction ★8.9

    === Por año (ASC) ===
      1972 — El Padrino
      1994 — Pulp Fiction
      2010 — Inception
      2014 — Interstellar
    ```

### Contar documentos: `countDocuments`

```kotlin
// Total de películas en la colección
val total = peliculas.countDocuments()
println("Total películas: $total")

// Solo las de Drama
val dramas = peliculas.countDocuments(eq("genero", "Drama"))
println("Dramas: $dramas")
```

## 4 Proyecciones: Seleccionar Campos

Las **proyecciones** permiten indicar qué campos incluir o excluir en los resultados, equivalente al `SELECT campo1, campo2` de SQL.

!!! info "Proyecciones con data classes"
    Cuando se usa una data class tipada con `getCollection<Pelicula>`, MongoDB devuelve siempre el documento completo y el driver lo mapea. Para proyecciones parciales es más práctico usar `getCollection<Document>` y `Document` de BSON.

!!! success "🔍 Ejecutar y Analizar"
    Recupera solo el título y la nota de cada película (sin `_id`, `genero`, ni `anio`).

```kotlin
import com.mongodb.client.model.Projections
import org.bson.Document

// Usamos Document para proyecciones parciales
val colDoc = db.getCollection<Document>("peliculas")

println("=== Solo título y nota ===")
colDoc.find()
    .projection(
        Projections.fields(
            Projections.include("titulo", "nota"),
            Projections.excludeId()
        )
    )
    .forEach { doc ->
        println("  ${doc.getString("titulo")} ★${doc.getDouble("nota")}")
    }
```

!!! success "🔍 Salida esperada"
    ```
    === Solo título y nota ===
      Inception ★9.3
      Interstellar ★8.6
      El Padrino ★9.2
      Pulp Fiction ★8.9
    ```

## 5 Resumen: Comparativa SQL vs MQL

| Operación | SQL | MongoDB (Kotlin) |
|---|---|---|
| Seleccionar todo | `SELECT * FROM t` | `col.find()` |
| Filtrar | `WHERE campo = val` | `find(eq("campo", val))` |
| Ordenar | `ORDER BY campo DESC` | `.sort(Sorts.descending("campo"))` |
| Limitar | `LIMIT 5` | `.limit(5)` |
| Contar | `SELECT COUNT(*) FROM t` | `col.countDocuments()` |
| Insertar | `INSERT INTO t VALUES (...)` | `col.insertOne(obj)` |
| Actualizar | `UPDATE t SET c=v WHERE ...` | `col.updateOne(filtro, set("c",v))` |
| Eliminar | `DELETE FROM t WHERE ...` | `col.deleteOne(filtro)` |
| Campos concretos | `SELECT c1, c2 FROM t` | `.projection(include("c1","c2"))` |

---

## 🎯 Práctica 3. Consultas sobre el Catálogo

!!! warning "🎯 Práctica 3. Consultas sobre el Catálogo"

    Usando la colección de películas creada en la Práctica 2, escribe las siguientes consultas:

    1. **Todas las películas** ordenadas por nota de mayor a menor.
    2. **Las 5 mejor valoradas** (TOP 5 por nota descendente).
    3. **Películas de Drama** con nota mayor a 8.0.
    4. **Películas entre 2000 y 2020** (inclusive), ordenadas por año ascendente.
    5. **Número total de películas** del catálogo y desglosado por género (pista: ejecuta `countDocuments` con filtros independientes para cada género).
    6. (Reto) **Actualiza masivamente** todas las películas de un género concreto incrementando su nota en 0.1. Usa `updateMany` con `Updates.inc`.

---

!!! example "Autoevaluación"

    **Pregunta 7: ¿Cuál es el equivalente en MongoDB del `WHERE nota >= 8.5 AND genero = 'Drama'` de SQL?**

    A) `find(eq("nota", gte(8.5)), eq("genero", "Drama"))`

    B) `find(and(gte("nota", 8.5), eq("genero", "Drama")))`

    C) `find(where("nota >= 8.5").and("genero = 'Drama'"))`

    D) `find(filter("nota >= 8.5 AND genero = 'Drama'"))`

    ??? quote "Solución"
        ❌ A) `gte` no va dentro de `eq`. Los filtros de comparación son funciones independientes: `gte("nota", 8.5)` es un filtro completo por sí mismo.

        ✅ B) Esta es la sintaxis correcta del MongoDB Kotlin Sync Driver. `and(...)` combina múltiples filtros con la condición AND lógica. `gte("nota", 8.5)` equivale a `nota >= 8.5` y `eq("genero", "Drama")` equivale a `genero = 'Drama'`. Importando `com.mongodb.client.model.Filters.*` puedes usar estas funciones directamente.

        ❌ C) No existe un método `where(String)` en la API del driver Kotlin. Esa sintaxis no es válida.

        ❌ D) No existe un método `filter(String)` que acepte una cadena SQL. El driver usa la API de constructores de filtros (`Filters.*`), no cadenas de texto.

    **Pregunta 8: En MongoDB, ¿para qué sirve una proyección?**

    A) Para definir qué campos se incluyen o excluyen en los documentos devueltos por una consulta, reduciendo el volumen de datos transferidos.

    B) Para crear índices sobre los campos más consultados y mejorar el rendimiento de las búsquedas.

    C) Para ordenar los documentos resultantes por uno o varios campos.

    D) Para transformar los valores de los campos (p. ej. convertir un String a mayúsculas) antes de devolverlos.

    ??? quote "Solución"
        ✅ A) Las proyecciones controlan qué campos devuelve MongoDB en cada documento. Con `Projections.include("titulo", "nota")` solo recibes esos dos campos; con `Projections.excludeId()` excluyes el `_id`. Esto es equivalente al `SELECT campo1, campo2` de SQL y reduce significativamente el tráfico de red cuando los documentos son grandes.

        ❌ B) Los índices se crean con `createIndex(...)` y `IndexModel`. Son estructuras independientes de las proyecciones.

        ❌ C) El ordenamiento se controla con `.sort(Sorts.ascending(...))` o `.sort(Sorts.descending(...))`, no con proyecciones.

        ❌ D) Las transformaciones de valores se realizan con el framework de Agregación (`aggregate`), no con proyecciones básicas. Las proyecciones solo seleccionan o excluyen campos, no transforman sus valores.
