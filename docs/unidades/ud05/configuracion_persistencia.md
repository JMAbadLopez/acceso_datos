# Configuración del Entorno y Persistencia

Vamos a dar vida al servidor. Configuraremos las dependencias necesarias, prepararemos el sistema de traducción de datos (JSON) e integraremos la conexión a la base de datos que creamos en la unidad anterior.

## 1 Dependencias Clave: El motor de nuestra APP

Para que Ktor funcione correctamente con Exposed y MySQL, necesitamos configurar nuestro archivo de construcción. Gracias al asistente de IntelliJ, gran parte de esto ya estará listo, pero es vital verificar que no falte nada.

!!! success "🔍 Ejecutar y Analizar"
    Abre tu archivo `build.gradle.kts`. Asegúrate de tener estas dependencias organizadas. Observa cómo cada una cumple un rol específico:

```kotlin

dependencies {

    val exposedVersion = "0.52.0"
    val ktor_version: String by project

    // 1. KTOR: El motor del servidor y los plugins básicos
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("io.ktor:ktor-server-netty")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-config-yaml")

    // 2. EXPOSED: Nuestro ORM

    implementation("org.jetbrains.exposed:exposed-core:${exposedVersion}")
    implementation("org.jetbrains.exposed:exposed-jdbc:${exposedVersion}")
    implementation("org.jetbrains.exposed:exposed-dao:${exposedVersion}")
    implementation("org.jetbrains.exposed:exposed-java-time:${exposedVersion}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // 3. BASE DE DATOS: Driver de MySQL
    implementation("mysql:mysql-connector-java:8.0.29")
    
    // 4. LOGGING: Para ver qué pasa en el servidor
    implementation("io.ktor:ktor-server-auth-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-host-common-jvm:$ktor_version")

    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}
```

> **Importante:** Recuerda actualizar las dependencias con el gestor de Gradle

## 2 Configuración de Serialización JSON

Una API REST se comunica principalmente mediante **JSON**. Sin embargo, Kotlin trabaja con objetos. Necesitamos un "traductor" que automatice este proceso. Para ello usamos el plugin `ContentNegotiation`.

!!! success "🔍 Ejecutar y Analizar"
    Busca tu archivo `Serialization.kt` (o el archivo donde IntelliJ haya configurado los plugins). La configuración debe verse así:

```kotlin
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.application.*

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json() // Esto indica a Ktor que use kotlinx-serialization para manejar JSON
    }
}
```

**¿Por qué es importante?** Sin esto, cuando intentes enviar un objeto `UsuarioDTO`, el servidor no sabrá cómo convertirlo a texto para enviarlo por internet.

## 3 Integración de Exposed: El orden de encendido

Para que nuestra API funcione, la base de datos debe estar lista **antes** de que el servidor empiece a recibir peticiones. El flujo correcto es:

1. Conectar a la Base de Datos y verificar tablas (**Modelo**).
2. Iniciar el motor del servidor Ktor (**Controlador**).

### Estructura Modular y MVC

A continuación, vemos cómo se distribuyen los archivos actuales y cómo se relacionan con el patrón MVC visto en la introducción:

```text
src/main/kotlin/com/tu.proyecto/
├── core/
│   └── ConexionDB.kt    // MODELO (M): Gestiona la conexión y el esquema de datos.
├── plugins/
│   ├── Serialization.kt // VISTA (V): Configura cómo se presentan los datos (JSON).
│   └── Routing.kt       // CONTROLADOR (C): Define las rutas y dirige el tráfico.
└── Application.kt       // LA UNIÓN: El punto de entrada que orquesta todo.
```

### El archivo de configuración: `core/ConexionDB.kt`

!!! success "🔍 Ejecutar y Analizar"
    Este archivo es el **Modelo (M)** encargado de establecer el canal de comunicación con MariaDB. Por ahora, nos centraremos solo en que la conexión sea exitosa.

```kotlin
import org.jetbrains.exposed.sql.Database

object ConexionDB {
    // Parámetros de conexión al servidor de base de datos
    private const val HOST = "192.168.56.101"
    private const val PORT = 3306
    private const val DATABASE = "proyecto" // Nombre de nuestra BD
    private const val USER = "dam"
    private const val PASSWORD = "Dam2526"
    
    private val URL = "jdbc:mysql://$HOST:$PORT/$DATABASE?useSSL=false&serverTimezone=Europe/Madrid"

    lateinit var db: Database 
    
    fun conectar() {
        try {
            // Establece la conexión a través de Exposed
            db = Database.connect(
                url = URL, 
                user = USER, 
                password = PASSWORD
            )
            println("✅ Conexión establecida con éxito en $HOST")
        } catch (e: Exception) {
            println("❌ Error al conectar con la base de datos: ${e.message}")
        }
    }
}
```

### Adaptando la conexión en `Application.kt`

!!! warning "🎯 Práctica para Aplicar"
    Modifica tu función `main` en el archivo `Application.kt`. Observa cómo llamamos a los plugins configurados para que el servidor sepa usarlos:

```kotlin
import com.tu.proyecto.core.ConexionDB
import com.tu.proyecto.plugins.*

fun main(args: Array<String>) {
    // 1. MODELO: Inicializamos la base de datos primero
    println("Iniciando conexión con la base de datos...")
    ConexionDB.conectar() 

    // 2. CONTROLADOR: Iniciamos el motor del servidor
    io.ktor.server.netty.EngineMain.main(args)
}

// Esta función es llamada automáticamente por EngineMain
fun Application.module() {
    configureSerialization() // VISTA: Activamos la traducción a JSON
    configureRouting()       // CONTROLADOR: Activamos las rutas
}
```

