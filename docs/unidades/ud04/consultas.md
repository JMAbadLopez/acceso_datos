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

!!! success "🔍 Ejecutar y Analizar: Filtro por fecha"
    Crea un archivo `Consultas.kt` con las consultas que veamos en esta sección. Llama a la función `obtenerUsuariosMayoresQue` desde tu `main` para obtener datos de la tabla. Con los datos insertados en la práctica anterior (Atenea: 1999, Hera: 2004, Iris: 2005), solo Atenea nació antes de 2001-01-01. La salida debe ser:

    ```
    Conexión a MySQL establecida con éxito usando Exposed.
    Esquema de la tabla 'Usuarios' verificado/creado.
    ID: 1 | Nombre: Atenea | Email: atenea@mail.com
    ```

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

!!! success "🔍 Ejecutar y Analizar: Filtro compuesto AND"
    Añade la consulta a tu `main`. Esta consulta busca usuarios cuyo nombre empieza por `A` **y** nacieron después de 2000-01-01. Con los datos de ejemplo, Atenea empieza por `A` pero nació en 1999 (no cumple la condición de fecha). El resultado esperado es una lista vacía:

    ```
    (sin resultados — ningún usuario cumple ambas condiciones a la vez)
    ```

    ¡Esto es correcto! Cambia el nombre en la inserción de Atenea a "Ariadna" con fecha 2003-06-01 y vuelve a ejecutar para ver un resultado.

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

!!! success "🔍 Ejecutar y Analizar: Ordenación"
    Añade la consulta a tu `main`. El orden es descendente por `fecha_nacimiento` (los más jóvenes primero). Con los datos de ejemplo, la salida esperada es:

    ```
    ID: 3 | Nombre: Iris | Email: iris@mail.com
    ID: 2 | Nombre: Hera | Email: hera@mail.com
    ID: 1 | Nombre: Atenea | Email: atenea@mail.com
    ```

```kotlin
    val consultaOrdenacion = obtenerUsuariosOrdenadosPorNacimiento()

    consultaOrdenacion.forEach { usuario ->
        println("ID: ${usuario.id} | Nombre: ${usuario.nombre} | Email: ${usuario.mail}")
    }
```

!!! example "Autoevaluación"

    **Pregunta 7: ¿Qué operador de Exposed DSL equivale al `LIKE` de SQL y para qué se usa?**

    A) `contains()` — comprueba si un campo contiene una subcadena en cualquier posición.

    B) `like()` — busca patrones con comodines (`%` para cualquier cadena, `_` para un carácter), igual que `LIKE` en SQL.

    C) `matches()` — ejecuta una expresión regular directamente en MySQL.

    D) `eq()` — compara si dos valores son exactamente iguales, incluyendo patrones con comodines.

    ??? quote "Solución"
        ❌ A) `contains` no existe como operador de Exposed DSL. Para buscar subcadenas en SQL se usa `LIKE '%texto%'`, que en Exposed se escribe como `columna like "%texto%"`.

        ✅ B) `like()` es el equivalente directo de `LIKE` en el DSL de Exposed. Acepta los mismos comodines SQL: `%` para cero o más caracteres y `_` para exactamente un carácter. Ejemplo: `Usuarios.nombre like "A%"` genera `WHERE nombre LIKE 'A%'`.

        ❌ C) Exposed no expone un operador `matches()` para expresiones regulares en su DSL estándar. Las regex se pueden ejecutar mediante SQL nativo, pero no con el DSL tipado.

        ❌ D) `eq()` comprueba igualdad exacta (`=`). No interpreta comodines: `Usuarios.nombre eq "A%"` buscaría literalmente el texto `"A%"`, no nombres que empiezan por A.

    **Pregunta 8: Al encadenar `.orderBy(Usuarios.fechaNacimiento, SortOrder.DESC)`, ¿qué devuelve primero la consulta?**

    A) El usuario con la fecha de nacimiento más antigua (el mayor de edad).

    B) El usuario con la fecha de nacimiento más reciente (el más joven).

    C) Los usuarios en orden aleatorio, porque `DESC` significa "desordenado" (*descartado*).

    D) Una excepción, porque `orderBy` no acepta columnas de tipo `Date` en Exposed.

    ??? quote "Solución"
        ❌ A) Eso sería `SortOrder.ASC` (ascendente). La fecha más antigua en orden ascendente aparecería primero.

        ✅ B) `DESC` (descendente) ordena de mayor a menor. Las fechas más recientes tienen un valor numérico mayor, por lo que aparecen primero. Con los usuarios del ejemplo: Iris (2005) → Hera (2004) → Atenea (1999).

        ❌ C) `DESC` es la abreviatura de *descending* (descendente), no "descartado". Es un término SQL estándar.

        ❌ D) Exposed soporta `.orderBy` con columnas de cualquier tipo comparable, incluyendo fechas (`LocalDate`), siempre que se haya importado `exposed-java-time`.

## 🎯 Práctica 4. Consultas Avanzadas

Añade las consultas avanzadas al `VideojuegoDAO.kt` del **Catálogo de Videojuegos**.

!!! warning "🎯 Práctica 4. Consultas Avanzadas"

    Implementa las siguientes funciones en `VideojuegoDAO.kt`:

    1. **`fun filtrarPorGenero(genero: String): List<Videojuego>`**
       Devuelve todos los videojuegos cuyo género coincida exactamente con el parámetro (usa `eq`).
       Ejemplo: `filtrarPorGenero("Roguelike")` debe devolver únicamente *Hades*.

    2. **`fun buscarPorTitulo(fragmento: String): List<Videojuego>`**
       Devuelve los videojuegos cuyo título contenga el fragmento indicado (usa `like` con `%`).
       Ejemplo: `buscarPorTitulo("Red")` debe devolver *Red Dead Redemption 2*.

    3. **`fun obtenerTop3PorNota(): List<Videojuego>`**
       Devuelve los 3 videojuegos mejor valorados, ordenados de mayor a menor nota (usa `.orderBy()` con `SortOrder.DESC` y `.limit(3)`).
       Con los datos del proyecto, el resultado esperado es: *Red Dead Redemption 2 (9.5)*, *Zelda: BOTW (9.3)*, *Hades (9.0)* / *Celeste (9.0)*.

    4. **`fun obtenerVideojuegosDesdeAnio(anio: Int): List<Videojuego>`**
       Devuelve todos los videojuegos publicados en el año indicado o posterior (usa `greaterEq`).
       Ejemplo: `obtenerVideojuegosDesdeAnio(2018)` debe devolver *RDR2 (2018)*, *Hades (2020)* y *Celeste (2018)*.

    5. Llama a todas estas funciones desde `Main.kt` y verifica que los resultados son los esperados.

    !!! danger "Reto extra"
        Implementa `fun obtenerVideojuegosConFiltroCompuesto(genero: String, notaMinima: Double)` que combine `genero eq` y `nota greaterEq` con el operador `and`.

