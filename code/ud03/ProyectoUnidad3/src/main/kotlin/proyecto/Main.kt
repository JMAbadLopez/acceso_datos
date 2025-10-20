package proyecto

fun main() {
    val conn = ConexionBD.getConnection()
    if (conn != null) {
        println("Conectado a la BD correctamente.")
        ConexionBD.closeConnection(conn)
    }
}