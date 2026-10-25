# UD4 y UD5 — ORM (Exposed) y API REST (Ktor)

**Estado:** Revisión completa completada y publicada en main (octubre 2026).

## Qué se hizo en esta revisión

### UD4 — ORM (Exposed)
- Añadidas autoevaluaciones (Q1–Q8) en los 4 archivos de la unidad.
- Añadidas salidas esperadas en todos los bloques `!!! success "🔍 Ejecutar y Analizar"`.
- Normalizados títulos de prácticas (`🎯 Práctica N. Título`).
- Prácticas 1–4 reescritas para construir el **Catálogo de Videojuegos** (id, titulo, genero, anio, nota) en lugar de ser genéricas.
- Creado `entrega.md` (sin entrada en mkdocs.yml — UD4 no tiene entrega formal).
- Driver MySQL actualizado: `mysql:mysql-connector-java:8.0.29` → `com.mysql:mysql-connector-j:8.4.0`.

### UD5 — API REST (Ktor)
- Añadidas autoevaluaciones (Q1–Q8) en los 6 archivos de la unidad.
- Normalizados títulos de prácticas.
- Las 6 prácticas construyen la API de **Usuarios** (ejemplo del profesor) — sin referencias al proyecto intermodular.
- Creado `entrega.md` que apunta al **backend del Proyecto Intermodular del alumno**; secciones (1–8) coinciden exactamente con `proyecto.md`.
- `proyecto.md` retirado de la navegación (mkdocs.yml); el fichero se conserva en disco.
- Driver MySQL unificado a `com.mysql:mysql-connector-j:8.4.0`.

### Transversal
- Curso `2025-2026` → `2026-2027` en todos los documentos y código.
- Contraseña BD `Dam2526` → `Dam2627` en todos los `.kt` de code/ y en docs.
- Driver MySQL corregido también en UD3 (coordenadas viejas con versión nueva).

## Rama de trabajo
`revision-ud04-ud05` → mergeada a `main` y publicada con `mkdocs gh-deploy`.

## Modelo de datos UD4 (Videojuegos — práctica de unidad, NO intermodular)
```kotlin
data class Videojuego(val id: Int, val titulo: String, val genero: String, val anio: Int, val nota: Double)
object Videojuegos : Table("videojuegos") { id, titulo, genero, anio, nota }
object Listas : Table("listas") { id_lista, nombre }
```

## Modelo de datos UD5 (Usuarios — ejemplo del profesor)
```kotlin
data class UsuarioDTO(val id: Int, val nombre: String, val mail: String, val password: String, val fechaNacimiento: LocalDate)
object Usuarios : Table("usuarios") { id, nombre, mail, password, fechaNacimiento }
```

## Estructura entrega UD5 (Proyecto Intermodular del alumno)
1. Portada y Presentación
2. Índice de Contenidos
3. Introducción Teórica (ORM, Modelado, API REST)
4. Diseño de Datos (esquema relacional + SQL)
5. Backend Funcional (5.1 Arquitectura, 5.2 Sesiones, 5.3 Despliegue)
6. Documentación de la API (README.md)
7. Conexión con la App Cliente
8. Conclusiones
+ Criterios de entrega: PDF, código, SQL, README, demo Postman
