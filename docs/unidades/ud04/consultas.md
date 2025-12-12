# Consultas Avanzadas con Exposed DSL

El DSL de Exposed nos permite construir consultas `SELECT` complejas utilizando operadores de Kotlin en lugar de concatenar cadenas SQL, lo cual mejora la legibilidad y previene los riesgos de inyección SQL.

## 1. Consultas con Condiciones (`WHERE`)

Para filtrar datos, utilizamos los operadores de Exposed en un bloque `select` o `selectWhere`.

### 1.1. Operadores Lógicos Básicos

Exposed proporciona operadores idiomáticos que reemplazan a los operadores SQL:

| Operador Exposed | Equivalente SQL | Significado |
| :--- | :--- | :--- |
| `eq()` | `=` | Igual a |
| `neq()` | `!=` | Diferente de |
| `less()` / `lessEq()` | `<` / `<=` | Menor que / Menor o igual |
| `greater()` / `greaterEq()` | `>` / `>=` | Mayor que / Mayor o igual |
| `like()` | `LIKE` | Búsqueda de patrones (ej: `%Atenea%`) |

### 1.2. Ejemplo: Usuarios Mayores de Cierta Edad

Vamos a crear una función que busque usuarios que hayan nacido antes de un año específico (por ejemplo, mayores de 25 años).

```kotlin
fun obtenerUsuariosMayoresQue(fechaLimite: LocalDate): List<UsuarioDTO> {

    return transaction(ConexionDB.db) {

        // WHERE fecha_nacimiento < 'fechaLimite'
        Usuarios.selectAll().where { Usuarios.fechaNacimiento less fechaLimite }
            .map { resultRow ->
                // Mapeamos el resultado a nuestro DTO
                UsuarioDTO(
                    id = resultRow[Usuarios.id],
                    nombre = resultRow[Usuarios.nombre],
                    mail = resultRow[Usuarios.mail],
                    fechaNacimiento = resultRow[Usuarios.fechaNacimiento]
                )
            }.toList()
    }
}
```

!!! success "🔍 Ejecutar y Analizar"
    Crea un archivo `Consultas.kt` con las consultas que veamos en esta sección. Llama a la función `obtenerUsuariosMayoresQue` desde tu `main` para obtener datos de la tabla.

```kotlin
fun main() {

    ConexionDB.conectar()

    val fechaLimite = LocalDate.of(2001, 1, 1)
    val consulta = obtenerUsuariosMayoresQue(fechaLimite)

    consulta.forEach { usuario ->
        println("ID: ${usuario.id} | Nombre: ${usuario.nombre} | Email: ${usuario.mail}")
    }


}
```

## 2. Combinación de Condiciones (`AND`, `OR`)

Podemos combinar múltiples condiciones utilizando los operadores de Kotlin **`and`** y **`or`** entre las expresiones.

### 2.1. Ejemplo: Usuarios con Nombre y Edad

Buscamos usuarios cuyo nombre empiece por 'I' **Y** que hayan nacido después del año 2000.

```kotlin
fun buscarUsuariosPorFiltro(): List<UsuarioDTO> {
    val limiteNacimiento = LocalDate.of(2000, 1, 1)

    return transaction(ConexionDB.db) {

        // WHERE nombre LIKE 'I%' AND fecha_nacimiento > '2000-01-01'
        Usuarios.selectAll()
            .where { (Usuarios.nombre like "A%") and (Usuarios.fechaNacimiento greater limiteNacimiento) }
            .map { resultRow ->
            // ... (Mapeo a UsuarioDTO)
            UsuarioDTO(
                id = resultRow[Usuarios.id],
                nombre = resultRow[Usuarios.nombre],
                mail = resultRow[Usuarios.mail],
                fechaNacimiento = resultRow[Usuarios.fechaNacimiento]
            )
        }.toList()
    }
}
```

!!! success "🔍 Ejecutar y Analizar"
    Añade la consulta a tu `main`.

```kotlin
    val consultaFiltro = buscarUsuariosPorFiltro()

    consultaFiltro.forEach { usuario ->
        println("ID: ${usuario.id} | Nombre: ${usuario.nombre} | Email: ${usuario.mail}")
    }
```

## 3. Consultas con Ordenación (`ORDER BY`)

Para ordenar los resultados, utilizamos el método `.orderBy()` después de la selección.

### 3.1. Ejemplo: Listar por Antigüedad

Listamos todos los usuarios ordenados por su fecha de nacimiento (los más jóvenes primero).

```kotlin
fun obtenerUsuariosOrdenadosPorNacimiento(): List<UsuarioDTO> {
    
    return transaction(ConexionDB.db) {
        // CE f: Consulta con ordenación.
        // SELECT * FROM usuarios ORDER BY fecha_nacimiento DESC
        Usuarios.selectAll()
            .orderBy(Usuarios.fechaNacimiento, SortOrder.DESC) // DESC: del más reciente al más antiguo
            .map { resultRow -> 
                // ... (Mapeo a UsuarioDTO)
                UsuarioDTO(
                    id = resultRow[Usuarios.id],
                    nombre = resultRow[Usuarios.nombre],
                    mail = resultRow[Usuarios.mail],
                    fechaNacimiento = resultRow[Usuarios.fechaNacimiento]
                )
            }.toList()
    }
}
```

!!! success "🔍 Ejecutar y Analizar"
    Añade la consulta a tu `main`.

```kotlin
    val consultaOrdenacion = obtenerUsuariosOrdenadosPorNacimiento()

    consultaOrdenacion.forEach { usuario ->
        println("ID: ${usuario.id} | Nombre: ${usuario.nombre} | Email: ${usuario.mail}")
    }
```

## 🎯 Práctica 4. Consultas Avanzadas

Una vez que domines los filtros y la ordenación, estarás listo para realizar cualquier consulta de datos en Exposed.

!!! warning "🎯 Práctica 4. Consultas Avanzadas"

1. **Filtro Compuesto:** Crea una función que filtre tu entidad (`Producto`, `Libro`, etc.) utilizando una combinación de `AND` y `OR` (ej: `Stock > 10 AND (Nombre LIKE 'A%' OR Precio < 50)`).
2. **Consulta Ordenada:** Crea una función que obtenga los 5 registros más recientes o más caros, utilizando `.orderBy()` y el método `.limit(5)`.
3. **Verificación:** Ejecuta las consultas y verifica que el listado retornado coincida exactamente con lo esperado de la base de datos.
