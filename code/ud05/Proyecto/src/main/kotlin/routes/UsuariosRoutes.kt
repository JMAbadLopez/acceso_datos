package edu.gva.es.routes

import edu.gva.es.domain.UsuarioDTO
import edu.gva.es.services.UsuariosService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.usuarioRouting() {

    // El controlador llama al Servicio, NUNCA directamente al DAO
    val service = UsuariosService

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