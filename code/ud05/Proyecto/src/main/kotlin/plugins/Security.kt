package edu.gva.es.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.sessions.*
import io.ktor.server.response.*
import edu.gva.es.domain.UserSession

fun Application.configureSecurity() {

    install(Sessions) {
        cookie<UserSession>("USER_SESSION") {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 3600 // La sesión dura 1 hora
        }
    }

    install(Authentication) {
        session<UserSession>("auth-session") {
            validate { session ->
                // Si la sesión existe, el usuario está validado
                session
            }
            challenge {
                // Qué pasa si alguien intenta entrar sin sesión
                call.respondText("401: Acceso denegado. Debes iniciar sesión.", status = io.ktor.http.HttpStatusCode.Unauthorized)
            }
        }
    }
}