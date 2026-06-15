# Módulo de Acceso a Datos — 2º DAM

**IES Camp de Morvedre · CFGS Desarrollo de Aplicaciones Multiplataforma**

Repositorio del material didáctico del módulo de **Acceso a Datos** de **2º de DAM**. El sitio web con el contenido publicado está disponible en:

🌐 **<https://jmabadlopez.github.io/acceso_datos/>**

---

## Descripción

Este repositorio contiene las unidades teóricas y los recursos del módulo de Acceso a Datos. El material está construido con [MkDocs Material](https://squidfunk.github.io/mkdocs-material/) y se publica en GitHub Pages.

El módulo cubre el ciclo completo del acceso y persistencia de datos en aplicaciones: desde la programación en Kotlin y el manejo de ficheros, hasta el diseño de APIs REST con arquitectura por capas, pasando por JDBC y ORM.

---

## Contenido

### Unidades Didácticas

| Unidad | Título |
| :---: | :--- |
| UD1 | Programación en el entorno Kotlin |
| UD2 | Persistencia en ficheros |
| UD3 | Persistencia en Bases de Datos |
| UD4 | ORM. Mapeo Objeto Relacional |
| UD5 | Diseño de componentes. API REST |

---

## Tecnología

- **Generador:** [MkDocs](https://www.mkdocs.org/) con el tema [Material](https://squidfunk.github.io/mkdocs-material/)
- **Extensiones:** `admonition`, `pymdownx.details`, `pymdownx.superfences` (Mermaid), `pymdownx.tabbed`, `pymdownx.highlight`
- **Tema visual:** Darcula (IntelliJ IDEA)
- **Despliegue:** GitHub Pages (rama `gh-pages`)

Para construir el sitio en local:

```bash
pip install mkdocs-material
mkdocs serve        # servidor de desarrollo en http://127.0.0.1:8000
mkdocs gh-deploy    # publicar en GitHub Pages
```

---

## Autoría

Este material ha sido elaborado y es mantenido por **José Manuel Abad López** — docente del IES Camp de Morvedre.

- 📧 <jm.abadlopez@edu.gva.es>
- 🌐 <https://jmabadlopez.github.io>

Las unidades UD1, UD2 y UD3 están basadas en el trabajo original de **Begoña Paterna** (a partir de materiales de **Alicia Salvador**), adaptadas y ampliadas para el presente curso. Las unidades UD4 y UD5 han sido creadas íntegramente por José Manuel Abad López.

---

## Licencia

Este material se distribuye bajo licencia [Creative Commons CC BY-SA 4.0](https://creativecommons.org/licenses/by-sa/4.0/). Puedes reutilizarlo y adaptarlo siempre que cites la autoría y mantengas la misma licencia.
