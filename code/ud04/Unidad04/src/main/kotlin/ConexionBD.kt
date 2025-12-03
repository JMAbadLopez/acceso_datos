package es.gva.edu

import es.gva.edu.schema.Usuarios
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.DriverManager
import java.sql.SQLException

object ConexionBD {

    // Configuración de la unidad anterior
    private const val HOST = "192.168.56.101"
    private const val PORT = 3306
    private const val DATABASE = "orm" // Nombre de nuestra BD
    private const val USER = "dam"
    private const val PASSWORD = "Dam2526"

    // URL JDBC para MySQL, ahora utilizada por Exposed
    private val URL = "jdbc:mysql://$HOST:$PORT/$DATABASE?useSSL=false&serverTimezone=Europe/Madrid"

    // Propiedad para almacenar la instancia de la base de datos de Exposed
    lateinit var db: Database // LA INSTANCIA DE LA CONEXIÓN EXPOSED

    /**
     * Intenta establecer la conexión con Exposed e inicializa el esquema de la BD.
     */
    fun conectar() {
        try {
            // 1. Establece la conexión con la base de datos a través de Exposed
            db = Database.connect(
                url = URL,
                user = USER,
                password = PASSWORD
            )
            println("Conexión a MySQL establecida con éxito usando Exposed.")

            // Pasamos 'db' para indicar qué conexión usar.
            transaction(db) {
                // Crea la tabla 'Usuarios' (y cualquier otra tabla que definamos e importemos)
                SchemaUtils.create(Usuarios)
                println("Esquema de la tabla 'Usuarios' verificado/creado.")
            }

        } catch (e: Exception) {
            println("Error al conectar o inicializar la base de datos: ${e.message}")
            if (e.message?.contains("Communications link failure") == true) {
                println("   Pista: Asegúrate de que el servidor MySQL está activo y las credenciales son correctas.")
            }
        }
    }

    /**
     * Función de prueba para verificar la conexión, manteniendo la estructura de la Unidad 3.
     */
    fun testConexion(): Boolean {
        return try {
            DriverManager.getConnection(URL, USER, PASSWORD)?.use { conn ->
                println("Test: Conexión JDBC pura establecida con éxito.")
                true
            } ?: false
        } catch (e: SQLException) {
            println("Test: Error al conectar con JDBC puro: ${e.message}")
            false
        }
    }
}