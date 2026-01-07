val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "2.2.21"
    id("io.ktor.plugin") version "3.3.2"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.2.21"
}

group = "edu.gva.es"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

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
    implementation("io.ktor:ktor-server-auth-jvm:${ktor_version}")
    implementation("io.ktor:ktor-server-sessions-jvm:${ktor_version}")
    implementation("io.ktor:ktor-server-host-common-jvm:$ktor_version")

    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}
