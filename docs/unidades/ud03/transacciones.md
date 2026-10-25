# Transacciones

## Gestión de Transacciones

Una transacción es una secuencia de una o más operaciones sobre una base de datos que deben ejecutarse como una unidad indivisible. El objetivo es asegurar que todas las operaciones se completen con éxito o, en caso de fallo, ninguna de ellas se aplique, manteniendo así la base de datos en un estado consistente.

> Por ejemplo, en una transferencia bancaria, si falla el abono en una cuenta, se cancela el débito en la otra.

Las transacciones se gestionan mediante comandos como `BEGIN TRANSACTION` (para iniciar), `COMMIT` (para confirmar los cambios) y `ROLLBACK` (para deshacer los cambios en caso de error). Este mecanismo protege la base de datos frente a fallos parciales y situaciones de concurrencia, asegurando que los datos siempre reflejen una realidad válida y coherente.

### Propiedades de una transacción (ACID)

Las transacciones garantizan propiedades fundamentales, conocidas por el acrónimo ACID:

Propiedad|Significado breve
---------|-------------------
Atomicidad|Todas las operaciones se ejecutan o ninguna lo hace
Consistencia|El sistema pasa de un estado válido a otro
Isolación|No interfiere con otras transacciones simultáneas
Durabilidad| Una vez confirmada, el cambio permanece

### Comandos clave

Para controlar correctamente una transacción desde el código, necesitamos usar tres comandos clave:

- **commit()**: Confirma los cambios realizados por la transacción, haciéndolos permanentes.
- **rollback()**: Revierte todos los cambios realizados durante la transacción actual, volviendo al estado anterior.

Por defecto, muchas conexiones JDBC están en modo **auto-commit**, es decir, cada operación se ejecuta y confirma automáticamente. Para usar transacciones de forma manual, debes desactivar este modo:

```kotlin
conexion.autoCommit = false
```

### Excepciones

El manejo de excepciones en las transacciones es absolutamente necesario para garantizar que los datos de la base de datos no queden en un estado inconsistente o corrupto cuando ocurre un error durante una operación.

Una transacción sin control de errores no es una transacción segura. Siempre hay que estar preparado para deshacer todo si algo sale mal.

Cuando realizamos varias operaciones dentro de una misma transacción (por ejemplo, una transferencia bancaria), pueden ocurrir errores como:

- un fallo de conexión,
- un ID incorrecto,
- un valor nulo inesperado,
- un error lógico como saldo insuficiente.
  
Si no controlamos esos errores, la base de datos podría:

- Aplicar solo algunas de las operaciones
- Dejar datos parcialmente modificados
- Generar resultados incorrectos para otros usuarios

Para evitarlo se utiliza un bloque **try-catch** que:

- Llama a commit() si todo sale bien
- Llama a rollback() si ocurre cualquier excepción

``` kotlin
        try {
            conexion.autoCommit = false

            // Varias operaciones SQL...
            conexion.commit()  // Todo bien
        } catch (e: Exception) {
            conexion.rollback()  // Algo falló → revertir
            println("Error en la transacción. Cambios anulados.")
        }
```

!!! success "🔍 **Ejecutar y Analizar: Implementación de una Transacción**"
    Prueba el código de ejemplo y verifica que la salida por consola es:

    ```
    Transacción completada.
    ```

    Si el ID no existe en la BD, no se imprime nada (el `executeUpdate` afecta 0 filas). Si hay un error de conexión se imprimirá el mensaje de `catch` y los cambios quedarán revertidos.

Este método de ejemplo actualiza dos registros de forma segura dentro de una transacción.

```kotlin
import java.sql.Connection

fun actualizarPreciosConTransaccion(id1: Int, nuevoPrecio1: Double, id2: Int, nuevoPrecio2: Double) {
    var conn: Connection? = null
    try {
        conn = ConexionBD.getConnection()
        conn?.autoCommit = false // 1. Iniciar transacción

        conn?.prepareStatement("UPDATE plantas SET precio = ? WHERE id = ?")?.use { stmt ->
            stmt.setDouble(1, nuevoPrecio1)
            stmt.setInt(2, id1)
            stmt.executeUpdate()
        }
        conn?.prepareStatement("UPDATE plantas SET precio = ? WHERE id = ?")?.use { stmt ->
            stmt.setDouble(1, nuevoPrecio2)
            stmt.setInt(2, id2)
            stmt.executeUpdate()
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

### Ejemplo con varias tablas

Vamos a modificar la `BD_plantas`. Para el siguiente ejemplo se han añadido a la BD las tablas `jardines`y `jardines_plantas` cuya estructura es la siguiente:

```sql
/* Creación de la tabla jardines */
CREATE TABLE jardines (
    id_jardin INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT
);
/* Inserción de un jardín de ejemplo */
INSERT INTO jardines(nombre) VALUES ('Jardín botánico de Sagunto');

