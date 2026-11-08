# Entrega: Aplicación CRUD Funcional

!!! danger "📁 Entrega Final"
    Esta es la entrega definitiva de la UD3. El proyecto debe ejecutarse sin errores para poder ser calificado.

## 🎯 Proyecto final: Catálogo de Videojuegos con BD Relacional

!!! warning "🎯 Proyecto final: Catálogo de Videojuegos con BD Relacional"

    A lo largo de las prácticas has construido una aplicación de gestión de un catálogo de videojuegos conectada a una base de datos relacional. Has implementado el patrón DAO, gestionado transacciones y migrado la BD a un SGBD cliente-servidor.

    En esta entrega final integras todo lo construido y lo amplías con una nueva entidad: **el Jugador**. Las listas de videojuegos ya no son anónimas, sino que pertenecen a un jugador concreto.

    !!! warning "Requisito imprescindible"
        No debe existir ningún rastro de los ejemplos del profesor (`PlantasDAO`, `ConexionBD` con `plantas.sqlite`, etc.). La entrega contendrá únicamente los archivos de **tu proyecto**: `ConexionBD.kt`, tus `data classes`, tus DAOs y `Main.kt`.

    ### Base de Datos (MySQL o PostgreSQL)

    El proyecto trabaja ahora con **cuatro tablas** relacionadas:

    * `videojuegos`
    * `jugadores`
    * `listas` — debe incluir una **clave foránea** hacia `jugadores`, de forma que cada lista pertenezca a un jugador.
    * `listas_videojuegos` — tabla intermedia entre `listas` y `videojuegos`.

    Todas las relaciones deben estar implementadas mediante **Claves Primarias (PK)** y **Claves Foráneas (FK)**. Deberás diseñar la tabla `jugadores` y modificar la tabla `listas` para reflejar esta relación. **El diseño del esquema SQL es parte de la entrega**: no se proporciona el código.

    Las tablas deben estar **pobladas** con al menos 5 videojuegos, 3 jugadores y 3 listas (cada una asignada a un jugador diferente).

    Fichero `datos/videojuegos.sql` con el **esquema completo** (CREATE TABLE) y la **inserción de datos** (INSERT).

    ### Estructura del Código (Kotlin)

    * **`ConexionBD.kt`**: configurado para MySQL o PostgreSQL.
    * **Modelos de Datos**: una `data class` por tabla:
        * `Videojuego.kt`
        * `Jugador.kt` ← nueva
        * `Lista.kt` ← modificada para incluir el `idJugador`
        * `ListaVideojuego.kt`
    * **DAOs**:
        * `VideojuegoDAO.kt` → CRUD completo.
        * `JugadorDAO.kt` ← nuevo → solo `insertar` y `eliminar`.
        * `ListaDAO.kt` → solo `insertar` y `eliminar` (la inserción debe recibir el `idJugador` al que pertenece la lista).
        * `ListaVideojuegoDAO.kt` → `insertar` y `fun obtenerVideojuegosDeLista(idLista: Int): List<Videojuego>`.

    ### Aplicación Principal (`Main.kt`)

    El `main` presenta un menú dividido en tres bloques que demuestra que toda la funcionalidad está operativa:

    ```text
    ===== CATÁLOGO DE VIDEOJUEGOS =====

    --- VIDEOJUEGOS ---
    1. Ver todos los videojuegos
    2. Buscar videojuego por ID
    3. Añadir videojuego
    4. Modificar videojuego
    5. Eliminar videojuego

    --- JUGADORES ---
    6. Crear jugador
    7. Eliminar jugador

    --- LISTAS ---
    8. Crear lista
    9. Eliminar lista
    10. Añadir videojuego a una lista
    11. Ver videojuegos de una lista

    0. Salir
    ====================================

    Escoge una opción:
    ```

    !!! info "Notas sobre el bloque LISTAS"
        - Al **crear una lista** (opción 8), el programa debe pedir también el ID del jugador al que pertenece. Si el jugador no existe, no se creará la lista.
        - **Añadir un videojuego a una lista** (opción 10) debe seguir usando la función con transacción implementada en la Práctica 5.
        - Al **eliminar un jugador** (opción 7), ten en cuenta la integridad referencial: si ese jugador tiene listas asociadas, deberás eliminarlas antes (o configurar la FK con `ON DELETE CASCADE`).

    ### Documentación: El Fichero LEEME.md

    Tu proyecto debe incluir un fichero `LEEME.md` con al menos estas secciones:

    * **Nombre del proyecto y breve descripción**.
    * **Estructura de la Base de Datos**: diseño de las cuatro tablas y sus relaciones (puedes incluir el diagrama ER o el script SQL).
    * **Instrucciones de Ejecución**: requisitos, cómo crear la BD y cómo configurar la conexión.
    * **Decisiones de Diseño** (Opcional pero Recomendado): ¿qué campos tiene la tabla `jugadores`? ¿cómo has gestionado la relación con `listas`? ¿MySQL o PostgreSQL?

    Puedes consultar la guía de la [Unidad 2](../ud02/documentacion_final.md) como referencia de estructura y formato.
