# 📁 **Entrega: Aplicación CRUD Funcional**

Has construido el núcleo de la aplicación. Ahora es el momento de dar un salto cualitativo y convertir nuestro proyecto en una aplicación con una estructura de datos más robusta y profesional. Una sola tabla está bien para empezar, pero el verdadero poder de las bases de datos reside en cómo se **relacionan** las distintas informaciones.

## **El Objetivo**

En este punto de control, vamos a ampliar nuestra base de datos para que contenga **al menos tres tablas relacionadas entre sí**. Esto nos obligará a gestionar claves primarias y foráneas, y a crear la lógica necesaria en el código para manejar estas nuevas entidades.

Por ejemplo, si tu proyecto es sobre **plantas**, un **jardín** puede tener muchas **plantas** y una **planta** puede estar en varios **jardines** (relación N:N). Si es sobre **videojuegos**, un videojuego es desarrollado por un **estudio** (1:N). Si es sobre **recetas**, una **receta** tiene varios **ingredientes**, un **ingreciente** puede estar en muchas **recetas**, etc.

## **Requisitos del Proyecto**

!!! warning "Requisito imprescindible"
    No debe existir ningún rastro de los ejemplos o prácticas realizados durante la Unidad. La entrega contendrá sólo los archivos necesarios para tu proyecto: **ConexionBD, Data Classes, DAOs y Main**

Para superar esta entrega, tu aplicación debe ser funcional y cumplir con lo siguiente:

### **Base de Datos (MySQL o PostgreSQL)**

* Debe contener **al menos tres tablas** con una relación lógica entre ellas (ej: `jardines`, `plantas` y `jardines_plantas`).
* La relación debe estar correctamente implementada usando una **Clave Primaria (PK)** en la tabla principal (la del "uno") y una **Clave Foránea (FK)** (o varias) en la/s tabla/s secundaria/s (la del "muchos").
* **Puebla** las tablas de tu proyecto con datos de ejemplo.
* **Exporta la BD a un fichero**. Deberás entregar un fichero `SQL` con la **creación de las tablas y la inserción de los datos**.

### **Estructura del Código (Kotlin)**

* **Carpeta datos:** Deberá contener un fichero `SQL` con la creación e inserción de datos de tu BD (por ejemplos `datos/plantas.sql`).
* **Fichero de Conexión:** Un único `ConexionBD.kt` que gestiona el acceso a la base de datos.
* **Modelos de Datos:** Deberás tener una `data class` por cada tabla de tu base de datos (ej: `Jardin.kt`, `Planta.kt` y `JardinesPlantas.kt`).
* **Objetos de Acceso a Datos (DAOs):** Deberás tener un fichero DAO por cada modelo (ej: `JardinDAO.kt`, `PlantaDAO.kt` y `JardinesPlantasDAO.kt`).
* Los DAOs deben implementar las **operaciones CRUD** completas para sus respectivas tabla.
* **Reto extra:** El DAO de la tabla/s secundaria/s (la/s del "muchos", ej: `JardinesPlantasDAO`) debe incluir un método adicional para consultar todos los elementos que pertenecen a una entidad principal (ej: `fun obtenerPlantasPorJardin(idJardin: Int): List<Planta>`).
* **Aplicación Main:** Se explica en el siguiente punto.

### **Aplicación Principal (`Main.kt`):**

* El fichero `main` debe demostrar que toda la funcionalidad se ha implementado correctamente. Debe ser capaz de:
    1. **Insertar**, **Actualizar** y **Eliminar** (¡cuidado con la integridad referencial!) datos en todas las tablas (cuidado: por ejemplo, para relacionar plantas y jardines, necesitas tener al menos una inserción en ambas tablas).
    2. **Listar todos los elementos** de las tablas principales.
    3. **Listar los elementos de la tabla secundaria** filtrando por la principal (usando el "reto extra").

#### Ejemplo de menú de la aplicación

```text
--- JARDINES ---
1. Nuevo jardín
2. Modificar jardín
3. Eliminar jardín
4. Ver jardines

--- PLANTAS ---
5. Nueva planta
6. Modificar stock de planta
7. Eliminar planta
8. Ver plantas

--- JARDINES Y PLANTAS ---
9. Añadir planta a jardín
10. (EXTRA) Listar plantas de un jardín
11. Salir

Escoge una opción:
```

### **Documentación: El Fichero LEEME.md**

Tu proyecto debe estar documentado. En un proyecto de software el código fuente por sí solo no cuenta toda la historia y es fundamental crear documentación adicional. 

La forma estándar y más extendida de hacerlo es a través de un fichero `LEEME.md` (o `README.md`). Un proyecto sin un `LEEME.md` se considera incompleto o poco profesional.

Tu fichero `LEEME.md` debería contener, como mínimo, las siguientes secciones:

* **Nombre del proyecto y breve descripción**.
* **Estructura de la Base de Datos**: En esta sección se explica el diseño de tu base de datos.
* **Instrucciones de Ejecución**: Pasos claros y sencillos para que otra persona pueda ejecutar nuestro programa.
* **Decisiones de Diseño** (Opcional pero Recomendado): Un pequeño apartado para explicar brevemente por qué tomamos ciertas decisiones.

Puedes seguir la guía que usamos en la [Unidad 2 sobre Markdown y el fichero LEEME.md](../ud02/documentacion_final.md).
