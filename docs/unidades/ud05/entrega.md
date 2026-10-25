# Entrega: Backend del Proyecto Intermodular

!!! danger "📁 Entrega Final"
    Esta es la entrega definitiva de la UD5. El backend debe arrancar sin errores y superar todas las pruebas de Postman para poder ser calificado.

## 🎯 Proyecto final: Backend de tu Proyecto Intermodular

!!! warning "🎯 Proyecto final: Backend del Proyecto Intermodular"

    A lo largo de las prácticas has construido una **API REST completa con Ktor** siguiendo el modelo `Usuarios`. Ahora aplicarás exactamente los mismos patrones para construir el **backend del Proyecto Intermodular de tu grupo**: la API que dará servicio a vuestra aplicación cliente.

    El proyecto se entrega como **memoria técnica en PDF** más el **código fuente**. Los puntos de la entrega corresponden a las secciones que se detallan a continuación.

    ---

    ### 1. Portada y Presentación

    La memoria debe incluir una portada con:

    * Nombre del proyecto (ej: "Sistema de Gestión de [Temática elegida]").
    * Módulos implicados: *Acceso a Datos*.
    * Curso y grupo: 2026-2027 · 2º DAM.
    * Nombres completos de los componentes del equipo.

    ---

    ### 2. Índice de Contenidos

    El documento debe estar correctamente paginado e incluir un **índice dinámico** que permita navegar por todas las secciones: introducción, diseño, implementación, documentación y conclusiones.

    ---

    ### 3. Introducción Teórica

    Síntesis de los fundamentos tecnológicos del proyecto:

    * **¿Qué es un ORM?** Ventajas de Exposed frente a JDBC puro.
    * **Modelado de Datos**: importancia de la integridad referencial y la normalización.
    * **Arquitectura API REST**: protocolo HTTP, patrón MVC y rol de Ktor.

    ---

    ### 4. Diseño de Datos

    * **Esquema Relacional**: diagramas de tablas, atributos, tipos y relaciones (1:N, N:M).
    * **Script SQL**: fichero completo con `CREATE DATABASE`, `CREATE TABLE` e inserciones iniciales para recrear el entorno en cualquier máquina.

    ---

    ### 5. Desarrollo del Backend Funcional

    El backend debe cumplir estos requisitos técnicos:

    #### 5.1 Arquitectura Modular (por capas)

    ```text
    src/main/kotlin/com/tu.proyecto/
    ├── core/        → ConexionDB.kt
    ├── data/        → Tables + DAOs (Exposed, CRUD completo)
    ├── domain/      → DTOs serializables, UserSession, LoginRequest
    ├── services/    → Lógica de negocio (validaciones, reglas)
    ├── routes/      → Endpoints Ktor
    ├── plugins/     → Serialization, Routing, Security
    └── Application.kt
    ```

    #### 5.2 Seguridad y Sesiones

    * Implementación de **Session Authentication**.
    * Rutas de escritura (POST, PUT, DELETE) protegidas con `authenticate("auth-session")`.
    * Rutas de lectura (GET) públicas.

    #### 5.3 Arquitectura y Despliegue

    * Diagrama de la arquitectura del servidor.
    * Configuración de **Gradle** para la generación del ejecutable (`buildFatJar`).
    * El JAR debe arrancar de forma independiente y conectar con la BD MySQL.

    ---

    ### 6. Documentación de la API (README.md)

    El repositorio debe incluir un `README.md` con:

    * Tabla de **endpoints**: URL, método, descripción.
    * **Ejemplos de JSON** de petición y respuesta para cada endpoint.
    * **Códigos de estado** utilizados (200, 201, 400, 401, 404, 409...).

    Puedes usar como referencia la [guía de documentación de APIs en Markdown](https://andros.dev/blog/e6a134c6/ejemplo-en-markdown-de-como-documentar-una-api/).

    ---

    ### 7. Conexión con la App Cliente

    Descripción técnica de la integración entre el backend y la aplicación cliente:

    * Intercambio de JSON entre servidor y cliente.
    * Gestión de cookies/sesiones desde el cliente.
    * Requisitos de red para el despliegue conjunto.

    ---

    ### 8. Conclusiones

    Reflexión final sobre el proceso de desarrollo:

    * Dificultades encontradas en el mapeo ORM o en la configuración de Ktor.
    * Ventajas detectadas al usar arquitectura basada en servicios.
    * Posibles mejoras futuras del sistema.

    ---

    ### 🎯 Criterios de Entrega

    | # | Entregable | Descripción |
    | :---: | :--- | :--- |
    | 1 | **Memoria Técnica (PDF)** | Todos los puntos anteriores desarrollados. |
    | 2 | **Backend Funcional** | Código fuente en repositorio o carpeta comprimida. |
    | 3 | **Script SQL** | Para recrear la BD en cualquier entorno. |
    | 4 | **README.md** | Documentación completa de la API. |
    | 5 | **Demostración (Postman)** | Verificación en vivo de los endpoints principales. |
