plugins {
    kotlin("jvm") version "2.2.0"
    kotlin("plugin.serialization") version "1.9.0"
}

group = "edu.jmabadlopez"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:1.9.1")
    implementation ("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.17.0")
    implementation ("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(11)
}