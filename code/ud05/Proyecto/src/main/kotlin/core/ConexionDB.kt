import edu.gva.es.data.Usuarios
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object ConexionDB {
    // Configuración para el entorno de servidor del proyecto
    private const val HOST = "192.168.56.101"
    private const val PORT = 3306
    private const val DATABASE = "proyecto" // Nombre de nuestra BD
    private const val USER = "dam"
    private const val PASSWORD = "Dam2526"

    private val URL = "jdbc:mysql://$HOST:$PORT/$DATABASE?useSSL=false&serverTimezone=Europe/Madrid"

    lateinit var db: Database

    fun conectar() {
        try {
            // 1. Establece la conexión a través de Exposed
            db = Database.connect(
                url = URL,
                user = USER,
                password = PASSWORD
            )

            println("Conexión establecida con éxito en $HOST")

            transaction(db) {
                // Crea la tabla 'Usuarios' (y cualquier otra tabla que definamos e importemos)
                SchemaUtils.create(Usuarios)
                println("Esquema de la tabla 'Usuarios' verificado/creado.")
            }

        } catch (e: Exception) {
            println("Error al conectar con la base de datos: ${e.message}")
        }
    }
}