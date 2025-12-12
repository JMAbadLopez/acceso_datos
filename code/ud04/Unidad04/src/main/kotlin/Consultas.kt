package es.gva.edu

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate

fun main() {

    ConexionDB.conectar()

    val fechaLimite = LocalDate.of(2001, 1, 1)
    val consulta = obtenerUsuariosMayoresQue(fechaLimite)

    consulta.forEach { usuario ->
        println("ID: ${usuario.id} | Nombre: ${usuario.nombre} | Email: ${usuario.mail}")
    }

    val consultaFiltro = buscarUsuariosPorFiltro()

    consultaFiltro.forEach { usuario ->
        println("ID: ${usuario.id} | Nombre: ${usuario.nombre} | Email: ${usuario.mail}")
    }

    val consultaOrdenacion = obtenerUsuariosOrdenadosPorNacimiento()

    consultaOrdenacion.forEach { usuario ->
        println("ID: ${usuario.id} | Nombre: ${usuario.nombre} | Email: ${usuario.mail}")
    }


}

fun obtenerUsuariosMayoresQue(fechaLimite: LocalDate): List<UsuarioDTO> {

    return transaction(ConexionDB.db) {

        // WHERE fecha_nacimiento < 'fechaLimite'
        Usuarios.selectAll().where { Usuarios.fechaNacimiento less fechaLimite }
            .map { resultRow ->
                // Mapeamos el resultado a nuestro DTO
                UsuarioDTO(
                    id = resultRow[Usuarios.id],
                    nombre = resultRow[Usuarios.nombre],
                    mail = resultRow[Usuarios.mail],
                    fechaNacimiento = resultRow[Usuarios.fechaNacimiento]
                )
            }.toList()
    }
}

fun buscarUsuariosPorFiltro(): List<UsuarioDTO> {
    val limiteNacimiento = LocalDate.of(2000, 1, 1)

    return transaction(ConexionDB.db) {

        // WHERE nombre LIKE 'I%' AND fecha_nacimiento > '2000-01-01'
        Usuarios.selectAll()
            .where { (Usuarios.nombre like "I%") and (Usuarios.fechaNacimiento greater limiteNacimiento) }
            .map { resultRow ->
            // ... (Mapeo a UsuarioDTO)
            UsuarioDTO(
                id = resultRow[Usuarios.id],
                nombre = resultRow[Usuarios.nombre],
                mail = resultRow[Usuarios.mail],
                fechaNacimiento = resultRow[Usuarios.fechaNacimiento]
            )
        }.toList()
    }
}

fun obtenerUsuariosOrdenadosPorNacimiento(): List<UsuarioDTO> {

    return transaction(ConexionDB.db) {
        // CE f: Consulta con ordenación.
        // SELECT * FROM usuarios ORDER BY fecha_nacimiento DESC
        Usuarios.selectAll()
            .orderBy(Usuarios.fechaNacimiento, SortOrder.DESC) // DESC: del más reciente al más antiguo
            .map { resultRow ->
                // ... (Mapeo a UsuarioDTO)
                UsuarioDTO(
                    id = resultRow[Usuarios.id],
                    nombre = resultRow[Usuarios.nombre],
                    mail = resultRow[Usuarios.mail],
                    fechaNacimiento = resultRow[Usuarios.fechaNacimiento]
                )
            }.toList()
    }
}