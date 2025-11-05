# 📁 **Entrega: Aplicación CRUD Funcional**

Has construido el núcleo de la aplicación. Ahora es el momento de dar un salto cualitativo y convertir nuestro proyecto en una aplicación con una estructura de datos más robusta y profesional. Una sola tabla está bien para empezar, pero el verdadero poder de las bases de datos reside en cómo se **relacionan** las distintas informaciones.

## **El Objetivo**

En este punto de control, vamos a ampliar nuestra base de datos para que contenga **al menos tres tablas relacionadas entre sí**. Esto nos obligará a gestionar claves primarias y foráneas, y a crear la lógica necesaria en el código para manejar estas nuevas entidades.

Por ejemplo, si tu proyecto es sobre **plantas**, una planta pertenece a un **jardín**. Si es sobre **videojuegos**, un videojuego es desarrollado por un **estudio**. Si es sobre **recetas**, una receta pertenece a una **categoría** (entrantes, postres, etc.).

## **Requisitos del Proyecto**

Para superar este punto de control, tu aplicación debe ser funcional y cumplir con lo siguiente:

### **Base de Datos (MySQL o PostgreSQL):**

* Debe contener **al menos tres tablas** con una relación lógica entre ellas (ej: `jardines` y `plantas`).
* La relación debe estar correctamente implementada usando una **Clave Primaria (PK)** en la tabla principal (la del "uno") y una **Clave Foránea (FK)** en la/s tabla/s secundaria/s (la del "muchos").

### **Estructura del Código (Kotlin):**

* **Fichero de Conexión:** Un único `ConexionBD.kt` que gestiona el acceso a la base de datos.
* **Modelos de Datos:** Deberás tener una `data class` por cada tabla de tu base de datos (ej: `Jardin.kt` y `Planta.kt`).
* **Objetos de Acceso a Datos (DAOs):** Deberás tener un fichero DAO por cada modelo (ej: `JardinDAO.kt` y `PlantaDAO.kt`).
* Ambos DAOs deben implementar las **operaciones CRUD** completas para su respectiva tabla.
* **Reto extra:** El DAO de la tabla secundaria (la del "muchos", ej: `JardinesDAO`) debe incluir un método adicional para consultar todos los elementos que pertenecen a una entidad principal (ej: `fun obtenerPlantasPorJardin(idFamilia: Int): List<Planta>`).

### **Aplicación Principal (`Main.kt`):**

* El fichero `main` debe demostrar que toda la funcionalidad se ha implementado correctamente. Debe ser capaz de:
    1. Insertar datos en ambas tablas (ej: crear una familia y luego crear una planta asociada a esa familia).
    2. Listar todos los elementos de ambas tablas.
    3. Listar los elementos de la tabla secundaria filtrando por la principal (usando el "reto extra").
    4. Actualizar un registro de cada tabla.
    5. Eliminar un registro de cada tabla (¡cuidado con la integridad referencial!).

### **Documentación: El Fichero LEEME.md**

Tu proyecto debe estar documentado. En un proyecto de software el código fuente por sí solo no cuenta toda la historia y es fundamental crear documentación adicional. 

La forma estándar y más extendida de hacerlo es a través de un fichero `LEEME.md` (o `README.md`). Un proyecto sin un `LEEME.md` se considera incompleto o poco profesional.

Tu fichero `LEEME.md` debería contener, como mínimo, las siguientes secciones:

* **Nombre del proyecto y breve descripción**.
* **Estructura de la Base de Datos**: En esta sección se explica el diseño de tu base de datos.
* **Instrucciones de Ejecución**: Pasos claros y sencillos para que otra persona pueda ejecutar nuestro programa.
* **Decisiones de Diseño** (Opcional pero Recomendado): Un pequeño apartado para explicar brevemente por qué tomamos ciertas decisiones.

Puedes seguir la guía que usamos en la [Unidad 2 sobre Markdown y el fichero LEEME.md](../ud02/documentacion_final.md).
