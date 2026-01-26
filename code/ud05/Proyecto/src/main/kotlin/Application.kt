package edu.gva.es

import edu.gva.es.plugins.configureRouting
import edu.gva.es.plugins.configureSecurity
import edu.gva.es.plugins.configureSerialization
import edu.gva.es.plugins.configureCors
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

    ConexionDB.conectar()

    configureSerialization()
    configureSecurity()      // INSTALACIÓN DEL GUARDIÁN (Auth y Sesiones)
    configureCors()          // INSTALACIÓN DE CORS
    configureRouting()
}
