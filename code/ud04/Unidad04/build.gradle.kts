plugins {
    kotlin("jvm") version "2.2.20"
}

group = "es.gva.edu"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // 1. Exposed Core (Funcionalidad base del ORM)
    implementation("org.jetbrains.exposed:exposed-core:0.52.0")

    // 2. Exposed JDBC (Para operar con bases de datos relacionales vía JDBC)
    implementation("org.jetbrains.exposed:exposed-jdbc:0.52.0")

    // 3. Exposed DAO (Opcional, para el enfoque de Entidades)
    implementation("org.jetbrains.exposed:exposed-dao:0.52.0")

    // 4. Driver JDBC MySQL (Necesario para conectar con nuestro servidor MySQL)
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