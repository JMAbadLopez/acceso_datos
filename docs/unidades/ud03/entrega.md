# Entrega: Aplicación CRUD Funcional

!!! danger "📁 Entrega Final"
    Esta es la entrega definitiva de la UD3. El proyecto debe ejecutarse sin errores para poder ser calificado.

## 🎯 Proyecto final: Catálogo de Videojuegos con BD Relacional

!!! warning "🎯 Proyecto final: Catálogo de Videojuegos con BD Relacional"

    A lo largo de las prácticas has construido una aplicación de gestión de un catálogo de videojuegos conectada a una base de datos relacional. Has implementado el patrón DAO, gestionado transacciones y migrado la BD a un SGBD cliente-servidor.

    En esta entrega final integras todo lo construido y lo cierras con las **tres tablas relacionadas** del proyecto.

    !!! warning "Requisito imprescindible"
        No debe existir ningún rastro de los ejemplos del profesor (`PlantasDAO`, `ConexionBD` con `plantas.sqlite`, etc.). La entrega contendrá únicamente los archivos de **tu proyecto**: `ConexionBD.kt`, tus `data classes`, tus DAOs y `Main.kt`.

    ### Base de Datos (MySQL o PostgreSQL)

    * Las **tres tablas** del proyecto (`videojuegos`, `listas`, `listas_videojuegos`) con sus relaciones implementadas mediante **Claves Primarias (PK)** y **Claves Foráneas (FK)**.
    * Las tablas deben estar **pobladas** con al menos 5 videojuegos y 3 listas.
    * Fichero `datos/videojuegos.sql` con el **esquema completo** (CREATE TABLE) y la **inserción de datos** (INSERT).

    ### Estructura del Código (Kotlin)

    * **`ConexionBD.kt`**: configurado para MySQL o PostgreSQL.
    * **Modelos de Datos**: una `data class` por tabla → `Videojuego.kt`, `Lista.kt`, `ListaVideojuego.kt`.
    * **DAOs**: un fichero DAO por modelo → `VideojuegoDAO.kt`, `ListaDAO.kt`, `ListaVideojuegoDAO.kt`. Los tres implementan el **CRUD completo**.
    * **Reto extra**: `ListaVideojuegoDAO` incluye el método `fun obtenerVideojuegosDeLista(idLista: Int): List<Videojuego>`.

    ### Aplicación Principal (`Main.kt`)

    El `main` debe demostrar que toda la funcionalidad está operativa:

    1. **Listar** todos los videojuegos y todas las listas.
    2. **Insertar**, **Actualizar** y **Eliminar** videojuegos y listas (respetando la integridad referencial al eliminar).
    3. **Añadir** un videojuego a una lista usando la función de transacción de la Práctica 5.
    4. **(Reto extra) Listar** todos los videojuegos de una lista concreta.

    Ejemplo de menú:

    ```text
    ===== CATÁLOGO DE VIDEOJUEGOS =====
    --- VIDEOJUEGOS ---
    1. Ver todos los videojuegos
    2. Buscar videojuego por ID
    3. Añadir videojuego
    4. Modificar videojuego
    5. Eliminar videojuego

    --- LISTAS ---
    6. Ver todas las listas
    7. Crear lista
    8. Eliminar lista

    --- LISTAS Y VIDEOJUEGOS ---
    9. Añadir videojuego a una lista
    10. (EXTRA) Ver videojuegos de una lista
    0. Salir
    ====================================

    Escoge una opción:
    ```

    ### Documentación: El Fichero LEEME.md

    Tu proyecto debe incluir un fichero `LEEME.md` con al menos estas secciones:

    * **Nombre del proyecto y breve descripción**.
    * **Estructura de la Base de Datos**: diseño de las tres tablas y sus relaciones (puedes incluir el diagrama ER o el script SQL).
    * **Instrucciones de Ejecución**: requisitos, cómo crear la BD y cómo configurar la conexión.
    * **Decisiones de Diseño** (Opcional pero Recomendado): ¿por qué esas tablas? ¿MySQL o PostgreSQL?

    Puedes consultar la guía de la [Unidad 2](../ud02/documentacion_final.md) como referencia de estructura y formato.
