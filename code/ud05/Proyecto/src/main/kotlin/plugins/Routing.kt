package edu.gva.es.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import edu.gva.es.routes.*

fun Application.configureRouting() {
    routing {

        get("/") {
            call.respondText("¡Hola mundo!")
        }

        usuarioRouting()
    }
}
