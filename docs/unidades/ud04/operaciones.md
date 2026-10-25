# CRUD Básico y Persistencia

En este apartado, aprenderemos a manipular los datos de nuestra tabla `Usuarios` utilizando el **DSL (Domain Specific Language)** de Kotlin Exposed.

## 1. El Concepto Central: La Transacción

Antes de realizar cualquier operación de base de datos (incluso una simple lectura), debemos asegurarnos de que el código se ejecuta dentro de un bloque **transaccional**. Exposed gestiona automáticamente el *commit* (guardar) o el *rollback* (deshacer) si ocurre un error.

```kotlin
import org.jetbrains.exposed.sql.transactions.transaction

// Todas las operaciones CRUD deben ir dentro de este bloque
transaction(ConexionDB.db) {
    // Aquí ejecutamos las inserciones, consultas o actualizaciones.
}
```

> **¡Importante\!** El argumento `ConexionDB.db` nos asegura que la transacción se ejecute en nuestra base de datos MySQL configurada.

## 2. Operación C: Creación (Insertar Registros)

La inserción es el primer mecanismo de persistencia. Utilizamos el método `insert` en nuestro objeto `Usuarios` y le pasamos un bloque *lambda* para definir los valores.

### 2.1 Código de Inserción

```kotlin
import Usuarios // Importamos el objeto de la tabla
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate

fun insertarUsuario(nombre: String, mail: String, passwordHash: String, fechaNac: LocalDate) {
    transaction(ConexionDB.db) {
        
        // 1. Ejecutamos la función insert en el objeto de la tabla
        val idGenerado = Usuarios.insert {
            // CE d: Aplicamos mecanismos de persistencia a los objetos
            it[Usuarios.nombre] = nombre
            it[Usuarios.mail] = mail
            it[Usuarios.password] = passwordHash
            it[Usuarios.fechaNacimiento] = fechaNac // Uso del LocalDate
        } get Usuarios.id // Capturamos el ID que genera MySQL (AutoIncrement)
        
        println("Usuario '$nombre' insertado con ID: $idGenerado")
    }
}
```

### 2.2 Uso Práctico

!!! success "🔍 Ejecutar y Analizar: Inserción de registros"
    Crea un archivo `Operaciones.kt` con las operaciones CRUD. Llama a la función `insertarUsuario` desde tu `main` para añadir datos a la tabla. Verifica que la salida por consola es:

    ```
    Conexión a MySQL establecida con éxito usando Exposed.
    Esquema de la tabla 'Usuarios' verificado/creado.
    Usuario 'Atenea' insertado con ID: 1
    Usuario 'Hera' insertado con ID: 2
    Usuario 'Iris' insertado con ID: 3
    ```

    Comprueba en la herramienta **Database** de IntelliJ que los tres registros aparecen en la tabla `usuarios`.

```kotlin
fun main() {
    ConexionDB.conectar()
    
    insertarUsuario("Atenea", "atenea@mail.com", "hash123", LocalDate.of(1999, 5, 20))
    insertarUsuario("Hera", "hera@mail.com", "hash456", LocalDate.of(2004, 10, 15))
    insertarUsuario("Iris", "iris@mail.com", "hash789", LocalDate.of(2005, 1, 1))
}
```

## 3. Operación R: Lectura (Recuperar Registros)

Para recuperar datos, usamos el método `selectAll()` y luego transformamos cada fila (`ResultRow`) devuelta por Exposed en un objeto Kotlin más utilizable.

### 3.1 Estructura de Datos para el Resultado

Primero, definimos una clase de datos simple para almacenar el resultado, manteniendo la coherencia objeto-relacional.

Cada objeto de la `data class UsuarioDTO` va a funcionar como un espacio en el que volquemos el resultado de cada **fila (row)** de nuestra tabla, mientras se recorre.

```kotlin
data class UsuarioDTO( // Data Transfer Object (DTO)
    val id: Int,
    val nombre: String,
    val mail: String,
    val fechaNacimiento: LocalDate
)
```

### 3.2 Código de Lectura

