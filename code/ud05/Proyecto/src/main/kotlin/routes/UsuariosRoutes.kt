package edu.gva.es.routes

import edu.gva.es.domain.LoginRequest
import edu.gva.es.domain.UsuarioDTO
import edu.gva.es.domain.UserSession
import edu.gva.es.services.UsuariosService
import io.ktor.http.*
import io.ktor.server.auth.authenticate
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

fun Route.usuarioRouting() {

    val service = UsuariosService

    route("/auth") {
        post("/login") {

            // Recibimos solo Mail y Password usando el DTO específico
            val login = call.receive<LoginRequest>()
            val usuario = service.buscarPorEmail(login.mail, login.password)

            if (usuario != null) {
                call.sessions.set(UserSession(email = usuario.mail))
                call.respondText("Login exitoso. Bienvenido, ${usuario.nombre}")
            } else {
                call.respond(HttpStatusCode.Unauthorized, "Credenciales incorrectas")
            }
        }

        get("/logout") {
            call.sessions.clear<UserSession>()
            call.respondText("Sesión cerrada correctamente.")
        }
    }

    route("/usuarios") {

        // 1. GET: Obtener todos (Lectura)
        get {
            val lista = service.listarUsuarios()
            call.respond(lista) // Ktor convierte automáticamente la lista a JSON
        }

        // 2. GET/{id}: Obtener uno específico (Lectura)
        get("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@get call.respondText("ID no válido", status = HttpStatusCode.BadRequest)

            val usuario = service.buscarPorId(id)
            if (usuario != null) {
                call.respond(usuario)
            } else {
                call.respondText("Usuario no encontrado", status = HttpStatusCode.NotFound)
            }
        }
        authenticate("auth-session") {
            // 3. POST: Crear nuevo (Escritura)
            post {
                try {
                    // Importante: El JSON debe coincidir con el DTO
                    val usuario = call.receive<UsuarioDTO>()
                    val nuevoId = service.registrarUsuario(usuario)

                    if (nuevoId != -1) call.respond(HttpStatusCode.Created, nuevoId)
                    else call.respond(HttpStatusCode.Conflict, "El email ya existe")
                } catch (e: Exception) {
                    call.respondText("Error en los datos: ${e.message}", status = HttpStatusCode.BadRequest)
                }
            }

            // 4. PUT/{id}: Actualizar (Modificación)
            put("{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@put call.respondText("ID no válido", status = HttpStatusCode.BadRequest)

                val usuarioActualizado = call.receive<UsuarioDTO>()
                val filas = service.actualizarUsuario(id, usuarioActualizado)

                if (filas > 0) {
                    call.respondText("Usuario actualizado correctamente")
                } else {
                    call.respondText("No se pudo actualizar: Usuario no encontrado", status = HttpStatusCode.NotFound)
                }
            }

            // 5. DELETE/{id}: Borrar (Eliminación)
            delete("{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@delete call.respondText("ID no válido", status = HttpStatusCode.BadRequest)

                val eliminado = service.borrarUsuario(id)
                if (eliminado > 0) {
                    call.respondText("Usuario eliminado", status = HttpStatusCode.OK)
                } else {
                    call.respondText("Usuario no encontrado", status = HttpStatusCode.NotFound)
                }
            }
        }
    }
}