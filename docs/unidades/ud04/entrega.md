# Entrega: Catálogo de Videojuegos con Exposed ORM

!!! danger "📁 Entrega Final"
    Esta es la entrega definitiva de la UD4. El proyecto debe ejecutarse sin errores para poder ser calificado.

## 🎯 Proyecto final: Catálogo de Videojuegos con ORM

!!! warning "🎯 Proyecto final: Catálogo de Videojuegos con ORM"

    A lo largo de las prácticas has construido el **Catálogo de Videojuegos** usando **Kotlin Exposed ORM**. En esta entrega final integras y consolidas todo lo construido.

    !!! warning "Requisito imprescindible"
        No debe existir ningún rastro del proyecto de ejemplo del profesor (`Usuarios`, `UsuarioDTO`, etc.). La entrega contendrá únicamente los archivos de **tu proyecto intermodular**: `ConexionDB.kt`, tus `data classes`, tus `object Table`, tus funciones DAO y `Main.kt`.

    ### Base de Datos (MySQL)

    * La tabla principal `videojuegos` con los cinco campos (`id`, `titulo`, `genero`, `anio`, `nota`) definida mediante `object Videojuegos : Table(...)`.
    * La tabla `listas` definida mediante `object Listas : Table(...)`.
    * Ambas tablas deben gestionarse con `SchemaUtils.create(Videojuegos, Listas)` dentro de `conectar()`.
    * La BD debe estar **poblada** con al menos los 5 videojuegos del proyecto y 3 listas.

    ### Estructura del Código (Kotlin)

    * **`ConexionDB.kt`**: configurado para MySQL con `Database.connect(...)` y `SchemaUtils.create(...)`.
    * **Esquemas**: `Videojuegos.kt` y `Listas.kt` en el paquete `schemas`.
    * **Data classes**: `Videojuego.kt` y `Lista.kt`.
    * **DAOs**: `VideojuegoDAO.kt` con el **CRUD completo** (insert, selectAll, update, delete) y las **4 consultas avanzadas** de la Práctica 4.

    ### Aplicación Principal (`Main.kt`)

    El `main` debe demostrar que toda la funcionalidad está operativa:

    1. **Listar** todos los videojuegos.
    2. **Insertar** al menos un videojuego nuevo.
    3. **Actualizar** la nota de un videojuego existente.
    4. **Eliminar** un videojuego (respetando la integridad referencial si tiene listas asociadas).
    5. **Consultar** usando las funciones de la Práctica 4:
        - Filtrar por género.
        - Buscar por fragmento de título.
        - Obtener el Top 3 por nota.
        - Obtener videojuegos desde un año determinado.

    Ejemplo de salida esperada:

    ```text
    Conexión a MySQL establecida con éxito usando Exposed.
    Esquema de la tabla 'Videojuegos' verificado/creado.
    Esquema de la tabla 'Listas' verificado/creado.

    === CATÁLOGO DE VIDEOJUEGOS ===
    ID: 1 | Zelda: Breath of the Wild | Aventura | 2017 | 9.3
    ID: 2 | Red Dead Redemption 2 | Acción | 2018 | 9.5
    ID: 3 | Hades | Roguelike | 2020 | 9.0
    ID: 4 | Hollow Knight | Metroidvania | 2017 | 8.9
    ID: 5 | Celeste | Plataformas | 2018 | 9.0

    === TOP 3 POR NOTA ===
    1. Red Dead Redemption 2 — 9.5
    2. Zelda: Breath of the Wild — 9.3
    3. Hades — 9.0

    === GÉNERO: Roguelike ===
    Hades (2020) — 9.0
    ```

    !!! danger "Reto extra (+1 punto)"
        Implementa también `ListaDAO.kt` con el CRUD completo para `listas` y la función `fun anyadirVideojuegoALista(idVideojuego: Int, idLista: Int)` usando una **transacción manual** (igual que en la UD3) para garantizar la integridad de la inserción en `listas_videojuegos`.

    ### Documentación: El Fichero LEEME.md

    Tu proyecto debe incluir un fichero `LEEME.md` con al menos estas secciones:

    * **Nombre del proyecto y breve descripción**.
    * **Ventajas de Exposed frente a JDBC puro**: ¿qué mejoras observas en el código?
    * **Estructura de la Base de Datos**: esquema de las tablas y sus relaciones.
    * **Instrucciones de Ejecución**: requisitos (JDK, MySQL), cómo crear la BD y cómo configurar la conexión.
