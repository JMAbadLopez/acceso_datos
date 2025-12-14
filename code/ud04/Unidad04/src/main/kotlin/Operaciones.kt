package es.gva.edu

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDate

data class UsuarioDTO( // Data Transfer Object (DTO)
    val id: Int,
    val nombre: String,
    val mail: String,
    val fechaNacimiento: LocalDate
)

fun main() {

    ConexionDB.conectar()

    insertarUsuario("Atenea", "atenea@mail.com", "hash123", LocalDate.of(1999, 5, 20))
    insertarUsuario("Hera", "hera@mail.com", "hash456", LocalDate.of(2004, 10, 15))
    insertarUsuario("Iris", "iris@mail.com", "hash789", LocalDate.of(2005, 1, 1))

    println("\n--- LISTADO DE USUARIOS ---")
    val listaUsuarios = obtenerTodosLosUsuarios()
    listaUsuarios.forEach { usuario ->
        println("ID: ${usuario.id} | Nombre: ${usuario.nombre} | Email: ${usuario.mail}")
    }

    actualizarMail(1, "atena@olympus.com")
    eliminarUsuario(2)

}

fun insertarUsuario(nombre: String, mail: String, passwordHash: String, fechaNac: LocalDate) {
    transaction(ConexionDB.db) {

        // 1. Ejecutamos la función insert en el objeto de la tabla
        val idGenerado = Usuarios.insert {
            // CE d: Aplicamos mecanismos de persistencia a los objetos
            it[Usuarios.nombre]= nombre
            it[Usuarios.mail] = mail
            it[Usuarios.password] = passwordHash
            it[Usuarios.fechaNacimiento] = fechaNac // Uso del LocalDate
        } get Usuarios.id // Capturamos el ID que genera MySQL (AutoIncrement)

        println("Usuario '$nombre' insertado con ID: $idGenerado")
    }
}

fun obtenerTodosLosUsuarios(): List<UsuarioDTO> {

    return transaction(ConexionDB.db) {

        Usuarios.selectAll() // Seleccionamos todas las filas de la tabla
            .map { resultRow -> // Mapeamos cada fila a nuestro objeto UsuarioDTO
                UsuarioDTO(
                    id = resultRow[Usuarios.id],
                    nombre = resultRow[Usuarios.nombre],
                    mail = resultRow[Usuarios.mail],
                    // No recuperamos el password por seguridad
                    fechaNacimiento = resultRow[Usuarios.fechaNacimiento]
                )
            }.toList()
    }
}

fun actualizarMail(usuarioId: Int, nuevoMail: String) {
    transaction(ConexionDB.db) {

        // 1. Usamos la función update y le pasamos la condición 'where'
        val filasAfectadas = Usuarios.update({ Usuarios.id eq usuarioId }) {
            // CE e: Modificamos el objeto persistente
            it[Usuarios.mail] = nuevoMail
        }

        if (filasAfectadas > 0) {
            println("Usuario ID $usuarioId actualizado. Nuevo email: $nuevoMail")
        } else {
            println("No se encontró el usuario ID $usuarioId.")
        }
    }
}

fun eliminarUsuario(usuarioId: Int) {
    transaction(ConexionDB.db) {

        // 1. Usamos la función deleteWhere y definimos la condición
        val filasAfectadas = Usuarios.deleteWhere { Usuarios.id eq usuarioId }

        if (filasAfectadas > 0) {
            println("Usuario ID $usuarioId ELIMINADO.")
        } else {
            println("No se encontró el usuario ID $usuarioId para eliminar.")
        }
    }
}