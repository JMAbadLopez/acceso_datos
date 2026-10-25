# UD5 — Diseño de Componentes. API REST

!!! abstract "Resultado de Aprendizaje"
    - **RA6** - Programa componentes de acceso a datos identificando las características que debe poseer un componente y utilizando herramientas de desarrollo.

## Objetivos de la Unidad

| # | Objetivo |
| :---: | :--- |
| 1 | Comprender los principios REST y el patrón MVC aplicado a Ktor/Exposed |
| 2 | Configurar un proyecto Ktor con los plugins necesarios (Routing, ContentNegotiation, Serialization) |
| 3 | Integrar la capa de persistencia (Exposed) con la capa de negocio (Servicio) y la de presentación (Rutas) |
| 4 | Implementar un CRUD completo en una API REST con Ktor |
| 5 | Gestionar autenticación, sesiones y despliegue de la API |

---

## Guía de Uso

Estos apuntes están diseñados para un aprendizaje práctico. A lo largo de la unidad se aplicarán los conceptos teóricos para construir, paso a paso, una aplicación completa de gestión de datos.

La temática de la aplicación es de libre elección, pero la estructura y los pasos a seguir serán comunes. Intercaladas con la teoría y los ejemplos, se utilizarán las siguientes cajas de contenido:

!!! success "🔍 Ejecutar y Analizar"
    Contienen fragmentos de código que deben ser ejecutados y comprendidos en detalle. El objetivo es observar su funcionamiento y salida.

!!! warning "🎯 Práctica para Aplicar"
    Indican la necesidad de programar y aplicar los conceptos aprendidos para avanzar en el desarrollo del proyecto personal.

!!! danger "📁 Entrega"
    Marcan los puntos de entrega del trabajo, que serán revisados y calificados por el profesor.

## 1. Introducción a las APIs REST

Una **API REST (Representational State Transfer)** es un conjunto de principios arquitectónicos que definen cómo los sistemas de software en la web se comunican entre sí. Se basa en el concepto de **recursos**, que son entidades (como `usuario`, `producto`, `tarea`) accesibles a través de URLs.

### 1.1 Elementos Clave

| Elemento | Descripción | Ejemplo |
| :--- | :--- | :--- |
| **Recurso** | Una entidad a la que se accede, identificada por una URL. | `/usuarios`, `/usuarios/5` |
| **Verbos HTTP** | Indican la acción que se desea realizar sobre el recurso (CRUD). | GET, POST, PUT, DELETE |
| **Representación** | El formato en que se envía o recibe el recurso (JSON o XML). | `{"id": 1, "nombre": "Atenea"}` |
| **Códigos de Estado** | Indican el resultado de la operación (éxito, error, etc.). | 200 OK, 201 Created, 404 Not Found |

### 1.2 Verbos HTTP y CRUD

| Verbo HTTP | Operación CRUD | Descripción | Ejemplo de Ruta |
| :--- | :--- | :--- | :--- |
| **GET** | Read (Lectura) | Recuperar uno o varios recursos. | `GET /usuarios` |
| **POST** | Create (Creación) | Crear un nuevo recurso. | `POST /usuarios` |
| **PUT** | Update (Actualización) | Reemplazar completamente un recurso. | `PUT /usuarios/5` |
| **DELETE** | Delete (Borrado) | Eliminar un recurso específico. | `DELETE /usuarios/5` |

---

## 2. El Patrón Modelo-Vista-Controlador (MVC)

El patrón **MVC** es la columna vertebral de nuestra arquitectura. Su objetivo es separar las responsabilidades de la aplicación en tres capas interconectadas para mejorar la modularidad y la escalabilidad.

### 2.1 Componentes y Mapeo a Ktor/Exposed

| Componente | Rol en MVC | Mapeo en Ktor/Exposed | Unidad y Archivo |
| :--- | :--- | :--- | :--- |
| **Modelo (M)** | Gestión de datos y lógica de persistencia. | **Capa DAO (Exposed) y DTOs.** | Unidad 4: `UsuariosDAO.kt` |
| **Controlador (C)** | Recibe peticiones HTTP, delega la lógica y devuelve la respuesta. | **Rutas Ktor (`route { ... }`) y Capa de Servicio.** | Unidad 5: `UsuarioRoutes.kt` |
| **Vista (V)** | Formato de presentación de los datos al usuario. | **Serialización JSON** (Usando `ContentNegotiation`). | Unidad 5: Respuestas JSON |

### 2.2 Diagrama del Flujo MVC con Ktor/Exposed

El flujo de una petición (por ejemplo, `POST /usuarios`) sigue una ruta clara a través de las capas:

```mermaid
graph TD
    A[Cliente] -->|1. Petición HTTP| B(Controller Ktor);
    B -->|2. Llama Servicio| C(Service);
    C -->|3. Llama DAO| D(DAO Persistencia);
    D -->|4. Ejecuta Transacción| E[BD MySQL Exposed];
    E -->|5. Retorna ID| D;
    D -->|6. Retorna DTO| C;
    C -->|7. Devuelve JSON| B;
    B -->|8. Respuesta 201 Created| A;
```

---

## 3. Configuración Inicial de un Proyecto Ktor

### ¿Qué es Ktor?

![Ktor](../../assets/images/ud05/ud5_1.png)

**Ktor** es un framework de código abierto creado por **JetBrains** (los mismos creadores de Kotlin e IntelliJ) diseñado para construir aplicaciones conectadas de forma rápida, asíncrona y eficiente. A diferencia de otros frameworks más pesados, Ktor destaca por:

