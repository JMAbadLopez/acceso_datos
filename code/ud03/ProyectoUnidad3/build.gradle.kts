plugins {
    kotlin("jvm") version "2.2.20"
}

group = "org.abad.jose"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    // Para SQLite
    implementation("org.xerial:sqlite-jdbc:3.43.0.0")

    // Para PostgreSQL (se usará opcionalmente más adelante)
    // implementation("org.postgresql:postgresql:42.7.1")

    // Para MySQL
    // implementation("mysql:mysql-connector-java:8.3.0") //MySQL
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(24)
}