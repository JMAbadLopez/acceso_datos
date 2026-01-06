package edu.gva.es.data

import edu.gva.es.domain.UsuarioDTO
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object UsuariosDAO {
    private fun ResultRow.toUsuarioDTO() = UsuarioDTO(
        id = this[Usuarios.id],
        nombre = this[Usuarios.nombre],
        mail = this[Usuarios.mail],
        password = this[Usuarios.password],
        fechaNacimiento = this[Usuarios.fechaNacimiento]
    )

    fun insertar(u: UsuarioDTO) = transaction {
        Usuarios.insert {
            it[nombre] = u.nombre
            it[mail] = u.mail
            it[password] = u.password
            it[fechaNacimiento] = u.fechaNacimiento
        } get Usuarios.id
    }

    fun actualizar(id: Int, u: UsuarioDTO) = transaction {
        Usuarios.update({ Usuarios.id eq id }) {
            it[nombre] = u.nombre
            it[mail] = u.mail
            it[password] = u.password
            it[fechaNacimiento] = u.fechaNacimiento
        }
    }

    fun seleccionarTodos() = transaction {
        Usuarios.selectAll().map { it.toUsuarioDTO() }
    }

    fun seleccionarPorId(id: Int) = transaction {
        Usuarios.selectAll().where { Usuarios.id eq id }
            .map { it.toUsuarioDTO() }
            .singleOrNull()
    }

    fun eliminar(id: Int) = transaction {
        Usuarios.deleteWhere { Usuarios.id eq id }
    }
}