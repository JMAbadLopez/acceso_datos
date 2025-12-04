# Creación de esquemas de BD

## La Clase `Table`: El Mapeo Lógico

Vamos a comenzar a usar nuestro **ORM** con la definición del esquema de la tabla `Usuarios` con los campos siguientes (`id`, `nombre`, `mail`, `password`, `fecha_nacimiento`).

### Tipos de Columna Comunes

Exposed facilita la definición de columnas con métodos que reflejan tipos SQL, pero con el tipado fuerte de Kotlin:

| Tipo Exposed | Tipo SQL (MySQL) | Tipo Kotlin | Notas |
| :--- | :--- | :--- | :--- |
| `integer(name)` | `INT` | `Int` | Para números enteros. |
| `long(name)` | `BIGINT` | `Long` | Para números muy grandes (IDs de mucha concurrencia). |
| `varchar(name, length)`| `VARCHAR` | `String` | Cadenas de texto de longitud limitada. |
| `text(name)` | `TEXT` | `String` | Cadenas de texto largas sin límite fijo. |
| `bool(name)` | `BOOLEAN` | `Boolean` | Valores true/false. |
| `double(name)` | `DOUBLE` | `Double` | Números con coma flotante. |
| `date(name)` | `DATE` | `java.time.LocalDate` | Requiere la importación `exposed-jdbc`. |

En Exposed, el **fichero de mapeo** se define como un `object` que hereda de la clase `org.jetbrains.exposed.sql.Table`. Cada propiedad dentro de este objeto se convierte en una **columna** en la tabla de MySQL.

> **Modularidad:** Separar estas definiciones en archivos específicos (`Usuarios.kt`, `Productos.kt`, etc.) es la mejor práctica para mantener el proyecto organizado y escalable. Además, podemos incluirlas dentro de un *package*, por ejemplo, **schemas**.

### Definiendo la Tabla `Usuarios`

Creamos el archivo `Usuarios.kt` que define el mapeo de la tabla `usuarios` con los campos solicitados, especificando los tipos de datos de Exposed.

| Campo SQL | Tipo de Dato en Exposed | Tipo de Dato en Kotlin | Restricciones |
| :--- | :--- | :--- | :--- |
| `id` | `integer` | `Int` | Clave Primaria, Auto-Incrementable. |
| `nombre` | `varchar(50)` | `String` | No nulo. |
| `mail` | `varchar(100)` | `String` | No nulo, **Único**. |
| `password` | `varchar(255)` | `String` | No nulo. |
| `fecha_nacimiento` | `date` | `LocalDate` | No nulo. |

!!! success "🔍 Ejecutar y Analizar"
    Sigue los pasos para **definir** y **mapear** nuestra tabla de `Usuarios`.

Y aquí tenemos el mapeo `Usuarios.kt`:

```kotlin
package es.gva.edu.schema

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.javatime.date

/**
 * Define la tabla 'usuarios' en la base de datos (CE c: Fichero de mapeo).
 * Hereda de [Table] de Exposed. Cada propiedad es una columna.
 */
object Usuarios : Table("usuarios") {

    // 1. ID: Clave Primaria, Auto-Incrementable (INT)
    val id: Column<Int> = integer("id").autoIncrement()

    // 2. Nombre: (VARCHAR(50))
    val nombre: Column<String> = varchar("nombre", 50)

    // 3. Mail: (VARCHAR(100)), con índice ÚNICO
    val mail: Column<String> = varchar("mail", 100).uniqueIndex()

    // 4. Password: (VARCHAR(255))
    val password: Column<String> = varchar("password", 255)

    // 5. Fecha de Nacimiento: (DATE)
    val fechaNacimiento: Column<java.time.LocalDate> = date("fecha_nacimiento")

    // 6. Definición explícita de la clave primaria para Exposed
    override val primaryKey = PrimaryKey(id)
}
```

## Integración con la Conexión

Ahora debemos asegurar que la tabla `Usuarios` se cree o se verifique cada vez que la aplicación se inicia. Para ello, añadimos lo siguiente en el método `conectar()` de nuestro objeto `ConexionDB`.