* **Asincronía nativa:** Utiliza corrutinas de Kotlin para manejar miles de conexiones simultáneas con un consumo mínimo de recursos.
* **Modularidad (Plugins):** No carga nada que no necesites. Si quieres usar JSON, instalas el plugin de serialización; si quieres seguridad, instalas el de autenticación. Esto lo hace extremadamente ligero.
* **Kotlin puro:** Está escrito desde cero en Kotlin, lo que nos permite usar todas las ventajas del lenguaje (DSL, tipado fuerte, funciones de extensión) de forma natural.

Necesitamos una estructura base simple de Ktor que nos permita organizar estas potentes funcionalidades.

### 3.1 Creación del Proyecto con IntelliJ Wizard

La forma más sencilla y profesional de empezar es utilizando el asistente de proyectos de IntelliJ IDEA. Sigue estos pasos:

1. **Nuevo Proyecto:** Selecciona File > New > Project... y elige Ktor en el menú de la izquierda.

   ![Ktor](../../assets/images/ud05/ud5_2.png)

2. **Configuración Básica:**
    * Name: El nombre de tu aplicación.
    * Build System: Selecciona Gradle Kotlin.
    * Engine: Usaremos Netty (es el motor de servidor más robusto y común).
3. **Selección de Plugins (Añadir dependencias):** En la siguiente pantalla del asistente, debemos buscar e instalar los siguientes plugins esenciales:
    * Routing: Para poder definir las rutas (/usuarios, etc.) de nuestra API.
    * Content Negotiation: Fundamental para que el servidor pueda "negociar" el formato de los datos (JSON).
    * kotlinx.serialization: El motor que convertirá nuestros objetos Kotlin a JSON automáticamente.

    ![Ktor](../../assets/images/ud05/ud5_3.png)

    !!! info "Resolución de errores"
        Es posible que la consola nos muestre algún error de versiones. Sólo tienes que pulsar en el primer enlace de *Possible solutions* para solucionarlo.

        ![Ktor](../../assets/images/ud05/ud5_4.png)

4. **Estructura inicial:** Ahora nuestro proyecto está preparado para **Ktor**. Fíjate que nos ha creado una serie de archivos inciales (iremos modificando la estructura del proyecto).
   ![Ktor](../../assets/images/ud05/ud5_5.png)

Al finalizar, IntelliJ generará toda la estructura de archivos necesaria y configurará el *build.gradle.kts* por ti.

Para testear que todo ha ido bien, lanza la función **main** del archivo **Application.kt**. Mostrará una ruta donde el servidor de tu API esté funcionando y *escuchando* peticiones (por ejempo, <http://0.0.0.0:8080>).

Haz click y se abrirá un navegador con nuestro primer *Hello world!*

![Ktor](../../assets/images/ud05/ud5_6.png)

!!! example "Autoevaluación"

    **Pregunta 1: Una API REST recibe una petición para modificar los datos de un usuario existente. ¿Qué verbo HTTP es el correcto para esta operación?**

    A) GET — porque se recuperan los datos actuales antes de modificarlos.

    B) POST — porque se envía información nueva al servidor.

    C) PUT — porque reemplaza completamente el recurso identificado por la URL.

    D) PATCH — siempre, ya que es el único verbo que permite modificar datos.

    ??? quote "Solución"
        ❌ A) GET solo recupera datos; nunca modifica el estado del servidor. Es una operación de solo lectura.

        ❌ B) POST se usa para crear un recurso nuevo. Si el recurso ya existe, lo correcto no es usar POST para actualizarlo.

        ✅ C) PUT es el verbo estándar REST para actualizar un recurso existente completo. Se dirige a una URL con el ID del recurso (ej: `PUT /usuarios/5`) y reemplaza todos sus campos con los del body JSON.

        ❌ D) PATCH también sirve para actualizar, pero solo modifica parcialmente un recurso (solo los campos que se envíen). En nuestra API usamos PUT porque enviamos el recurso completo.

    **Pregunta 2: ¿Para qué sirve el plugin `ContentNegotiation` de Ktor junto con `kotlinx-serialization`?**

    A) Para cifrar las peticiones HTTP entre el cliente y el servidor.

    B) Para convertir automáticamente los objetos Kotlin en JSON al enviar respuestas, y JSON en objetos Kotlin al recibir peticiones.

    C) Para gestionar la autenticación de usuarios mediante tokens JWT.

    D) Para permitir que la API acepte peticiones desde cualquier dominio (CORS).

    ??? quote "Solución"
        ❌ A) El cifrado es responsabilidad de HTTPS/TLS, que se configura a nivel de servidor o proxy, no de `ContentNegotiation`.

        ✅ B) `ContentNegotiation` es el "traductor" de la API. Con `kotlinx-serialization`, serializa objetos Kotlin (`data class`) a JSON al enviar respuestas (`call.respond(objeto)`) y deserializa JSON a objetos Kotlin al leer el body (`call.receive<MiDTO>()`).

        ❌ C) La autenticación JWT en Ktor se gestiona con el plugin `Authentication` y su proveedor `jwt`. `ContentNegotiation` no interviene en la verificación de tokens.

        ❌ D) CORS (Cross-Origin Resource Sharing) se gestiona en Ktor con el plugin `CORS`. `ContentNegotiation` solo se ocupa del formato de los datos, no de los permisos de origen.

## 🎯 Práctica 1. Creación del Proyecto con Ktor

!!! warning "🎯 Práctica 1. Creación del Proyecto con Ktor"
    1. Crea tu nuevo proyecto Ktor siguiendo los pasos del asistente mencionados arriba.
    2. Una vez generado, localiza el archivo Application.kt dentro de la carpeta core o plugins (dependiendo de la versión).
    3. Explora el archivo Routing.kt y modifica el mensaje de la ruta principal con un mensaje personalizado.
    4. Lanza el servidor y verifica que responde en `http://0.0.0.0:8080`.
