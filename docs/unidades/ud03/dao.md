# Arquitectura de Acceso a Datos (Patrón DAO)

Otra buena práctica es crear un objeto para manejar las diferentes operaciones CRUD de acceso a los datos. 

El **DAO (Data Access Object)** es un objeto que encapsula todo el acceso a la base de datos para una entidad. Abstrae la lógica de persistencia del resto de la aplicación.

**Ventajas:**

* **Separación de responsabilidades**: La lógica de negocio no se mezcla con el código SQL.
* **Mantenibilidad**: Cambios en la BD solo afectan al DAO correspondiente.
* **Reutilización**: Los métodos del DAO pueden ser invocados desde diferentes partes del programa.

Con la conexión resuelta, necesitamos una forma estructurada de realizar operaciones. Para ello, implementaremos el patrón de diseño DAO.

## El Modelo de Datos (`data class`)

Una `data class` sirve como un DTO (Data Transfer Object) que representa la estructura de un registro de la tabla. Su única función es transportar datos.

```kotlin
// Fichero: Planta.kt
data class Planta(
    val id: Int? = null, // Nulable, ya que es autogenerado por la BD
    val nombreComun: String,
    val nombreCientifico: String,
    val frecuenciaRiego: Int,
    val altura: Double
)
```

!!! warning "🎯 Práctica 3: Definir el Modelo de Datos"


1. Crea un nuevo fichero Kotlin para tu modelo (ej: `Videojuego.kt`).
2. Define una `data class` cuyas propiedades se correspondan con las columnas de tu tabla.


## Implementación del DAO

Vamos a ver el siguiente ejemplo de DAO para la tabla `plantas` de la BD `plantas.sqlite` en la que se utiliza el código de conexión del objeto **ConexionBD.kt**.

En el ejemplo se declaran funciones para leer la información de la tabla, añdir registros nuevos, modificar la información existenete y borrarla. 

Para ello se utiliza un data class **Planta.kt** con la estructura definida anteriormente.

!!! success "🔍 Ejecutar y Analizar"
    Ejecutar y Analizar: Implementación del Objeto DAO

El siguiente código muestra una implementación completa de `PlantasDAO`. Analiza cómo cada función utiliza `PreparedStatement`, gestiona los recursos con `.use` y mapea los datos entre el `ResultSet` y la `data class Planta`.

```kotlin
// Fichero: PlantasDAO.kt
import java.sql.SQLException

object PlantasDAO {

    fun listarPlantas(): List<Planta> {
        val lista = mutableListOf<Planta>()
        val sql = "SELECT * FROM plantas"
        ConexionBD.getConnection()?.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                val rs = stmt.executeQuery()
                while (rs.next()) {
                    lista.add(Planta(
                        id = rs.getInt("id"),
                        nombreComun = rs.getString("nombre_comun"),
                        nombreCientifico = rs.getString("nombre_cientifico"),
                        frecuenciaRiego = rs.getInt("frecuencia_riego"),
                        altura = rs.getDouble("altura")
                    ))
                }
            }
        }
        return lista
    }
    
    fun consultarPlantaPorId(id: Int): Planta? {
        var planta: Planta? = null
        val sql = "SELECT * FROM plantas WHERE id = ?"
        ConexionBD.getConnection()?.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, id)
                val rs = stmt.executeQuery()
                if (rs.next()) {
                    planta = Planta(
                        id = rs.getInt("id"),
                        nombreComun = rs.getString("nombre_comun"),
                        nombreCientifico = rs.getString("nombre_cientifico"),
                        frecuenciaRiego = rs.getInt("frecuencia_riego"),
                        altura = rs.getDouble("altura")
                    )
                }
            }
        }
        return planta
    }

    fun insertarPlanta(planta: Planta) {
        val sql = "INSERT INTO plantas(nombre_comun, nombre_cientifico, frecuencia_riego, altura) VALUES (?, ?, ?, ?)"
        ConexionBD.getConnection()?.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, planta.nombreComun)
                stmt.setString(2, planta.nombreCientifico)
                stmt.setInt(3, planta.frecuenciaRiego)
                stmt.setDouble(4, planta.altura)
                if (stmt.executeUpdate() > 0) {
                    println("${planta.nombreComun} insertada con éxito.")
                }
            }
        }
    }

    fun actualizarPlanta(planta: Planta) {
        if (planta.id == null) {
            println("Error: No se puede actualizar una planta sin ID.")
            return
        }
        val sql = "UPDATE plantas SET nombre_comun = ?, nombre_cientifico = ?, frecuencia_riego = ?, altura = ? WHERE id = ?"
        ConexionBD.getConnection()?.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, planta.nombreComun)
                stmt.setString(2, planta.nombreCientifico)
                stmt.setInt(3, planta.frecuenciaRiego)
                stmt.setDouble(4, planta.altura)
                stmt.setInt(5, planta.id)
                if (stmt.executeUpdate() > 0) {
                    println("Planta con ID ${planta.id} actualizada.")
                } else {
                    println("No se encontró ninguna planta con ID ${planta.id} para actualizar.")
                }
            }
        }
    }

    fun eliminarPlanta(id: Int) {
        val sql = "DELETE FROM plantas WHERE id = ?"
        ConexionBD.getConnection()?.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, id)
                if (stmt.executeUpdate() > 0) {
                    println("Planta con ID $id eliminada.")
                } else {
                    println("No se encontró ninguna planta con ID $id para eliminar.")
                }
            }
        }
    }
}
```