/* Creación de la relación 'jardines_plantas' */
CREATE TABLE jardines_plantas (
    id_planta INTEGER,
    id_jardin INTEGER,
    cantidad INTEGER,
    FOREIGN KEY (id_planta) REFERENCES plantas (id),
    FOREIGN KEY (id_jardin) REFERENCES jardines (id_jardin)
);
```

```
Tabla jardines:
 id_jardin | nombre
-----------+-----------------------------
     1     | Jardín botánico de Sagunto

Tabla jardines_plantas (tras llevarPlantasAJardin):
 id_planta | id_jardin | cantidad
-----------+-----------+---------
     1     |     1     |   10
```

Supongamos que queremos llevar varias unidades de una planta a un jardín. El programa debe actualizar el stock en la tabla `plantas` (restando las unidades correspondientes) y añadir un registro en la tabla `jardines_plantas` indicando el jadín, la planta y la cantidad.

Ambas operaciones deben realizarse juntas, o no realizarse ninguna. El código sería el siguiente:

``` kotlin
fun llevarPlantasAJardin(id_jardin: Int, id_planta: Int, cantidad: Int) {
    var conn: Connection? = null
    try {
        conn = ConexionBD.getConnection()
        conn?.autoCommit = false  // Iniciar transacción manual

        // Restar stock a la planta
        conn?.prepareStatement("UPDATE plantas SET stock = stock - ? WHERE id = ?").use { stmt ->
            stmt?.setInt(1, cantidad);
            stmt?.setInt(2, id_planta);
            stmt?.executeUpdate()
        }

        // Añadir línea en tabla jardines_plantas
        conn?.prepareStatement("INSERT INTO jardines_plantas(id_jardin, id_planta, cantidad) VALUES (?, ?, ?)")
            .use { stmt ->
                stmt?.setInt(1, id_jardin);
                stmt?.setInt(2, id_planta);
                stmt?.setInt(3, cantidad);
                stmt?.executeUpdate()
            }

        // Confirmar cambios
        conn?.commit()
        println("Transacción realizada con éxito.")
    } catch (e: SQLException) {
        if (e.message?.contains("UNIQUE constraint failed") == true) {
            println("Intento de insertar clave duplicada")
            conn?.rollback()
            println("Transacción revertida.")
        } else {
            throw e // otros errores, relanzamos
        }
    } finally {
        // Código que se ejecuta siempre
        println("Fin del programa.")
    }
}
```

Si no se produce ningún error se hará el `commit` y en caso contrario el `rollback`

!!! success "🔍 **Ejecutar y Analizar: Transacciones con varias tablas**"
    Crea una función `main` que llame a `actualizarPreciosConTransaccion` y a `llevarPlantasAJardin` como se muestra a continuación, y verifica que la salida por consola es:

    ```
    Transacción completada.
    Transacción realizada con éxito.
    Fin del programa.
    ```

    Comprueba también en la herramienta **Database** de IntelliJ que el stock de la planta con ID 1 (Aloe Vera) se redujo en 10 unidades y que apareció un nuevo registro en `jardines_plantas`.

```kotlin
// Por ejemplo
fun main() {
    actualizarPreciosConTransaccion(1, 0.8, 3, 1.2)
    llevarPlantasAJardin(1, 1, 10)
}
```

!!! example "Autoevaluación"

    **Pregunta 8: El administrador del herbario quiere llevar 10 unidades de Aloe Vera (id=1) al jardín botánico (id=1). La operación requiere dos acciones: restar 10 unidades del `stock` en `plantas` e insertar un registro en `jardines_plantas`. Si el programa falla justo después de la primera operación (por un corte de luz), ¿qué propiedad ACID garantiza que el `stock` NO quede reducido sin el registro en `jardines_plantas`?**

    A) Consistencia — el sistema comprueba que los datos siguen siendo válidos tras cada operación individual.

    B) Durabilidad — los cambios confirmados permanecen aunque el sistema falle después.

    C) Atomicidad — o se ejecutan todas las operaciones de la transacción, o no se ejecuta ninguna.

    D) Isolación — ninguna otra transacción puede leer los datos modificados hasta que se confirmen.

    ??? quote "Solución"
        ❌ A) La consistencia garantiza que la BD pasa de un estado válido a otro, pero no decide si las operaciones se revierten o no. Esa decisión es de la atomicidad.

        ❌ B) La durabilidad actúa tras el `COMMIT`: garantiza que los cambios ya confirmados sobreviven a un fallo. Aquí el fallo ocurre antes del `COMMIT`.

        ✅ C) La atomicidad trata la transacción como una unidad indivisible. Si alguna operación no se completa, el sistema revierte todas las operaciones previas de esa transacción. Ni el `stock` quedaría reducido ni el registro en `jardines_plantas` insertado.

        ❌ D) La isolación controla qué ven otras transacciones concurrentes. No determina qué pasa con las operaciones incompletas de una misma transacción.

    **Pregunta 9: El siguiente código desactiva `autoCommit` y realiza operaciones en la BD:**

    ```kotlin
    conn?.autoCommit = false
    // ... varias operaciones SQL ...
    conn?.commit()
    ```

    **¿Qué ocurriría si se omitiera la línea `conn?.autoCommit = false` y se mantuviera la llamada a `conn?.commit()` al final?**

    A) Se lanzaría una `SQLException` porque `commit()` solo puede llamarse cuando `autoCommit` está desactivado.

    B) Cada operación SQL se confirmaría de forma inmediata e individual; si falla alguna operación intermedia, los cambios anteriores ya habrían sido aplicados y no podrían revertirse con `rollback()`.

    C) Las operaciones se acumularían en memoria hasta la llamada a `commit()`, igual que si `autoCommit` estuviera desactivado.

    D) El driver JDBC ignoraría la llamada a `commit()` sin producir ningún error, y las operaciones quedarían pendientes indefinidamente.

    ??? quote "Solución"
        ❌ A) Llamar a `commit()` con `autoCommit = true` no lanza ninguna excepción; en muchos drivers simplemente no tiene efecto o confirma lo ya confirmado automáticamente.

        ✅ B) Cuando `autoCommit = true` (el valor por defecto), cada `executeUpdate()` actúa como su propia micro-transacción y se confirma de inmediato. Si una operación posterior falla, las anteriores ya están permanentemente aplicadas y el `rollback()` ya no puede deshacerlas.

        ❌ C) En modo `autoCommit = true` las operaciones se confirman inmediatamente. Para acumularlas es necesario desactivar `autoCommit` explícitamente.

        ❌ D) `commit()` no deja operaciones en espera. En modo `autoCommit = true` puede tener efecto vacío según el driver, pero no bloquea ni deja cambios pendientes.

## 🎯 Práctica 5: Transacciones

!!! warning "🎯 Práctica 5: Transacciones"
    Para preparar la entrega final, vas a ampliar tu BD con una nueva relación. Añade las siguientes tablas a tu `videojuegos.sqlite`:

    ```sql
    CREATE TABLE listas (
        id_lista  INTEGER PRIMARY KEY AUTOINCREMENT,
        nombre    TEXT NOT NULL
    );

    INSERT INTO listas(nombre) VALUES ('Pendientes'), ('Completados'), ('Favoritos');

    CREATE TABLE listas_videojuegos (
        id_lista     INTEGER,
        id_videojuego INTEGER,
        fecha_añadido TEXT,
        FOREIGN KEY (id_lista) REFERENCES listas(id_lista),
        FOREIGN KEY (id_videojuego) REFERENCES videojuegos(id)
    );
    ```

    Implementa en tu proyecto una función `anyadirALista(idLista: Int, idVideojuego: Int, fecha: String)` que dentro de una **única transacción**:

    1. Comprueba (con un `SELECT`) que el videojuego existe en la tabla `videojuegos`.
    2. Inserta el registro en `listas_videojuegos`.

    Si cualquiera de las dos operaciones falla, la transacción debe hacer `rollback` y ningún dato debe quedar modificado.
