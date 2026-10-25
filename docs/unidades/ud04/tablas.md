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

!!! success "🔍 Ejecutar y Analizar: Definición del esquema"
    Crea el fichero `Usuarios.kt` con el código de mapeo que verás a continuación. Este archivo es una definición estática de esquema: no genera salida por consola. Comprueba que el archivo compila sin errores en IntelliJ antes de continuar.

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

!!! success "🔍 Ejecutar y Analizar: Creación del esquema en MySQL"
    Asegúrate de que tu archivo `ConexionDB.kt` incluya la función nueva de `conectar` con la creación del objeto `Usuarios`. **OJO** posiblemente, tendrás que actualizar o importar librerías. Llama a `ConexionDB.conectar()` desde `main` y verifica que la salida por consola es:

    ```
    Conexión a MySQL establecida con éxito usando Exposed.
    Esquema de la tabla 'Usuarios' verificado/creado.
    ```

    Comprueba también en IntelliJ Database (o MySQL Workbench) que la tabla `usuarios` aparece con las cinco columnas definidas.

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

!!! example "Autoevaluación"

    **Pregunta 3: En Exposed, ¿qué ocurre si llamas a `SchemaUtils.create(Usuarios)` y la tabla `usuarios` ya existe en la base de datos?**

    A) Lanza una excepción `TableAlreadyExistsException` que detiene la ejecución.

    B) Elimina la tabla existente con todos sus datos y la vuelve a crear desde cero.

    C) No hace nada: comprueba si la tabla existe y, si es así, la deja intacta.

    D) Añade las columnas nuevas que no existieran, pero no borra las existentes.

    ??? quote "Solución"
        ❌ A) `SchemaUtils.create` no lanza excepciones por tablas ya existentes. Está diseñado para usarse al arrancar la aplicación de forma segura.

        ❌ B) Para eliminar y recrear una tabla se usa `SchemaUtils.drop` seguido de `SchemaUtils.create`. `create` por sí solo nunca destruye datos existentes.

        ✅ C) `SchemaUtils.create` emite un `CREATE TABLE IF NOT EXISTS`. Si la tabla ya existe, el comando no tiene efecto y los datos se conservan. Por eso es seguro llamarlo cada vez que arranca la aplicación.

        ❌ D) Eso sería el comportamiento de `SchemaUtils.createMissingTablesAndColumns`, que sí añade columnas nuevas. `create` básico no hace migraciones de esquema.

    **Pregunta 4: Al recuperar datos con `Usuarios.selectAll().map { resultRow -> ... }`, ¿qué representa cada `resultRow` dentro del bloque `map`?**

    A) Un objeto `Usuarios` completo con todas sus propiedades ya mapeadas a Kotlin.

    B) Una fila de la tabla de base de datos, de la que se accede a cada columna usando el operador `[]` con la referencia a la columna (p.ej. `resultRow[Usuarios.nombre]`).

    C) Un índice numérico que identifica la posición de cada fila en el `ResultSet`.

    D) Una cadena JSON con todos los campos de la fila listos para enviar por la red.

    ??? quote "Solución"
        ❌ A) Exposed no crea instancias de tu clase automáticamente. El `map` es el paso donde tú construyes el objeto Kotlin (p.ej. un DTO) a partir de los datos brutos de la fila.

        ✅ B) `resultRow` es un objeto `ResultRow` de Exposed. Actúa como un mapa tipado donde la clave es la referencia a la columna del objeto `Table` (como `Usuarios.nombre`) y el valor es el dato de esa columna en la fila actual.

        ❌ C) El índice numérico lo gestiona Exposed internamente. Lo que recibe el bloque `map` es la fila en sí, no su posición.

        ❌ D) La serialización a JSON es responsabilidad de kotlinx-serialization, no de Exposed. `resultRow` contiene tipos Kotlin nativos (`String`, `Int`, `LocalDate`, etc.).

## 🎯 Práctica 2. Diseño de Esquemas

El primer paso para consolidar el conocimiento es definir la estructura de las tablas del **Catálogo de Videojuegos** con Exposed.

!!! warning "🎯 Práctica 2. Diseño de Esquemas"

    Vas a definir el mapeo de las tablas del **Catálogo de Videojuegos** usando Exposed.

    **Tabla principal — `object Videojuegos : Table("videojuegos")`**

    | Campo SQL | Tipo Exposed | Tipo Kotlin | Restricciones |
    | :--- | :--- | :--- | :--- |
    | `id` | `integer` | `Int` | PK, AutoIncrement |
    | `titulo` | `varchar(100)` | `String` | No nulo |
    | `genero` | `varchar(50)` | `String` | No nulo |
    | `anio` | `integer` | `Int` | No nulo |
    | `nota` | `double` | `Double` | No nulo |

    **Tabla de listas — `object Listas : Table("listas")`**

    | Campo SQL | Tipo Exposed | Tipo Kotlin | Restricciones |
    | :--- | :--- | :--- | :--- |
    | `id_lista` | `integer("id_lista")` | `Int` | PK, AutoIncrement |
    | `nombre` | `varchar(50)` | `String` | No nulo, Único |

    1. Crea el fichero `Videojuegos.kt` en el paquete `schemas` con el `object Videojuegos : Table("videojuegos")` y sus cinco columnas.
    2. Crea el fichero `Listas.kt` con el `object Listas : Table("listas")` y sus dos columnas.
    3. Actualiza la función `conectar()` en `ConexionDB.kt` para que ejecute `SchemaUtils.create(Videojuegos, Listas)` dentro de la transacción.
    4. Ejecuta el proyecto y verifica en IntelliJ Database (o MySQL Workbench) que las dos tablas aparecen con las columnas correctas.

    !!! danger "Reto extra"
        Define también la tabla de relación `ListasVideojuegos : Table("listas_videojuegos")` con las columnas `id_lista` (FK) y `id_videojuego` (FK). Crea las claves foráneas con `.references(Listas.id_lista)` y `.references(Videojuegos.id)` y añádela a `SchemaUtils.create(...)`.

