# Convenciones MkDocs Material — Acceso a Datos

## Identidad visual
- Base: tema Darcula/IntelliJ dark (fondo `#2b2b2b`, texto `#a9b7c6`)
- Acento naranja "Claude": `#da7756` cabecera, `#b85e3a` pestañas/acento
- `primary` y `accent` en mkdocs.yml deben ser nombres válidos de Material (p.ej. `black`). Valores arbitrarios rompen `navigation.path` en silencio.
- Indicador de pestaña activa: sobrescribir `--md-primary-bg-color` en scope `.md-tabs`, no perseguir clases `--active`.

## Tipos de admonición por función
| Tipo | Uso |
|------|-----|
| `!!! abstract` | Objetivos de la unidad (inicio de cada unidad) |
| `!!! success "🔍 Ejecutar y Analizar"` | Código a ejecutar con salida esperada |
| `!!! warning "🎯 Práctica N:"` | Práctica del alumno |
| `!!! danger "📁 Entrega Final"` | Entrega final |
| `!!! info` | Notas, tablas informativas |
| `!!! tip` | Buenas prácticas |
| `!!! example "Autoevaluación"` | Preguntas de autoevaluación |
| `??? quote "Solución"` | Respuesta colapsable a autoevaluación |

## Reglas de formato críticas
1. **Indentación en admoniciones**: SIEMPRE 4 espacios. NUNCA `>` blockquote.
2. **Tablas**: Separador `|---|---|` obligatorio en segunda fila.
3. **TOC**: `## 🎯 Práctica N:` heading ANTES de cada `!!! warning` de práctica (el título del admonition no aparece en el TOC).
4. **Autoevaluación**: Formato estricto: 4 opciones A/B/C/D, una ✅ correcta con explicación, tres ❌ incorrectas con explicación. Todo dentro de `!!! example` con `??? quote "Solución"` colapsable.

## Estructura de entrega final (patrón UD2)
```markdown
# Título

!!! danger "📁 Entrega Final"
    Descripción breve.

## 🎯 Proyecto final: Nombre del proyecto

!!! warning "🎯 Proyecto final: Nombre del proyecto"
    Descripción completa.
    
    !!! warning "Requisito imprescindible"
        Sin rastro del código del profesor.
    
    ### Base de Datos
    ### Estructura del Código
    ### Aplicación Principal
    ### Documentación: LEEME.md
```

## Flujo de publicación
1. Editar en local (Google Drive mount)
2. Probar en repo `-test` si es cambio grande
3. Migrar al repo oficial con aprobación de Jose
4. Entregar comandos para que Jose ejecute: `git add`, `git commit`, `git push`, `mkdocs gh-deploy`
5. NUNCA ejecutar esos comandos desde el sandbox

## Ubicación canónica
```
02_Educacion/02_MATERIAL/repositorios/acceso_datos/
```
Nunca dentro de `00_CURSOS/202x_202x/...` — ese directorio usa symlinks al canónico.
