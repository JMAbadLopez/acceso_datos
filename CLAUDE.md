# Memory — Acceso a Datos (DAM)

## Yo
Jose Abad, profesor de DAM. Este repo es el módulo **Acceso a Datos**, publicado con MkDocs Material en GitHub Pages.

## Reglas permanentes
- **Nunca** `git commit`, `git push` ni `mkdocs gh-deploy` sin confirmación explícita de Jose. Entregar siempre los comandos para que los ejecute él.
- **Nunca** borrar archivos de forma permanente.
- Pedir permiso antes de cualquier operación que requiera autenticación o acceso a directorios sensibles. El directorio de Google Drive es el más sensible.
- Cambios grandes → probar en repo `-test` primero; migrar al oficial solo con aprobación explícita.

## Repos
| Repo | URL | Estado |
|------|-----|--------|
| **acceso_datos** | JMAbadLopez/acceso_datos | Oficial (producción) |
| **acceso_datos-test** | repo de pruebas | Para cambios grandes |

## Estructura del módulo
```
docs/
  unidades/
    ud01/   — Introducción (ficheros)
    ud02/   — Ficheros avanzados (plantas.csv, Kotlin collections)
    ud03/   — JDBC / SQLite / MySQL / DAO / Transacciones
  assets/
    resources/  — plantas.sqlite, etc.
    images/ud03/
mkdocs.yml
```

## Modelo canónico "Plantas" (UD2/UD3)
`data class Planta(id: Int?, nombreComun: String, nombreCientifico: String, stock: Int, precio: Double)`

CSV data: Aloe Vera(1,7,0.6), Lavanda(2,3,1.0), Helecho de Boston(3,5,0.9), Bambú de la suerte(4,4,1.5), Girasol(5,2,3.0)

## Modelo "Videojuegos" (proyecto del alumno en UD3)
`data class Videojuego(id: Int?, titulo: String, genero: String, anio: Int, nota: Double)`

5 juegos: Zelda BOTW, RDR2, Hades, Hollow Knight, Celeste

3 tablas: `videojuegos`, `listas`, `listas_videojuegos`

## Términos clave
| Término | Significado |
|---------|-------------|
| **UD3** | Unidad 3: JDBC, SQLite, DAO, Transacciones, MySQL |
| **bóveda** | Sistema de memoria (CLAUDE.md + memory/) |
| **plantas.sqlite** | BD de ejemplo del profesor (stock+precio, no frecuencia/altura) |
| **videojuegos.sqlite** | BD del proyecto del alumno |
| **anyadirALista** | Función con transacción (NO usar ñ en nombres de función) |
| **gh-deploy** | `mkdocs gh-deploy` → publica en GitHub Pages |

→ Detalle completo: memory/glossary.md
→ Proyectos: memory/projects/

## Preferencias técnicas (MkDocs)
- Admoniciones con 4 espacios de indentación (NO `>` blockquote)
- Tablas con separador `|---|---|` obligatorio
- `## 🎯 Práctica N:` → aparece en TOC de MkDocs (obligatorio antes de cada `!!! warning`)
- `!!! warning` para prácticas, `!!! danger` para entrega, `!!! success` para ejecutar/analizar, `!!! example` para autoevaluación, `??? quote "Solución"` para respuestas colapsables
- Autoevaluaciones: 4 opciones A/B/C/D, una ✅ correcta, tres ❌ incorrectas, cada una explicada
- Acento naranja "Claude": `#da7756` cabecera, `#b85e3a` pestañas