```kotlin
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

fun obtenerTodosLosUsuarios(): List<UsuarioDTO> {
    
    return transaction(ConexionDB.db) {
        
        Usuarios.selectAll() // Seleccionamos todas las filas de la tabla
            .map { resultRow -> // Mapeamos cada fila a nuestro objeto UsuarioDTO
                UsuarioDTO(
                    id = resultRow[Usuarios.id],
                    nombre = resultRow[Usuarios.nombre],
                    mail = resultRow[Usuarios.mail],
                    // No recuperamos el password por seguridad
                    fechaNacimiento = resultRow[Usuarios.fechaNacimiento]
                )
            }.toList()
    }
}
```

### 3.3 Uso Práctico

!!! success "🔍 Ejecutar y Analizar: Lectura de registros"
    Añade esta llamada al `main`. Recuerda **comentar las inserciones**, pues dará un error porque hemos definido el `mail` como un campo único en la BD. Verifica que la salida por consola es:

    ```

    --- LISTADO DE USUARIOS ---
    ID: 1 | Nombre: Atenea | Email: atenea@mail.com
    ID: 2 | Nombre: Hera | Email: hera@mail.com
    ID: 3 | Nombre: Iris | Email: iris@mail.com
    ```

```kotlin
// ... (código en main)
println("\n--- LISTADO DE USUARIOS ---")
val listaUsuarios = obtenerTodosLosUsuarios()
listaUsuarios.forEach { usuario ->
    println("ID: ${usuario.id} | Nombre: ${usuario.nombre} | Email: ${usuario.mail}")
}
// ...
```

## 4. Operación U: Actualización (Modificar Registros)

La actualización requiere una **cláusula de condición** (`where`) para especificar qué registro modificar.

### 4.1 Código de Actualización

```kotlin
import Usuarios
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

fun actualizarMail(usuarioId: Int, nuevoMail: String) {
    transaction(ConexionDB.db) {

        // 1. Usamos la función update y le pasamos la condición 'where'
        val filasAfectadas = Usuarios.update({ Usuarios.id eq usuarioId }) {
            // CE e: Modificamos el objeto persistente
            it[Usuarios.mail] = nuevoMail
        }

        if (filasAfectadas > 0) {
            println("Usuario ID $usuarioId actualizado. Nuevo email: $nuevoMail")
        } else {
            println("No se encontró el usuario ID $usuarioId.")
        }
    }
}
```

### 4.2 Uso Práctico

!!! success "🔍 Ejecutar y Analizar: Actualización de registros"
    Añade esta llamada al `main` después del listado. Luego comprueba si ha cambiado en la **Base de Datos**. Verifica que la salida por consola es:

    ```
    Usuario ID 1 actualizado. Nuevo email: atena@olympus.com
    ```

```kotlin
// ... (código en main)
actualizarMail(1, "atena@olympus.com")
// ...
```

## 5. Operación D: Eliminación (Borrar Registros)

La eliminación, al igual que la actualización, necesita una condición estricta para saber qué registros borrar.

### 5.1 Código de Eliminación

```kotlin
import Usuarios
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

fun eliminarUsuario(usuarioId: Int) {
    transaction(ConexionDB.db) {
        
        // 1. Usamos la función deleteWhere y definimos la condición
        val filasAfectadas = Usuarios.deleteWhere { Usuarios.id eq usuarioId }
        
        if (filasAfectadas > 0) {
            println("Usuario ID $usuarioId ELIMINADO.")
        } else {
            println("No se encontró el usuario ID $usuarioId para eliminar.")
        }
    }
}
```

### 5.2 Uso Práctico

!!! success "🔍 Ejecutar y Analizar: Eliminación de registros"
    Añade esta llamada al `main` después de la actualización. Luego comprueba si se ha eliminado en la **Base de Datos**. Verifica que la salida por consola es:

    ```
    Usuario ID 3 ELIMINADO.
    ```

```kotlin
// ... (código en main)
eliminarUsuario(3)
// ...
```

---

