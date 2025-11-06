package ejemplos
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException


fun main() {
    val conn = ConexionBDMySQL.getConnection();
    if(conn!=null) {
        println("Conectado correctamente");
        ConexionBDMySQL.closeConnection(conn);
    }

}

object ConexionBDMySQL {

    // Cambia los valores según tu configuración MySQL
    private const val HOST = "localhost"
    private const val PORT = 3306
    private const val DATABASE = "plantas"
    private const val USER = "dam"
    private const val PASSWORD = "Dam2526"

    // URL JDBC para MySQL
    private val url = "jdbc:mysql://$HOST:$PORT/$DATABASE?useSSL=false&serverTimezone=Europe/Madrid"

    fun getConnection(): Connection? {
        return try {
            DriverManager.getConnection(url, USER, PASSWORD)
        } catch (e: SQLException) {
            println("Error al conectar con la base de datos MySQL: ${e.message}")
            null
        }
    }
    // Función de prueba: verificar conexión
    fun testConnection(): Boolean {
        return getConnection()?.use { conn ->
            println("Conexión establecida con éxito")
            true
        } ?: false
    }

    // Cerrar conexión (para los casos en los que no se utiliza .use)
    fun closeConnection(conn: Connection?) {
        try {
            conn?.close()
            println("Conexión cerrada correctamente.")
        } catch (e: SQLException) {
            println("Error al cerrar la conexión: ${e.message}")
        }
    }
}