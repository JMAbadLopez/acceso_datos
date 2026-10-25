# Glossary — Acceso a Datos

Glosario completo de términos, convenciones y decisiones técnicas del módulo.

## Acrónimos y términos
| Término | Significado | Contexto |
|---------|-------------|---------|
| JDBC | Java Database Connectivity | API estándar para conectar a BD desde Kotlin/Java |
| DAO | Data Access Object | Patrón de diseño que encapsula el acceso a BD |
| ACID | Atomicity, Consistency, Isolation, Durability | Propiedades de transacciones |
| ORM | Object-Relational Mapping | Hibernate, Exposed, Room — alternativa a JDBC directo |
| SGBD | Sistema Gestor de Bases de Datos | MySQL, PostgreSQL, SQLite |
| gh-deploy | `mkdocs gh-deploy` | Publicar en GitHub Pages |
| bóveda | Sistema de memoria de Claude | CLAUDE.md + memory/ |

## Convenciones de código
| Convención | Regla |
|-----------|-------|
| ñ en funciones | Prohibido. Usar `ny` → `anyadirALista`, no `añadirALista` |
| autoCommit | Siempre `false` antes de una transacción manual |
| `.use {}` | Cierre automático de Connection/Statement/ResultSet |
| `object ConexionBD` | Singleton — única instancia de la conexión |
| `id: Int? = null` | El id es nulable en data class (autogenerado por BD) |

## Nombres de archivos clave
| Archivo | Contenido |
|---------|-----------|
| `ConexionBD.kt` | Singleton de conexión JDBC |
| `Planta.kt` | data class del modelo profesor |
| `PlantasDAO.kt` | DAO de ejemplo (profesor) |
| `Videojuego.kt` | data class del proyecto alumno |
| `VideojuegoDAO.kt` | DAO del proyecto alumno |
| `ListaDAO.kt` | DAO de listas (alumno) |
| `ListaVideojuegoDAO.kt` | DAO de relación N:M (alumno) |
| `plantas.sqlite` | BD de ejemplo (stock+precio) |
| `videojuegos.sqlite` | BD del proyecto alumno |

## Errores conocidos / gotchas técnicos
| Error | Causa | Solución |
|-------|-------|---------|
| `disk I/O error` en Python | Google Drive mount rechaza writes de SQLite directos | Generar en `/tmp` y copiar con `cp -f` |
| `*.sqlite-journal` en git | SQLite deja journal tras write fallido | Añadir `*.sqlite-journal` a `.gitignore` |
| Migas de pan rotas en MkDocs | `primary` con valor no válido en mkdocs.yml | Usar nombres de color Material válidos (`black`, etc.) |
| Indicador pestaña activa | Clase `--active` cambia entre versiones | Sobrescribir `--md-primary-bg-color` en scope `.md-tabs` |
| `site/` en git | Ya trackeado antes de añadir a .gitignore | `git rm -r --cached site/` + commit |
| admonición rota | Usar `>` en vez de 4 espacios | Siempre 4 espacios dentro de `!!!` |

## Modelo de datos canónico

### Planta (modelo profesor — UD2/UD3)
```kotlin
data class Planta(
    val id: Int? = null,
    val nombreComun: String,
    val nombreCientifico: String,
    val stock: Int,       // NO frecuenciaRiego
    val precio: Double    // NO altura
)
```
SQL: `id, nombre_comun, nombre_cientifico, stock, precio`

Datos CSV (5 plantas):
```
1, Aloe Vera, Aloe barbadensis miller, 7, 0.6
2, Lavanda, Lavandula angustifolia, 3, 1.0
3, Helecho de Boston, Nephrolepis exaltata, 5, 0.9
4, Bambú de la suerte, Dracaena sanderiana, 4, 1.5
5, Girasol, Helianthus annuus, 2, 3.0
```

### Videojuego (proyecto alumno — UD3)
```kotlin
data class Videojuego(
    val id: Int? = null,
    val titulo: String,
    val genero: String,
    val anio: Int,
    val nota: Double
)
```
SQL: `id, titulo, genero, anio, nota`

Datos (5 juegos):
```
1, The Legend of Zelda: Breath of the Wild, Aventura, 2017, 9.3
2, Red Dead Redemption 2, Acción, 2018, 9.5
3, Hades, Roguelike, 2020, 9.0
4, Hollow Knight, Metroidvania, 2017, 8.9
5, Celeste, Plataformas, 2018, 9.0
```

### Tablas UD3 completas (proyecto alumno)
```sql
-- videojuegos (principal)
CREATE TABLE videojuegos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    titulo TEXT, genero TEXT, anio INTEGER, nota REAL
);
-- listas
CREATE TABLE listas (
    id_lista INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL
);
INSERT INTO listas(nombre) VALUES ('Pendientes'), ('Completados'), ('Favoritos');
-- relación N:M
CREATE TABLE listas_videojuegos (
    id_lista INTEGER,
    id_videojuego INTEGER,
    fecha_añadido TEXT,
    FOREIGN KEY (id_lista) REFERENCES listas(id_lista),
    FOREIGN KEY (id_videojuego) REFERENCES videojuegos(id)
);
```

### Tablas UD3 — modelo profesor (plantas)
```sql
CREATE TABLE plantas (id, nombre_comun, nombre_cientifico, stock, precio);
CREATE TABLE jardines (id_jardin, nombre);
CREATE TABLE jardines_plantas (id_planta, id_jardin, cantidad, FK...);
```
