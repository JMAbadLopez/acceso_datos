package es.gva.edu.schemas

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object TasksSchema : Table("tasks") {

    // 1. ID: Clave Primaria, Auto-Incrementable (INT)
    val id: Column<Int> = integer("id").autoIncrement()

    // 2. Nombre: (VARCHAR(50))
    val name: Column<String> = varchar("name", 50)

    // 3. Mail: (VARCHAR(100)), con índice ÚNICO
    val description: Column<String> = varchar("description", 50)

    // 4. Password: (VARCHAR(255))
    val priority: Column<String> = varchar("priority", 50)

    // 6. Definición explícita de la clave primaria para Exposed
    override val primaryKey = PrimaryKey(id)
}