### Explicación del servidor

* **Netty:** Es el "motor" que escucha en el puerto (por defecto 8080).
* **EngineMain:** Lee la configuración (`application.conf`) e inicia el ciclo de vida de Ktor.

## 4 Migración y Persistencia Completa

Para que el patrón **MVC** sea efectivo, necesitamos definir claramente nuestros objetos de datos y la lógica de acceso.

### A. El Mapeo de la Tabla (Data Layer)

Definimos la estructura de la tabla `usuarios` usando el DSL de Exposed.

#### Fichero: `data/UsuariosTable.kt`

```kotlin
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.javatime.date

object Usuarios : Table("usuarios") {
    val id: Column<Int> = integer("id").autoIncrement()
    val nombre: Column<String> = varchar("nombre", 50)
    val mail: Column<String> = varchar("mail", 100).uniqueIndex()
    val password: Column<String> = varchar("password", 255)
    val fechaNacimiento: Column<java.time.LocalDate> = date("fecha_nacimiento")

    override val primaryKey = PrimaryKey(id)
}
```

!!! success "🔍 Integración en ConexionDB"
    Para que el mapeo anterior tenga efecto, debemos incluir este bloque en nuestra función `conectar()` de `ConexionDB.kt`:

    ```kotlin
    transaction(db) {
        // Crea la tabla 'Usuarios' (y cualquier otra tabla que definamos e importemos)
        SchemaUtils.create(Usuarios)
        println("Esquema de la tabla 'Usuarios' verificado/creado.")
    }
    ```

### B. El DTO y el Serializador de Fechas

!!! danger "El problema de LocalDate"
    Kotlinx.serialization no sabe por defecto cómo convertir una fecha a texto. Necesitamos crear un **Serializer** personalizado para enseñarle que una fecha se escribe como `"AAAA-MM-DD"`.

#### Fichero: `domain/LocalDateSerializer.kt`

```kotlin
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object LocalDateSerializer : KSerializer<LocalDate> {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    override val descriptor: SerialDescriptor = 
        PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeString(value.format(formatter))
    }

    override fun deserialize(decoder: Decoder): LocalDate {
        return LocalDate.parse(decoder.decodeString(), formatter)
    }
}
```

Separamos el objeto de transporte (DTO) de la lógica de acceso (DAO) que haremos a parte.

#### Fichero: `domain/UsuarioDTO.kt`

```kotlin
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class UsuarioDTO(
    val id: Int,
    val nombre: String,
    val mail: String,
    val password: String,
    @Serializable(with = LocalDateSerializer::class) // Indicamos el traductor
    val fechaNacimiento: LocalDate
)
```

### C. El DAO (Capa de persistencia): CRUD Completo

#### Fichero: `data/UsuariosDAO.kt`

!!! success "🔍 Ejecutar y Analizar"
    El DAO encapsula todas las operaciones sobre la tabla. Observa la función `actualizar`, que permite modificar registros por su ID.

```kotlin
import edu.gva.es.domain.UsuarioDTO
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object UsuariosDAO {
    private fun ResultRow.toUsuarioDTO() = UsuarioDTO(
        id = this[Usuarios.id],
        nombre = this[Usuarios.nombre],
        mail = this[Usuarios.mail],
        password = this[Usuarios.password],
        fechaNacimiento = this[Usuarios.fechaNacimiento]
    )

    fun insertar(u: UsuarioDTO) = transaction {
        Usuarios.insert {
            it[nombre] = u.nombre
            it[mail] = u.mail
            it[password] = u.password
            it[fechaNacimiento] = u.fechaNacimiento
        } get Usuarios.id
    }

    fun actualizar(id: Int, u: UsuarioDTO) = transaction {
        Usuarios.update({ Usuarios.id eq id }) {
            it[nombre] = u.nombre
            it[mail] = u.mail
            it[password] = u.password
            it[fechaNacimiento] = u.fechaNacimiento
        }
    }

    fun seleccionarTodos() = transaction {
        Usuarios.selectAll().map { it.toUsuarioDTO() }
    }

    fun seleccionarPorId(id: Int) = transaction {
        Usuarios.selectAll().where { Usuarios.id eq id }
            .map { it.toUsuarioDTO() }
            .singleOrNull()
    }

    fun eliminar(id: Int) = transaction {
        Usuarios.deleteWhere { Usuarios.id eq id }
    }
}
```

### D. Estructura Final del Proyecto

Así debe lucir tu árbol de directorios para mantener la coherencia modular:

```text
src/main/kotlin/com/tu.proyecto/
├── core/
│   └── ConexionDB.kt    // M: Conexión y Schema
├── data/
│   ├── UsuariosTable.kt // M: Mapeo de Tabla
│   └── UsuariosDAO.kt   // M: CRUD con Exposed
├── domain/
│   └── UsuarioDTO.kt    // M: Modelo de datos serializable
│   └── LocalDateSerializer.kt // Ayudante de serialización
├── plugins/
│   ├── Serialization.kt // V: Configuración JSON
│   └── Routing.kt       // C: Rutas de la API
└── Application.kt       // Unión de capas
```

## 🎯 Práctica 2. Preparando las conexiones y la estructura del proyecto

!!! warning "🎯 Práctica para Aplicar"
    1. Sigue los pasos de creación de toda la estructura y componentes de Persistencia del Proyecto.
    2. Verifica que tu estructura de carpetas coincide con el árbol modular mostrado arriba.
    3. Asegúrate de que `Application.kt` llama tanto a la base de datos como a los plugins de serialización y routing.
    4. Ejecuta el proyecto. Si ves los logs de Ktor sin errores, ¡tu arquitectura MVC está lista!