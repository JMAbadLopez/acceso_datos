package edu.gva.es

import edu.gva.es.plugins.configureRouting
import edu.gva.es.plugins.configureSerialization
import io.ktor.server.application.*

fun main(args: Array<String>) {
    // 1. MODELO: Inicializamos la persistencia antes de arrancar la red
    ConexionDB.conectar()

    // 2. CONTROLADOR: Lanzamos el motor Netty de Ktor
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    configureRouting()
}
