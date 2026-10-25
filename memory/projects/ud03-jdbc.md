# UD3 — Acceso a Datos con JDBC

**Estado:** Revisión completa completada (octubre 2026). Pendiente git push + gh-deploy por Jose.

## Estructura de archivos
```
docs/unidades/ud03/
  introduccion.md   — JDBC, URL de conexión, setup IntelliJ
  conexiones.md     — ConexionBD, .use{}, Singleton
  operaciones.md    — CRUD básico (⚠️ pendiente revisión menor)
  dao.md            — Patrón DAO, PlantasDAO, data class
  transacciones.md  — ACID, autoCommit, rollback, varios tablas
  migracion.md      — SQLite → MySQL/PostgreSQL
  entrega.md        — Entrega final: Catálogo de Videojuegos
docs/assets/resources/
  plantas.sqlite    — BD de ejemplo (schema: stock+precio ✅)
```

## Cambios realizados (sesión oct-2026)

### Modelos de datos
- `frecuenciaRiego` / `altura` → `stock` / `precio` en toda la UD3 (dao.md, transacciones.md, migracion.md)
- `plantas.sqlite` regenerado con schema correcto
- Datos CSV de UD2 sincronizados

### Autoevaluaciones añadidas (10 preguntas)
- Q1-Q2: introduccion.md (URL JDBC, embedded vs cliente-servidor)
- Q3-Q4: conexiones.md (.use{} vs close, Singleton object)
- Q5-Q7: dao.md (PreparedStatement SQL injection, ResultSet null, DAO pattern)
- Q8-Q9: transacciones.md (ACID Atomicity, autoCommit consequences)
- Q10: migracion.md (qué ficheros cambiar al migrar)

### Prácticas revisadas (referencias a Videojuegos UD2)
- Práctica 1 (introduccion): SQL CREATE TABLE videojuegos + INSERT 5 juegos
- Práctica 2 (conexiones): ConexionBD apuntando a videojuegos.sqlite
- Práctica 3 (dao): data class Videojuego con snippet
- Práctica 4 (dao): VideojuegoDAO con CRUD + Elden Ring como ejemplo
- Práctica 5 (transacciones): tablas listas + listas_videojuegos + `anyadirALista()`
- Práctica 6 (migracion): migrar las 3 tablas a MySQL/PostgreSQL

### entrega.md
- Rediseñada al estilo UD2: `## 🎯 Proyecto final:` + todo dentro de `!!! warning`
- Proyecto: Catálogo de Videojuegos con 3 tablas relacionadas
- DAOs: VideojuegoDAO, ListaDAO, ListaVideojuegoDAO
- Reto extra: `obtenerVideojuegosDeLista(idLista)`

### Formato MkDocs
- `## 🎯 Práctica N:` headings restaurados (aparecen en TOC)
- Tablas con `|---|---|` corregidas en conexiones.md
- `!!! info` con 4 espacios (no `>`) corregido

## Pendiente
- `operaciones.md`: 69 líneas, sin sección de práctica, sin expected output en `!!! success`. Revisar en próxima sesión.
- Git push + mkdocs gh-deploy (comandos entregados a Jose para ejecutar)

## Comandos git (para Jose)
```bash
cd "/Users/jose/Library/CloudStorage/GoogleDrive-abadlopez.jm@gmail.com/Mi unidad/02_Educacion/02_MATERIAL/repositorios/acceso_datos"
echo "*.sqlite-journal" >> .gitignore
git add docs/unidades/ud03/
git add docs/assets/resources/plantas.sqlite
git add .gitignore
git status
git commit -m "UD3: revisión completa — consistencia UD2, autoevaluaciones, prácticas Videojuegos, entrega final"
git push
mkdocs gh-deploy
```
