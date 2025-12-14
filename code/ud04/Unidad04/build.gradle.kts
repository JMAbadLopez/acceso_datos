plugins {
    kotlin("jvm") version "2.2.20"
}

group = "es.gva.edu"

repositories {
    mavenCentral()
}

// Define el archivo JAR
tasks.jar {
    manifest {
        attributes["Main-Class"] = "es.gva.edu.Mainkt" // ¡Importante! Reemplaza esto con tu clase principal
    }
    // Copia todas las dependencias al JAR
    configurations["compileClasspath"].forEach { file: File ->
        from(zipTree(file))
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

dependencies {

    val exposedVersion = "0.52.0"

    // 1. Exposed Core: Funcionalidad base
    implementation("org.jetbrains.exposed:exposed-core:${exposedVersion}")
    // 2. Exposed JDBC: Conexión y ejecución de consultas
    implementation("org.jetbrains.exposed:exposed-jdbc:${exposedVersion}")
    // 3. Exposed DAO: Data Access Object
    implementation("org.jetbrains.exposed:exposed-dao:${exposedVersion}")
    // 4. Módulo CLAVE para mapear tipos de fecha (LocalDate, etc.)
    implementation("org.jetbrains.exposed:exposed-java-time:${exposedVersion}")
    // 5. Driver JDBC MySQL (Necesario para conectar con nuestro servidor MySQL)
    implementation("mysql:mysql-connector-java:8.0.29") //MySQL

    // Dependencia estándar de Kotlin...
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")


}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(24)
}