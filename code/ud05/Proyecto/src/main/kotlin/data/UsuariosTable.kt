package edu.gva.es.data

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.javatime.date

object Usuarios : Table("usuarios") {
    val id: Column<Int> = integer("id").autoIncrement()
    val nombre: Column<String> = varchar("nombre", 50)
    val mail: Column<String> = varchar("mail", 100).uniqueIndex()
    val password: Column<String> = varchar("password", 255)
    val fechaNacimiento: Column<java.time.LocalDate> = date("fecha_nacimiento")

    override val primaryKey = PrimaryKey(id)
}