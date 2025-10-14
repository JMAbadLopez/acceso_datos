# Conexión a la Base de Datos con JDBC

Ahora que tenemos el entorno listo, el siguiente paso es establecer la comunicación entre nuestra aplicación Kotlin y la base de datos.

## Mecanismos de Acceso: ORM vs. Conectores

Existen dos estrategias principales para interactuar con una base de datos:

* **ORM (Object-Relational Mapping)**: Herramientas como *Hibernate* o *Exposed* que mapean tablas a clases de código, permitiendo trabajar con objetos en lugar de escribir SQL directamente.
  
> **Algunos ejemplos de ORMs**
>
> ORM / Framework| Lenguaje| Descripción
> ---------------|---------|-----------------
> Hibernate| Java/Kotlin| El ORM más utilizado con JPA
> Exposed| Kotlin| ORM ligero y expresivo creado por JetBrains
> Spring Data JPA| Java/Kotlin| Abstracción que automatiza el acceso a datos
> Room| Java/Kotlin| ORM oficial para bases de datos SQLite en Android  

* **Conectores (Drivers)**: Librerías que actúan como puente entre la aplicación y el SGBD. Utilizaremos el estándar **JDBC (Java Database Connectivity)**, que requiere escribir SQL manualmente pero proporciona un control total y una comprensión fundamental de la comunicación.

## Conectores

Un **conector** (también llamado driver) es una librería software que permite que una aplicación se comunique con un gestor de base de datos (SGBD). Actúa como un puente entre nuestro código y la base de datos, traduciendo las instrucciones SQL a un lenguaje que el gestor puede entender y viceversa. Sin un conector, tu aplicación no podría comunicarse con la base de datos.

Una base de datos puede ser accedida desde diferentes orígenes o herramientas, siempre que tengamos:

* Las credenciales de acceso (usuario y contraseña)
* El host/servidor donde se encuentra la base de datos
* El motor de base de datos (PostgreSQL, MySQL, SQLite, etc.)
* Los puertos habilitados y los permisos correctos

Las principales formas de conectarse a una base de datos son las siguientes:

 Medio de conexión | Descripción
-------------------|-------------
Aplicaciones de escritorio | Herramientas gráficas como **DBeaver**, **pgAdmin**, **MySQL Workbench**, **DB Browser for SQLite**, incluso el **plugin DB para IntelliJ** Permiten explorar, consultar y administrar BD de forma visual.
Aplicaciones desarrolladas en código | Programas en **Kotlin**, **Java**, **Python**, **C#**, etc., mediante **conectores** como **JDBC**, **psycopg2**, **ODBC**, etc. para acceder a BD desde código.
Línea de comandos | Clientes como `psql` (PostgreSQL), `mysql`, `sqlite3`. Permiten ejecutar comandos SQL directamente desde terminal.
Aplicaciones web | Sitios web que acceden a BD desde el backend (por ejemplo, en Spring Boot, Node.js, Django, etc.).
APIs REST o servidores intermedios | Servicios web que conectan la BD con otras aplicaciones, actuando como puente o capa de seguridad.
Aplicaciones móviles | Apps Android/iOS que acceden a BD locales (como **SQLite**) o remotas (vía **Firebase**, API REST, etc.).
Herramientas de integración de datos | Software como **Talend**, **Pentaho**, **Apache Nifi** para migrar, transformar o sincronizar datos entre sistemas.

De todas las formas posibles de interactuar con una base de datos, nos vamos a centrar en el uso de **conectores JDBC (Java Database Connectivity)**. **JDBC** es una API estándar de Java (y compatible con Kotlin) que permite conectarse a una BD, enviar instrucciones SQL y procesar los resultados manualmente. Es el método de más bajo nivel, pero ofrece un control total sobre lo que ocurre en la BD. Es ideal para aprender los fundamentos del acceso a datos y tener control total y aprenderlo ayuda a entender mejor lo que hace un ORM por debajo. Sus principales características son:

* El programador escribe directamente las consultas SQL.
* Requiere gestionar manualmente conexiones, sentencias y resultados.
* Se necesita un driver específico (conector) para cada SGBD.

> **Algunos ejemplos de conectores**
>
> SGBD|Conector (Driver JDBC)|URL de conexión típica
> ----|-------------------------|-----------------------
> PostgreSQL|org.postgresql.Driver| jdbc:postgresql://host:puerto/basedatos
> MySQL / MariaDB|com.mysql.cj.jdbc.Driver| jdbc:mysql://host:puerto/basedatos
> SQLite (embebido)|org.sqlite.JDBC|jdbc:sqlite:ruta_al_fichero

Una aplicación (escrita en Kotlin, Java u otro lenguaje) puede leer, insertar o modificar información almacenada en una base de datos relacional si previamente se ha conectado al sitema gestor de base de datos (SGBD). 

**JDBC** (Java Database Connectivity) es la API básica de Java (conector) para conectarse a bases de datos relacionales y su sintaxis general es:

```kotlin
jdbc:<gestor>://<host>:<puerto>/<nombre_base_datos>
```

Aunque puede variar según el SGBD con el que se trabaje. Por ejemplo en SQLite no se necesita usuario ni contraseña ya que es una base de datos local y embebida.

SGBD|URL de conexión
-----------------------|---------------------
PostgreSQL|jdbc:postgresql://localhost:5432/plantas
MySQL|jdbc:mysql://localhost:3306/plantas
SQLite|jdbc:sqlite:plantas.sqlite

También dependiendo del SGBD será necesario utilizar un **conector JDBC** u otro. Para ello utilizaremos la herramienta **Gradle**, que permite automatizar la gestión de dependencias sin tener que configurar nada a mano añadiendo las líneas correspondientes en el fichero **build.gradle.kts**.

A continuación se muestran las líneas para los SGBD PostgreSQL, MySQL y SQLite. Para usar JDBC, es necesario añadir la dependencia del driver en `build.gradle.kts`:

```kotlin
dependencies {
    // Para SQLite
    implementation("org.xerial:sqlite-jdbc:3.43.0.0")

    // Para PostgreSQL (se usará opcionalmente más adelante)
    implementation("org.postgresql:postgresql:42.7.1")

    // Para MySQL
    implementation("mysql:mysql-connector-java:8.3.0") //MySQL
}
```

## El Gestor de Conexión

Una buena práctica es centralizar la lógica de conexión en un único objeto. Esto facilita el mantenimiento, ya que cualquier cambio en la configuración solo requiere modificar un fichero.

🔍 **Ejecutar y Analizar: Conexión Básica**

El siguiente código muestra una conexión directa en un **proyecto en Kotlin** a una base de datos **SQLite**, en concreto `plantas.sqlite`. Ejecútalo para comprobar que se establece la comunicación.

```kotlin
import java.io.File
import java.sql.DriverManager

fun main() {
    val dbPath = "datos/plantas.sqlite" // Asegúrate de que exista este fichero
    val dbFile = File(dbPath)
    println("Ruta absoluta de la BD: ${dbFile.absolutePath}")
    val url = "jdbc:sqlite:${dbFile.absolutePath}"

    try {
        DriverManager.getConnection(url).use { conn ->
            println("Conexión establecida correctamente con SQLite.")
        }
    } catch (e: Exception) {
        println("Error al conectar: ${e.message}")
    }
}
```

## Organización del Proyecto

Cuando se trabaja con bases de datos, una buena opción para organizar el código es tener el código que maneja toda la lógica de BD en un mismo sitio En Kotlin se puede crear un objeto con funciones para que estén disponibles de forma directa en otras partes del programa.

Una de las ventajas de trabajar de esta forma es que evitas repetir código para abrir y cerrar conexiones y, además, si la base de datos cambia de ubicación, solo habrá que actualizar la ruta dentro del código de este objeto no en varios sitios.

También es muy recomendable utilizar los bloques **try-catch-finally** para capturar posibles errores y excepciones.

Para mejorar la organización, encapsularemos esta lógica en un objeto `ConexionBD`:

```kotlin
// Fichero: ConexionBD.kt
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

object ConexionBD {
    private val dbPath = "datos/plantas.sqlite" // Modificar según tu fichero
    private val dbFile = File(dbPath)
    private val url = "jdbc:sqlite:${dbFile.absolutePath}"

    fun getConnection(): Connection? {
        return try {
            DriverManager.getConnection(url)
        } catch (e: SQLException) {
            println("Error al conectar con la base de datos: ${e.message}")
            null
        }
    }

    // Función de prueba: verificar conexión
    fun testConnection(): Boolean {
        return getConnection()?.use { conn ->
            println("Conexión establecida con éxito a ${dbFile.absolutePath}")
            true
        } ?: false
    }

    // Cerrar conexión (para los casos en los que no se utiliza .use)
    fun closeConnection(conn: Connection?) {
        try {
            conn?.close()
            println("Conexión cerrada correctamente.")
        } catch (e: SQLException) {
            println("Error al cerrar la conexión: ${e.message}")
        }
    }
}
```

De esta forma, cuando el programa necesite acceder a la BD llamará a la función de conexión y cuando termine llamará a la función que cierra la conexión. Un ejemplo de estas llamadas podría ser:

``` kotlin
fun main() {
    val conn = ConexionBD.getConnection()
    if (conn != null) {
        println("Conectado a la BD correctamente.")
        ConexionBD.closeConnection(conn)
    }
}
```

## 🎯 **Práctica 2: Implementar el Gestor de Conexión**

1. Crea un nuevo fichero `ConexionBD.kt` en tu proyecto.
2. Implementa el `object` anterior, modificando `dbPath` para que apunte a tu fichero `.sqlite`.
3. En `Main.kt`, utiliza `ConexionBD.getConnection()` para verificar que la conexión se establece.