!!! example "Autoevaluación"

    **Pregunta 5: Al insertar un registro con Exposed usando `Usuarios.insert { ... } get Usuarios.id`, ¿qué devuelve la expresión `get Usuarios.id`?**

    A) El número total de filas que contiene la tabla después de la inserción.

    B) El valor del campo `id` del registro recién insertado, que MySQL ha generado de forma automática (AutoIncrement).

    C) Una referencia al objeto `Usuarios.id` que puede usarse para consultas posteriores.

    D) Siempre devuelve `null` porque el `id` es autogenerado y Exposed no puede conocerlo hasta la siguiente consulta.

    ??? quote "Solución"
        ❌ A) El número de filas totales lo devuelve `Usuarios.selectAll().count()`. La expresión `get Usuarios.id` es específica del registro insertado.

        ✅ B) Exposed recibe el `LAST_INSERT_ID()` que devuelve MySQL tras un `INSERT` exitoso y lo expone mediante `.get(columna)`. Esto permite saber exactamente qué ID se asignó al registro nuevo sin hacer una consulta adicional.

        ❌ C) `Usuarios.id` como referencia a la columna existe independientemente de la inserción. Lo que `get Usuarios.id` devuelve aquí es el valor concreto del ID generado, no la referencia.

        ❌ D) Exposed sí puede conocer el ID autogenerado porque MySQL lo devuelve en la respuesta al `INSERT`. La expresión nunca devuelve `null` si la inserción fue exitosa.

    **Pregunta 6: ¿Cuál es la sintaxis correcta en Exposed DSL para actualizar el campo `mail` de un usuario cuyo `id` sea igual a 5?**

    A) `Usuarios.where { Usuarios.id == 5 }.update { it[Usuarios.mail] = "nuevo@mail.com" }`

    B) `Usuarios.update(Usuarios.id == 5) { it[Usuarios.mail] = "nuevo@mail.com" }`

    C) `Usuarios.update({ Usuarios.id eq 5 }) { it[Usuarios.mail] = "nuevo@mail.com" }`

    D) `Usuarios.set(Usuarios.mail, "nuevo@mail.com").where(Usuarios.id eq 5)`

    ??? quote "Solución"
        ❌ A) En Exposed no se encadena `.where` antes de `.update`. Además, el operador de igualdad de Kotlin `==` no es el operador de Exposed; se usa `eq`.

        ❌ B) La condición `where` en `update` va entre llaves como un lambda, no como argumento directo. Además, `==` debe ser `eq`.

        ✅ C) Esta es la sintaxis correcta del DSL de Exposed. El primer bloque lambda `({ Usuarios.id eq 5 })` define la condición WHERE; el segundo `{ it[Usuarios.mail] = "..." }` define los campos a modificar con sus nuevos valores.

        ❌ D) Exposed no tiene un método `.set` encadenable en este orden. La estructura siempre es `Tabla.update({ condicion }) { campos }`.

## 🎯 Práctica 3. Implementación del CRUD

Hemos visto todas las operaciones básicas. Ahora aplícalas al **Catálogo de Videojuegos**.

!!! warning "🎯 Práctica 3. Implementación del CRUD"

    Crea el fichero `VideojuegoDAO.kt` con el CRUD completo para la tabla `videojuegos`.

    1. Define la `data class Videojuego(val id: Int, val titulo: String, val genero: String, val anio: Int, val nota: Double)`.
    2. Implementa las cuatro funciones CRUD:
        * `fun insertarVideojuego(titulo: String, genero: String, anio: Int, nota: Double): Int` → devuelve el ID generado.
        * `fun obtenerTodosLosVideojuegos(): List<Videojuego>` → SELECT ALL mapeado a `Videojuego`.
        * `fun actualizarNota(id: Int, nuevaNota: Double)` → UPDATE de la columna `nota`.
        * `fun eliminarVideojuego(id: Int)` → DELETE por ID.
    3. Asegúrate de que **todas** las funciones están dentro de `transaction(ConexionDB.db) { ... }`.
    4. En `Main.kt`, llama a las funciones para simular la gestión del catálogo:
        - Inserta los 5 videojuegos del proyecto: *Zelda: Breath of the Wild*, *Red Dead Redemption 2*, *Hades*, *Hollow Knight* y *Celeste*.
        - Lista todos los videojuegos.
        - Actualiza la nota de uno de ellos.
        - Elimina uno y vuelve a listar para confirmar el borrado.

    !!! danger "Reto extra"
        Implementa también `ListaDAO.kt` con el CRUD completo para la tabla `listas`.