```kotlin
    // Pasamos 'db' para indicar qué conexión usar.
    transaction(db) {
        // Crea la tabla 'Usuarios' (y cualquier otra tabla que definamos e importemos)
        SchemaUtils.create(Usuarios)
        println("Esquema de la tabla 'Usuarios' verificado/creado.")
    }
```

!!! success "🔍 Ejecutar y Analizar"
    Asegúrate de que tu archivo `ConexionDB.kt` incluya la función nueva de `conectar` con la creación del objeto `Usuarios`. **OJO** posiblemente, tendrás que actualizar o importar librerías.

El archivo `ConexionDB.kt` debe ser algo así:

```kotlin
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.DriverManager
import java.sql.SQLException

object ConexionDB {

    // Configuración de la unidad anterior 
    private const val HOST = "IP_HOST"
    private const val PORT = 3306
    private const val DATABASE = "NOMBRE_BD" // Nombre de nuestra BD
    private const val USER = "USUARIO_BD"
    private const val PASSWORD = "PASSWORD_BD"
    
    // URL JDBC para MySQL, ahora utilizada por Exposed
    private val URL = "jdbc:mysql://$HOST:$PORT/$DATABASE?useSSL=false&serverTimezone=Europe/Madrid"

    // Propiedad para almacenar la instancia de la base de datos de Exposed
    lateinit var db: Database // LA INSTANCIA DE LA CONEXIÓN EXPOSED

    /**
     * Intenta establecer la conexión con Exposed e inicializa el esquema de la BD.
     */
    fun conectar() {
        try {
            // 1. Establece la conexión con la base de datos a través de Exposed
            db = Database.connect(
                url = URL,
                user = USER,
                password = PASSWORD
            )
            println("Conexión a MySQL establecida con éxito usando Exposed.")

            // Pasamos 'db' para indicar qué conexión usar.
            transaction(db) {
                // Crea la tabla 'Usuarios' (y cualquier otra tabla que definamos e importemos)
                SchemaUtils.create(Usuarios)
                println("Esquema de la tabla 'Usuarios' verificado/creado.")
            }

        } catch (e: Exception) {
            println("Error al conectar o inicializar la base de datos: ${e.message}")
            if (e.message?.contains("Communications link failure") == true) {
                println("   Pista: Asegúrate de que el servidor MySQL está activo y las credenciales son correctas.")
            }
        }
    }

    /**
     * Función de prueba para verificar la conexión, manteniendo la estructura de la Unidad 3.
     */
    fun testConexion(): Boolean {
        return try {
            DriverManager.getConnection(URL, USER, PASSWORD)?.use { conn ->
                println("Test: Conexión JDBC pura establecida con éxito.")
                true
            } ?: false
        } catch (e: SQLException) {
            println("Test: Error al conectar con JDBC puro: ${e.message}")
            false
        }
    }
}
```

## 🎯 Práctica 2. Diseño de Esquemas

El primer paso para consolidar el conocimiento es aplicar esta técnica a tu propio proyecto.

!!! warning "🎯 Práctica 2. Diseño de Esquemas"

1. **Diseña tu Propia Entidad:** Basado en la temática de tu proyecto personal (ej: si es una tienda, crea la tabla `Productos`, si es una biblioteca, crea la tabla `Libros`).
2. **Define el Objeto `Table`:** Crea un nuevo `object` en tu código (ej: `object Productos : Table("productos")`) que herede de `Table`.
3. **Añade Columnas:** Define al menos **cinco columnas** usando tipos como `varchar`, `integer`, `bool` o `double`.
4. **Actualiza el Conector:** Modifica la función `conectar()` en `ConexionDB.kt` para que use `SchemaUtils.create()` y cree **tu nueva tabla** dentro de la transacción.
5. **Verificación:** Ejecuta el proyecto y verifica en tu gestor de BD (IntelliJ, MySQL Workbench, etc.) que tu nueva tabla se haya creado correctamente.
