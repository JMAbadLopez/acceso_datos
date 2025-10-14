# Conceptos avanzados

Finalmente, exploraremos dos conceptos que elevan la robustez y profesionalidad de la aplicación.

## Gestión de Transacciones

Una **transacción** agrupa operaciones en una única unidad de trabajo atómica (todo o nada). Si todas las operaciones tienen éxito, se confirman (`commit`); si alguna falla, se revierten todas (`rollback`), garantizando la integridad de los datos.

🔍 **Ejecutar y Analizar: Implementación de una Transacción**

Este método de ejemplo actualiza dos registros de forma segura dentro de una transacción.

```kotlin
import java.sql.Connection

fun actualizarAlturasConTransaccion(id1: Int, nuevaAltura1: Double, id2: Int, nuevaAltura2: Double) {
    var conn: Connection? = null
    try {
        conn = ConexionBD.getConnection()
        conn?.autoCommit = false // 1. Iniciar transacción

        conn?.prepareStatement("UPDATE plantas SET altura = ? WHERE id = ?")?.use { stmt ->
            stmt.setDouble(1, nuevaAltura1); stmt.setInt(2, id1); stmt.executeUpdate()
        }
        conn?.prepareStatement("UPDATE plantas SET altura = ? WHERE id = ?")?.use { stmt ->
            stmt.setDouble(1, nuevaAltura2); stmt.setInt(2, id2); stmt.executeUpdate()
        }

        conn?.commit() // 2. Confirmar cambios
        println("Transacción completada.")
    } catch (e: SQLException) {
        println("Error en la transacción, se revierten los cambios: ${e.message}")
        conn?.rollback() // 3. Revertir cambios
    } finally {
        conn?.autoCommit = true
        conn?.close()
    }
}
```

## Evolución a SGBD Cliente-Servidor

Gracias a la buena arquitectura, migrar de SQLite a PostgreSQL, MySQL o cualquier **Sistema Gestor de Bases de Datos** (SGBD) es sencillo. Solo requiere modificar el gestor de conexión.

## 🎯 **Práctica para Aplicar (Opcional): Migración a Mysql o PostgreSQL**

1. Instala MySQL o PostgreSQL, preferiblemente mediante un contenedor Docker.
2. Crea la base de datos y la tabla en el nuevo SGBD con la misma estructura.
3. Modifica `ConexionBD.kt` con los nuevos parámetros de conexión (URL, usuario, contraseña).
4. Ejecuta la aplicación. Debería funcionar sin alterar el DAO o la lógica principal.
