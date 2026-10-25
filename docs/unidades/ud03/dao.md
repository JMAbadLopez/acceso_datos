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
    val stock: Int,
    val precio: Double
)
```

## 🎯 Práctica 3: Definir el Modelo de Datos

!!! warning "🎯 Práctica 3: Definir el Modelo de Datos"
    1. Crea el fichero `Videojuego.kt` con la `data class` que representa un registro de tu tabla. Debe tener exactamente los mismos campos que la tabla `videojuegos` creada en la Práctica 1, con `id` nulable (autogenerado por la BD):

        ```kotlin
        data class Videojuego(
            val id: Int? = null,
            val titulo: String,
            val genero: String,
            val anio: Int,
            val nota: Double
        )
        ```

    2. Asegúrate de que los tipos de Kotlin se corresponden con los tipos SQL (`TEXT` → `String`, `INTEGER` → `Int`, `REAL` → `Double`).

## Implementación del DAO

Vamos a ver el siguiente ejemplo de DAO para la tabla `plantas` de la BD `plantas.sqlite` en la que se utiliza el código de conexión del objeto **ConexionBD.kt**.

En el ejemplo se declaran funciones para leer la información de la tabla, añdir registros nuevos, modificar la información existenete y borrarla. 

Para ello se utiliza un data class **Planta.kt** con la estructura definida anteriormente.

!!! success "🔍 Ejecutar y Analizar: Implementación del DAO"
    Crea el fichero `PlantasDAO.kt` con el siguiente código. Analiza cómo cada función utiliza `PreparedStatement`, gestiona los recursos con `.use` y mapea los datos entre el `ResultSet` y la `data class Planta`. No tiene salida directa: es un fichero de librería que se usa desde `Main.kt`.

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
                        stock = rs.getInt("stock"),
                        precio = rs.getDouble("precio")
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
                        stock = rs.getInt("stock"),
                        precio = rs.getDouble("precio")
                    )
                }
            }
        }
        return planta
    }

    fun insertarPlanta(planta: Planta) {
        val sql = "INSERT INTO plantas(nombre_comun, nombre_cientifico, stock, precio) VALUES (?, ?, ?, ?)"
        ConexionBD.getConnection()?.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, planta.nombreComun)
                stmt.setString(2, planta.nombreCientifico)
                stmt.setInt(3, planta.stock)
                stmt.setDouble(4, planta.precio)
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
        val sql = "UPDATE plantas SET nombre_comun = ?, nombre_cientifico = ?, stock = ?, precio = ? WHERE id = ?"
        ConexionBD.getConnection()?.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, planta.nombreComun)
                stmt.setString(2, planta.nombreCientifico)
                stmt.setInt(3, planta.stock)
                stmt.setDouble(4, planta.precio)
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

!!! success "🔍 Ejecutar y Analizar: Uso del DAO desde Main"
    Añade el siguiente código a `Main.kt` y ejecútalo. Verifica que la salida por consola es:

    ```
    Lista de plantas:
     - [1] Aloe Vera (Aloe barbadensis miller), stock: 7 unidades, precio: 0.6€
     - [2] Lavanda (Lavandula angustifolia), stock: 3 unidades, precio: 1.0€
     - [3] Helecho de Boston (Nephrolepis exaltata), stock: 5 unidades, precio: 0.9€
     - [4] Bambú de la suerte (Dracaena sanderiana), stock: 4 unidades, precio: 1.5€
     - [5] Girasol (Helianthus annuus), stock: 2 unidades, precio: 3.0€
    Planta encontrada: [3] Helecho de Boston (Nephrolepis exaltata), stock: 5 unidades, precio: 0.9€
    Cactus insertada con éxito.
    Planta con ID 1 actualizada.
    Planta con ID 2 eliminada.
    ```

Un ejemplo de uso de `PlantasDAO` y las llamadas a sus funciones desde **Main.kt** podría ser el siguiente:

``` kotlin
fun main() {

    // Listar todas las plantas
    println("Lista de plantas:")
    PlantasDAO.listarPlantas().forEach {
        println(" - [${it.id}] ${it.nombreComun} (${it.nombreCientifico}), stock: ${it.stock} unidades, precio: ${it.precio}€")
    }

    // Consultar planta por ID
    val planta = PlantasDAO.consultarPlantaPorId(3)
    if (planta != null) {
        println("Planta encontrada: [${planta.id}] ${planta.nombreComun} (${planta.nombreCientifico}), stock: ${planta.stock} unidades, precio: ${planta.precio}€")
    } else {
        println("No se encontró ninguna planta con ese ID.")
    }

    // Insertar planta nueva
    PlantasDAO.insertarPlanta(
        Planta(
            nombreComun = "Cactus",
            nombreCientifico = "Cactaceae",
            stock = 15,
            precio = 4.5
        )
    )

    // Actualizar planta con id=1 (Aloe Vera)
    PlantasDAO.actualizarPlanta(
        Planta(
            id = 1,
            nombreComun = "Aloe Vera",
            nombreCientifico = "Aloe barbadensis miller",
            stock = 10,
            precio = 0.75
        )
    )

    // Eliminar planta con id=2 (Lavanda)
    PlantasDAO.eliminarPlanta(2)
}
```

!!! example "Autoevaluación"

    **Pregunta 5: Imagina que un programador construye la siguiente consulta SQL concatenando directamente la entrada del usuario:**

    ```kotlin
    val entrada = readLine() ?: ""
    val sql = "SELECT * FROM plantas WHERE nombre_comun = '" + entrada + "'"
    conn.createStatement().executeQuery(sql)
    ```

    **¿Por qué esta práctica es peligrosa y cómo la soluciona `PreparedStatement`?**

    A) Porque `createStatement` no puede ejecutar sentencias `SELECT`; `PreparedStatement` sí puede.

    B) Porque si `entrada` contiene el texto `' OR '1'='1`, la consulta devolvería todos los registros de la tabla; `PreparedStatement` escapa los parámetros automáticamente, evitando inyecciones SQL.

    C) Porque `createStatement` no admite más de 255 caracteres en la cláusula `WHERE`.

    D) Porque `PreparedStatement` es siempre más rápido que `createStatement`, independientemente del contenido de la entrada.

    ??? quote "Solución"
        ❌ A) `createStatement` puede ejecutar cualquier tipo de sentencia SQL, incluyendo `SELECT`. El problema no es funcional sino de seguridad.

        ✅ B) La concatenación directa permite inyección SQL. Con `entrada = "' OR '1'='1"`, la consulta resultante sería `WHERE nombre_comun = '' OR '1'='1'`, que devuelve todos los registros. `PreparedStatement` trata el parámetro como un valor literal, no como parte del SQL, neutralizando el ataque.

        ❌ C) No existe tal límite de caracteres en la cláusula `WHERE`. El problema es de seguridad, no de longitud.

        ❌ D) El rendimiento es una ventaja secundaria (las consultas parametrizadas pueden precompilarse). La razón principal para usar `PreparedStatement` es la seguridad frente a inyección SQL.

    **Pregunta 6: La función `consultarPlantaPorId` busca una planta por su ID. Observa el código:**

    ```kotlin
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
                        stock = rs.getInt("stock"),
                        precio = rs.getDouble("precio")
                    )
                }
            }
        }
        return planta
    }
    ```

    **¿Qué devuelve la función si no existe ninguna planta con el ID solicitado?**

    A) Lanza una `NoSuchElementException` porque `rs.next()` no puede devolver `false`.

    B) Devuelve `null`, ya que `rs.next()` retorna `false` y el bloque `if` no se ejecuta, dejando `planta` con su valor inicial.

    C) Devuelve un objeto `Planta` con todos los campos a sus valores por defecto (`0`, `""`, etc.).

    D) Lanza una `SQLException` porque llamar a `rs.getInt()` o `rs.getString()` sin registros disponibles siempre produce un error.

    ??? quote "Solución"
        ❌ A) `rs.next()` está diseñado precisamente para devolver `false` cuando no hay más filas. No lanza ninguna excepción en ese caso.

        ✅ B) Si la consulta no encuentra ningún registro, `rs.next()` devuelve `false`. El bloque `if` no se ejecuta y `planta` mantiene el valor `null` con el que fue declarada. Eso es lo que retorna la función.

        ❌ C) JDBC no crea objetos Kotlin automáticamente. Si el bloque `if` no se ejecuta, `planta` sigue siendo `null`, no un objeto con valores por defecto.

        ❌ D) Las llamadas a `rs.getInt()` y `rs.getString()` están dentro del bloque `if (rs.next())`. Si no hay registros, esas líneas nunca se ejecutan y no se produce ninguna excepción.

    **Pregunta 7: El patrón DAO separa toda la lógica de acceso a datos en un objeto independiente. ¿Cuál de las siguientes situaciones ilustra MEJOR la ventaja de esta separación?**

    A) Si se cambia el nombre de la tabla `plantas` por `herbario` en la BD, solo hay que modificar las consultas SQL dentro de `PlantasDAO`, sin tocar el código de `Main.kt`.

    B) Al usar el patrón DAO, es imposible que se produzcan errores de conexión porque el objeto gestiona todos los fallos internamente.

    C) El patrón DAO compila automáticamente el código SQL antes de ejecutarlo, lo que hace las consultas más rápidas.

    D) Usar DAO elimina la necesidad de cerrar las conexiones con `.use {}` o `close()`, porque el objeto las cierra de forma automática.

    ??? quote "Solución"
        ✅ A) Esta es la esencia del patrón: si la estructura o el nombre de la BD cambia, solo hay que actualizar el DAO. El resto del código (Main, lógica de negocio) no necesita conocer nada de SQL ni de la estructura de la BD.

        ❌ B) El DAO puede capturar y gestionar excepciones de BD, pero no las elimina. Un fallo de conexión seguirá ocurriendo si la BD no es accesible.

        ❌ C) El DAO no compila SQL. La precompilación la realiza `PreparedStatement` cuando el SGBD la soporta, con o sin DAO.

        ❌ D) En la implementación del proyecto, el cierre de conexiones se sigue gestionando con `.use {}` dentro de cada función del DAO.

## 🎯 Práctica 4: Construir el DAO y orquestar la Aplicación desde Main

!!! warning "🎯 Práctica 4: Construir el DAO y orquestar la Aplicación desde Main"
    1. Crea el fichero `VideojuegoDAO.kt` siguiendo el mismo patrón que `PlantasDAO` del ejemplo.
    2. Implementa las cinco funciones: `listarVideojuegos`, `consultarPorId`, `insertarVideojuego`, `actualizarVideojuego` y `eliminarVideojuego`. Adapta las consultas SQL y los `PreparedStatement` a los campos de tu tabla (`titulo`, `genero`, `anio`, `nota`).
    3. En `Main.kt`, orquesta las llamadas al DAO para probar todas las operaciones:
        - Lista todos los videojuegos del catálogo.
        - Consulta un videojuego concreto por su ID.
        - Inserta un videojuego nuevo (por ejemplo, `Elden Ring, Souls-like, 2022, 9.6`).
        - Actualiza la nota de uno de los videojuegos existentes.
        - Elimina un videojuego por su ID.
    4. Muestra por consola los resultados tras cada operación para verificar su correcto funcionamiento.

