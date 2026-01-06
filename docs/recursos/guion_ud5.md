# 📋 Índice Propuesto Revisado: Unidad 5 - Creación de API REST

## Módulo 5.1: Introducción, Patrón MVC y Ktor (CE d)

* **1.1 Introducción a las APIs REST:** Conceptos, recursos, métodos (GET, POST, PUT, DELETE) y códigos de estado.
* **1.2 El Patrón Modelo-Vista-Controlador (MVC):** Explicación detallada y mapeo de cada componente a la arquitectura Ktor/Exposed.
  * **Modelo (M):** DTOs y Capa DAO/Persistencia (Unidad 4).
  * **Vista (V):** JSON (nuestra respuesta de la API).
  * **Controlador (C):** Rutas Ktor y Capa de Servicio/Lógica.

* **1.3 Configuración Inicial de un Proyecto Ktor:** Estructura de un proyecto base.

## Módulo 5.2: Configuración de Ktor y la Capa de Persistencia (CE c)

* **2.1 Dependencias Clave:** Instalación de Ktor, *plugins* (Routing, Content Negotiation) y el módulo de Exposed.
* **2.2 Configuración de Serialización JSON:** Usando el *plugin* `ContentNegotiation` y **Kotlinx.Serialization** (CE c).
* **2.3 Integración de Exposed:** Adaptación del `object ConexionDB` para inicializar la base de datos *antes* de iniciar el servidor Ktor.

## Módulo 5.3: Modelos, Contratos y Capas de Servicio (CE d)

* **3.1 Revisión de DTOs:** Adaptación del `UsuarioDTO` para que sea serializable por JSON.
* **3.2 Implementación de la Capa de Servicio:** Creación de un `object UsuariosService` que usa el `UsuariosDAO` para la persistencia y aplica la lógica de negocio.
* *Propósito:* Separar la lógica de negocio (Service) del acceso a datos (DAO) y del *routing* (Controller).
* **3.3 El Contrato API:** Definición de los *endpoints* y el formato de los datos de entrada/salida.

## Módulo 5.4: Implementación de Rutas y Flujos CRUD (CE b, e)

* **4.1 Configuración de Rutas (Routing):** Organización de las rutas por recursos (`/usuarios`).
* **4.2 Implementación del CRUD Completo (CE b, e):**
* **GET /usuarios y GET /usuarios/{id}:** Listar y Recuperar recursos (Lectura).
* **POST /usuarios:** **Creación** de un nuevo recurso (Incluye Deserialización).
* **PUT /usuarios/{id}:** **Modificación** de un recurso existente.
* **DELETE /usuarios/{id}:** **Borrado** de un recurso.
* **4.3 Desafío de Validación:** Primeros mecanismos básicos para verificar datos de entrada.

## Módulo 5.5: Gestión de Errores y Diseño Usable (CE h, a)

* **5.1 Manejo de Excepciones Globales:** Configuración del bloque `StatusPages` de Ktor para capturar errores y devolver JSON con códigos HTTP adecuados (CE h).
* *Ejemplo:* Capturar un `UserNotFoundException` y devolver `HTTP 404 Not Found`.
* **5.2 Validación de Datos Avanzada:** Implementación de mecanismos de validación de campos complejos.
* **5.3 Diseño de API Usable (CE a):** Evaluación de la API desde la perspectiva del cliente (nombres de recursos claros, uso correcto de verbos HTTP).
