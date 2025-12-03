package es.gva.edu.schema

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.javatime.date

/**
 * Define la tabla 'usuarios' en la base de datos (CE c: Fichero de mapeo).
 * Hereda de [Table] de Exposed. Cada propiedad es una columna.
 */
object Usuarios : Table("usuarios") {

    // 1. ID: Clave Primaria, Auto-Incrementable (INT)
    val id: Column<Int> = integer("id").autoIncrement()

    // 2. Nombre: (VARCHAR(50))
    val nombre: Column<String> = varchar("nombre", 50)

    // 3. Mail: (VARCHAR(100)), con índice ÚNICO
    val mail: Column<String> = varchar("mail", 100).uniqueIndex()

    // 4. Password: (VARCHAR(255))
    val password: Column<String> = varchar("password", 255)

    // 5. Fecha de Nacimiento: (DATE)
    val fechaNacimiento: Column<java.time.LocalDate> = date("fecha_nacimiento")

    // 6. Definición explícita de la clave primaria para Exposed
    override val primaryKey = PrimaryKey(id)
}