package es.gva.edu

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.http.content.staticResources
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*

import es.gva.edu.model.*

fun Application.configureRouting() {
    routing {
        staticResources("static", "static")

        get("/") {
            val text = "hola"
            val type = ContentType.parse("application/json")
            call.respondText(text, type)

        }

        get("/tasks") {
            val tasks = listOf(
                Task("cleaning", "Clean the house", Priority.Low),
                Task("gardening", "Mow the lawn", Priority.Medium),
                Task("shopping", "Buy the groceries", Priority.High),
                Task("painting", "Paint the fence", Priority.Medium)
            )
            staticResources("static", "static")
            call.respond(tasks)
        }
    }
}
