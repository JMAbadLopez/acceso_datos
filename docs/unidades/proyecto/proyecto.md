# 💡Proyecto Intermodular: Desarrollo de Backend Seguro y Escalable

Este documento constituye el enunciado oficial para el proyecto del módulo, integrando los conocimientos adquiridos en la **Unidad 4 (Mapeo Objeto Relacional)** y la **Unidad 5 (Diseño de componentes API REST)**.

## 1. Portada y Presentación

La memoria del proyecto (entregada en formato **PDF**) debe contar con una portada profesional que incluya:

* **Nombre del Proyecto:** (Ej: "Sistema de Gestión de [Temática elegida]")
* **Módulos implicados:** Acceso a Datos
* **Curso y Grupo:** 2025-2026 - 2º DAM.
* **Componentes del equipo:** Nombres completos. En caso de ser individual, nombre del autor/a.

## 2. Índice de Contenidos

El documento debe estar correctamente paginado y contar con un índice dinámico que permita navegar por las secciones de introducción, diseño, implementación, documentación y conclusiones.

## 3. Introducción Teórica

El equipo debe realizar una labor de síntesis explicando los fundamentos tecnológicos elegidos para el proyecto:

* **¿Qué es un ORM?** Ventajas de usar **Exposed** frente a consultas JDBC tradicionales.
* **Modelado de Datos:** Importancia de la integridad referencial y la normalización.
* **Arquitectura API REST:** Explicación del protocolo HTTP, el patrón **MVC** (Modelo-Vista-Controlador) y la importancia de la asincronía en servidores modernos con **Ktor**.

## 4. Diseño de Datos

Se debe definir el universo de datos que manejará la aplicación.

* **Esquema Relacional:** Descripción y diagramas detallados de las tablas, atributos, tipos de datos y relaciones (1:N, N:M).
* **Código SQL:** Script completo para la creación de la base de datos en un entorno **MySQL / MariaDB**.

```sql
-- Ejemplo de script requerido
CREATE DATABASE mi_proyecto;
USE mi_proyecto;

CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    mail VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    fecha_nacimiento DATE
);
-- Añadir el resto de tablas del modelo
```

## 5. Desarrollo del Backend Funcional

El núcleo del proyecto consiste en un servidor funcional escrito en **Kotlin** que cumpla con los siguientes requisitos técnicos:

### 5.1 Arquitectura Modular

El código debe estar organizado estrictamente por capas:

* **Capa de Datos (DAO):** Uso de **Exposed** para todas las operaciones CRUD.
* **Capa de Dominio (DTO):** Modelos de datos serializables para el intercambio de información.
* **Capa de Servicio:** Lógica de negocio (validaciones, cálculos, seguridad) separada del controlador.
* **Controladores (Routes):** Definición de endpoints en **Ktor**.

### 5.2 Seguridad y Sesiones

* Implementación de **Session Authentication**.
* Protección de rutas: las operaciones de escritura (POST, PUT, DELETE) deben requerir una sesión activa.

### 5.3 Arquitectura y Despliegue

* **Arquitectura:** Diagrama de la arquitectura del servidor.
* **Gradle:** Configuración de **Gradle** para la generación del ejecutable (ShadowJar).
* **Despliegue:** El servidor debe ser capaz de correr de forma independiente y conectar con la BD MySQL.

## 6. Documentación de la API

Se debe incluir una guía de uso para los desarrolladores de la App Cliente. Esta documentación debe seguir el formato **Markdown**, en el archivo **README.md** del proyecto, y detallar:

* **Endpoints:** URL, Método (GET, POST, etc.) y descripción.
* **Cuerpo de petición/respuesta:** Ejemplos reales de JSON. ¿Qué envío? ¿Qué recibo?
* **Códigos de Estado:** Qué responde la API en caso de éxito (200, 201) o error (400, 401, 404).

Aquí tenéis una guía de cómo desarrollar la documentación de la API en Markdown:

[Guía de documentación de la API](https://andros.dev/blog/e6a134c6/ejemplo-en-markdown-de-como-documentar-una-api/)

### 6.1 Ejemplo de documentación de la API en markdown

````markdown
# API Documentation

## Endpoints clients

### `/api/v1/clients/`

GET - List all clients

#### Parameters

Nothing

#### Request body

```json
{}
```

#### Response

```json
[
    {
        "id": 1,
        "alias": "Client 1",
        "name": "Client 1 description",
        "logo": {
            "dark": "http://localhost:8000/media/clients/1/logo_dark.png",
            "light": "http://localhost:8000/media/clients/1/logo_light.png"
        },
    },
    {
        "id": 2,
        "alias": "Client 2",
        "name": "Client 2 description",
        "logo": {
            "dark": "http://localhost:8000/media/clients/2/logo_dark.png",
            "light": "http://localhost:8000/media/clients/2/logo_light.png"
        },
    }
]
```
```` 

## 7. Conexión con la App Cliente

Descripción técnica de cómo se integrará este backend con la aplicación cliente (que se desarrollará en otros módulos).

* Explicación del intercambio de JSON.
* Gestión de Cookies/Sesiones desde el cliente.
* Requisitos de red para el despliegue.

## 8. Conclusiones

Reflexión final sobre el proceso de desarrollo.

* Dificultades encontradas en el mapeo ORM.
* Ventajas detectadas al usar una arquitectura basada en servicios.
* Posibles mejoras futuras del sistema.

## 🎯 Criterios de Entrega

1. **Memoria Técnica (PDF):** Con todos los puntos anteriores desarrollados.
2. **Backend Funcional:** Código fuente en un repositorio o carpeta comprimida.
3. **Script SQL:** Para la recreación del entorno.
4. **README.md:** Documentación de la API.
5. **Demostración:** Verificación de los endpoints mediante Postman.