!!! success "🔍 Ejecutar y Analizar"
    Ejecutar y Analizar: Uso del Objeto DAO

Un ejemplo de uso de `PlantasDAO` y las llamadas a sus funciones desde **Main.kt** podría ser el siguiente:

``` kotlin
fun main() {

    // Listar todas las plantas
    println("Lista de plantas:")
    PlantasDAO.listarPlantas().forEach {
        println(" - [${it.id}] ${it.nombreComun} (${it.nombreCientifico}), riego cada ${it.frecuenciaRiego} días, altura: ${it.altura} m")
    }

    // Consultar planta por ID
    val planta = PlantasDAO.consultarPlantaPorId(3)
    if (planta != null) {
        println("Planta encontrada: [${planta.id}] ${planta.nombreComun} (${planta.nombreCientifico}), riego cada ${planta.frecuenciaRiego} días, altura: ${planta.altura} m")
    } else {
        println("No se encontró ninguna planta con ese ID.")
    }

    // Insertar plantas
    PlantasDAO.insertarPlanta(
        Planta(
            nombreComun = "Palmera",
            nombreCientifico = "Arecaceae",
            frecuenciaRiego = 2,
            altura = 8.5
        )
    )

    // Actualizar planta con id=1
    PlantasDAO.actualizarPlanta(
        Planta(
            id = 1,
            nombreComun = "Aloe Arborescens",
            nombreCientifico = "Aloe barbadensis miller",
            frecuenciaRiego = 5,
            altura = 0.8
        )
    )

    // Eliminar planta con id=2
    PlantasDAO.eliminarPlanta(2)
}
```

!!! warning "🎯 Práctica 4: Construir el DAO y orquestar la Aplicación desde Main"


1. Crea un fichero para tu DAO (ej: `VideojuegoDAO.kt`).
2. Implementa las cuatro funciones CRUD (`listar`, `insertar`, `actualizar`, `eliminar`) y una de consulta por ID (`consultarPorId`).
3. Adapta el código SQL y los parámetros a tu tabla y tu `data class`.
4. El fichero `Main.kt` orquesta las llamadas al DAO para ejecutar la lógica de la aplicación. En tu fichero `Main.kt`, utiliza los métodos de tu DAO para probar todas las operaciones sobre tu tabla de datos.
5. Inserta datos, consulta uno por su ID, actualízalo y borra otro.
6. Muestra por consola los resultados para verificar el correcto funcionamiento.
