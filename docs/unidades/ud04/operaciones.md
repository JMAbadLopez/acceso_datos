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

!!! success "🔍 Ejecutar y Analizar"
    Crea un archivo `Operaciones.kt` con las operaciones CRUD. Llama a la función `insertarUsuario` desde tu `main` para añadir datos a la tabla.

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

!!! success "🔍 Ejecutar y Analizar"
    Añade esta llamada al `main`. Recuerda **comentar las inserciones**, pues dará un error porque hemos definido el `mail` como un campo único en la BD.

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

!!! success "🔍 Ejecutar y Analizar"
    Añade esta llamada al `main` después del listado. Luego comprueba si ha cambiado en la **Base de Datos**.

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

!!! success "🔍 Ejecutar y Analizar"
    Añade esta llamada al `main` después de la actualización. Luego comprueba si se ha eliminado en la **Base de Datos**.

```kotlin
// ... (código en main)
actualizarMail(1, "atena@olympus.com")
// ...
```

---

## 🎯 Práctica 3. Implementación del CRUD

Hemos visto todas las operaciones básicas. Ahora es tu turno de aplicarlas:

!!! warning "🎯 Práctica 3. Implementación del CRUD"

1. Crea la clase de datos (`data class`) para tu entidad del proyecto (ej: `ProductoDTO`).
2. Crea e implementa las funciones **CRUD** completas para tu entidad:
    * `insertarProducto(...)` (INSERT)
    * `obtenerTodosLosProductos()` (SELECT ALL)
    * `actualizarStock(id, nuevoStock)` (UPDATE)
    * `eliminarProducto(id)` (DELETE)
3. Asegúrate de que **TODAS** las funciones que interactúan con la base de datos están envueltas en `transaction(ConexionDB.db) { ... }`.
4. Llama a estas funciones desde tu `main` para simular la gestión de tu aplicación.
