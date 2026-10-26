# Configuración del Entorno y Persistencia

## 1 Crear el Proyecto Spring Boot

La forma más rápida de crear un proyecto Spring Boot es desde IntelliJ IDEA.

### 1.1 Desde IntelliJ IDEA

1. **File → New → Project** → selecciona **Spring Boot** en la columna izquierda.
2. Rellena los campos:
    - Language: **Kotlin**
    - Type: **Gradle - Kotlin**
    - Group: `com.tuproyecto`
    - Artifact: `usuarios-api`
    - Package name: `com.tuproyecto.usuariosapi`
    - JDK: **21** (o el disponible)
3. En la pantalla de dependencias marca: **Spring Web** y **Spring Security**.
4. Haz clic en **Create**.

!!! tip "Alternativa: Spring Initializr web"
    También puedes generar el proyecto en [start.spring.io](https://start.spring.io) con las mismas opciones y abrirlo en IntelliJ.

### 1.2 Dependencias en `build.gradle.kts`

Añade manualmente las dependencias de **Exposed** y **MySQL** al archivo generado:

```kotlin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.3.5"
    id("io.spring.dependency-management") version "1.1.6"
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.spring") version "2.0.21"
}

group = "com.tuproyecto"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_21

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot — servidor web (Tomcat embebido)
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Spring Security — autenticación y autorización
    implementation("org.springframework.boot:spring-boot-starter-security")

    // Exposed ORM — integración con Spring Boot
    implementation("org.jetbrains.exposed:exposed-spring-boot-starter:0.55.0")
    implementation("org.jetbrains.exposed:exposed-java-time:0.55.0")

    // MySQL driver
    implementation("com.mysql:mysql-connector-j:8.4.0")

    // Serialización JSON para Kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // Tests
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "21"
    }
}
```

!!! info "¿Qué es `exposed-spring-boot-starter`?"
    Este starter integra automáticamente Exposed con Spring Boot. Configura el `DataSource` usando las propiedades de `application.yml` y registra el soporte de transacciones de Exposed con el sistema de transacciones de Spring. No necesitamos llamar a `Database.connect()` manualmente.

## 2 Configuración de la Base de Datos

### 2.1 `application.yml`

Crea o edita el archivo `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/daw_usuarios_2627?serverTimezone=Europe/Madrid
    username: Dam2627
    password: Dam2627
    driver-class-name: com.mysql.cj.jdbc.Driver

  # Exposed gestiona el esquema; desactivamos la gestión de Hibernate
  jpa:
    open-in-view: false

server:
  port: 8080
```

!!! warning "Crear la base de datos antes de arrancar"
    Ejecuta en MySQL:
    ```sql
    CREATE DATABASE IF NOT EXISTS daw_usuarios_2627
      CHARACTER SET utf8mb4
      COLLATE utf8mb4_unicode_ci;
    ```

### 2.2 Práctica: crear la BD y verificar la conexión

!!! success "🔍 Ejecutar y Analizar"
    Arranca la aplicación (`Run` en IntelliJ o `./gradlew bootRun`). Si el `application.yml` es correcto verás en la consola:

    ```
    Started UsuariosApiApplicationKt in 2.456 seconds
    ```

    Si hay un error de conexión verás `Communications link failure` — revisa el servidor MySQL y las credenciales.

## 3 Capa de Datos con Exposed

La capa de datos es **idéntica** a la versión Ktor. Exposed no depende de Ktor; funciona igual bajo Spring Boot.

### 3.1 Tabla: `UsuariosTable.kt`

```kotlin
package com.tuproyecto.usuariosapi.data

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date

object UsuariosTable : Table("usuarios") {
    val id              = integer("id").autoIncrement()
    val nombre          = varchar("nombre", 100)
    val mail            = varchar("mail", 150).uniqueIndex()
    val password        = varchar("password", 255)
    val fechaNacimiento = date("fecha_nacimiento")

    override val primaryKey = PrimaryKey(id)
}
```

### 3.2 DTO: `UsuarioDTO.kt`

```kotlin
package com.tuproyecto.usuariosapi.domain

import java.time.LocalDate

data class UsuarioDTO(
    val id: Int?,
    val nombre: String,
    val mail: String,
    val password: String,
    val fechaNacimiento: LocalDate
)
```

### 3.3 DAO: `UsuariosDAO.kt`

En Spring Boot + Exposed las operaciones de BD se envuelven en `transaction { }` igual que antes. El starter garantiza que se ejecuten dentro del contexto de transacción de Spring.

```kotlin
package com.tuproyecto.usuariosapi.data

import com.tuproyecto.usuariosapi.domain.UsuarioDTO
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
object UsuariosDAO {

    fun crearTabla() = transaction {
        SchemaUtils.create(UsuariosTable)
    }

    fun seleccionarTodos(): List<UsuarioDTO> = transaction {
        UsuariosTable.selectAll().map { toDTO(it) }
    }

    fun seleccionarPorId(id: Int): UsuarioDTO? = transaction {
        UsuariosTable
            .selectAll()
            .where { UsuariosTable.id eq id }
            .firstOrNull()
            ?.let { toDTO(it) }
    }

    fun insertar(usuario: UsuarioDTO): Int = transaction {
        UsuariosTable.insertAndGetId {
            it[nombre]          = usuario.nombre
            it[mail]            = usuario.mail
            it[password]        = usuario.password
            it[fechaNacimiento] = usuario.fechaNacimiento
        }.value
    }

    fun actualizar(id: Int, usuario: UsuarioDTO): Int = transaction {
        UsuariosTable.update({ UsuariosTable.id eq id }) {
            it[nombre]          = usuario.nombre
            it[mail]            = usuario.mail
            it[password]        = usuario.password
            it[fechaNacimiento] = usuario.fechaNacimiento
        }
    }

    fun eliminar(id: Int): Int = transaction {
        UsuariosTable.deleteWhere { UsuariosTable.id eq id }
    }

    private fun toDTO(row: ResultRow) = UsuarioDTO(
        id              = row[UsuariosTable.id],
        nombre          = row[UsuariosTable.nombre],
        mail            = row[UsuariosTable.mail],
        password        = row[UsuariosTable.password],
        fechaNacimiento = row[UsuariosTable.fechaNacimiento]
    )
}
```

### 3.4 Inicializar la tabla al arrancar

En Spring Boot podemos ejecutar código al inicio usando `ApplicationRunner`:

```kotlin
package com.tuproyecto.usuariosapi

import com.tuproyecto.usuariosapi.data.UsuariosDAO
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component

@SpringBootApplication
class UsuariosApiApplication

fun main(args: Array<String>) {
    runApplication<UsuariosApiApplication>(*args)
}

@Component
class DataInitializer : ApplicationRunner {
    override fun run(args: ApplicationArguments) {
        UsuariosDAO.crearTabla()
        println("✅ Tabla 'usuarios' lista.")
    }
}
```

## 4 Estructura del Proyecto

```text
src/main/kotlin/com/tuproyecto/usuariosapi/
├── UsuariosApiApplication.kt   ← Punto de entrada + inicialización
├── data/
│   ├── UsuariosTable.kt        ← Definición de la tabla (Exposed)
│   └── UsuariosDAO.kt          ← Acceso a datos (CRUD)
├── domain/
│   └── UsuarioDTO.kt           ← Modelo de datos transferido
├── services/                   ← (próxima sección)
├── controllers/                ← (próxima sección)
└── security/                   ← (próxima sección)
src/main/resources/
└── application.yml             ← Configuración BD y servidor
```

---

## 🎯 Práctica 1. Configura el proyecto y la BD

!!! warning "🎯 Práctica 1. Configura el proyecto y la BD"

    1. Crea un proyecto Spring Boot con las dependencias indicadas.
    2. Crea la base de datos `daw_usuarios_2627` en MySQL.
    3. Configura `application.yml` con tus credenciales.
    4. Crea `UsuariosTable.kt`, `UsuarioDTO.kt` y `UsuariosDAO.kt`.
    5. Añade el `DataInitializer` en `UsuariosApiApplication.kt`.
    6. Arranca la aplicación y verifica que la tabla `usuarios` se ha creado en MySQL.

---

!!! example "Autoevaluación"

    **Pregunta 3: En la versión Spring Boot, ¿por qué no necesitamos llamar a `Database.connect()` como hacíamos en Ktor?**

    A) Porque Spring Boot no usa Exposed, sino Hibernate para el acceso a datos.

    B) Porque `exposed-spring-boot-starter` lee automáticamente la configuración del `DataSource` de `application.yml` y establece la conexión con Exposed al arrancar la aplicación.

    C) Porque Spring Boot se conecta directamente a MySQL sin necesidad de ningún ORM.

    D) Porque en Spring Boot las transacciones son opcionales y no se requiere una conexión previa.

    ??? quote "Solución"
        ❌ A) Seguimos usando Exposed. La dependencia `exposed-spring-boot-starter` es exactamente el puente entre Exposed y Spring Boot.

        ✅ B) El starter `exposed-spring-boot-starter` actúa como adaptador: lee el `DataSource` configurado por Spring Boot (a partir de `spring.datasource.*` en `application.yml`) y lo registra en Exposed automáticamente. Esto elimina la llamada manual a `Database.connect()` que era necesaria en Ktor.

        ❌ C) Spring Boot siempre usa una librería de acceso a datos (Exposed, JPA/Hibernate, JOOQ, JDBC puro, etc.). La BD no es directamente accesible desde la capa web.

        ❌ D) Las transacciones siguen siendo necesarias y se siguen marcando con `transaction { }` en el DAO.

    **Pregunta 4: ¿Para qué sirve la anotación `@Repository` en `UsuariosDAO`?**

    A) Le dice a Spring que esta clase es un controlador REST y debe escuchar peticiones HTTP.

    B) Indica a Spring que esta clase forma parte de la capa de acceso a datos, lo que activa la traducción automática de excepciones de BD y permite que Spring la gestione como un componente del contexto de aplicación.

    C) Hace que todos los métodos del DAO se ejecuten automáticamente dentro de una transacción de Spring, eliminando la necesidad de `transaction { }`.

    D) Genera automáticamente el código CRUD de la clase basándose en el nombre de los métodos.

    ??? quote "Solución"
        ❌ A) Eso es `@RestController`. `@Repository` no tiene nada que ver con las peticiones HTTP.

        ✅ B) `@Repository` es un estereotipo de Spring que cumple dos propósitos: (1) registra la clase en el contexto de Spring (igual que `@Component`), permitiendo que se inyecte en otras clases; y (2) activa la traducción de excepciones específicas de la capa de datos (`DataAccessException`), lo que hace el manejo de errores más uniforme. No genera código automáticamente.

        ❌ C) `@Repository` no envuelve los métodos en transacciones. Para eso se usa `@Transactional` (Spring) o el bloque `transaction { }` de Exposed. En nuestro DAO usamos el bloque `transaction { }` de Exposed dentro de cada método.

        ❌ D) Esa es la idea detrás de Spring Data JPA (`JpaRepository`), no de Exposed con `@Repository`.
