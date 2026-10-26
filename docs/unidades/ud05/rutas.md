# Rutas. Implementación del CRUD Completo

En este apartado construimos los **Controladores REST** de Spring Boot. Mientras que en Ktor las rutas se definen como funciones dentro de bloques `routing { }`, en Spring Boot cada endpoint es un **método anotado** dentro de una clase `@RestController`.

## 1 El Controlador en Spring Boot

### Estructura general

```kotlin
@RestController                     // Indica que esta clase maneja peticiones HTTP y devuelve JSON
@RequestMapping("/usuarios")        // Prefijo común para todos los endpoints de esta clase
class UsuarioController(
    private val service: UsuarioService  // Inyección de dependencias por constructor
) {

    @GetMapping                     // GET /usuarios
    fun listarTodos(): List<UsuarioDTO> { ... }

    @GetMapping("/{id}")            // GET /usuarios/{id}
    fun buscarPorId(@PathVariable id: Int): ResponseEntity<UsuarioDTO> { ... }

    @PostMapping                    // POST /usuarios
    fun crear(@RequestBody usuario: UsuarioDTO): ResponseEntity<Int> { ... }

    // ...
}
```

!!! info "Comparativa Ktor vs Spring Boot — Definición de Rutas"

    | Aspecto | Ktor | Spring Boot |
    |---|---|---|
    | Estructura | Función `Route.usuarioRouting()` | Clase `@RestController` |
    | GET todos | `get { call.respond(lista) }` | `@GetMapping fun listar()` |
    | GET por ID | `get("{id}") { }` | `@GetMapping("/{id}") fun buscar(@PathVariable id: Int)` |
    | Respuesta | `call.respond(objeto)` | `return ResponseEntity.ok(objeto)` |
    | 404 | `call.respond(HttpStatusCode.NotFound, ...)` | `return ResponseEntity.notFound().build()` |
    | Parámetro URL | `call.parameters["id"]?.toIntOrNull()` | `@PathVariable id: Int` |
    | Cuerpo JSON | `call.receive<UsuarioDTO>()` | `@RequestBody usuario: UsuarioDTO` |

    El resultado es idéntico; la sintaxis es diferente.

## 2 Implementación Completa: `UsuarioController.kt`

!!! success "🔍 Ejecutar y Analizar"
    Crea `controllers/UsuarioController.kt` y observa cómo `@PathVariable` y `@RequestBody` sustituyen a `call.parameters` y `call.receive<T>()` de Ktor.

```kotlin
package com.tuproyecto.usuariosapi.controllers

import com.tuproyecto.usuariosapi.domain.UsuarioDTO
import com.tuproyecto.usuariosapi.services.UsuarioService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/usuarios")
class UsuarioController(
    private val service: UsuarioService
) {

    // 1. GET /usuarios — Listar todos
    @GetMapping
    fun listarTodos(): ResponseEntity<List<UsuarioDTO>> {
        val lista = service.listarUsuarios()
        return ResponseEntity.ok(lista)
    }

    // 2. GET /usuarios/{id} — Buscar por ID
    @GetMapping("/{id}")
    fun buscarPorId(@PathVariable id: Int): ResponseEntity<UsuarioDTO> {
        val usuario = service.buscarPorId(id)
        return if (usuario != null) {
            ResponseEntity.ok(usuario)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    // 3. POST /usuarios — Crear nuevo
    @PostMapping
    fun crear(@RequestBody usuario: UsuarioDTO): ResponseEntity<Any> {
        val nuevoId = service.registrarUsuario(usuario)
        return if (nuevoId != -1) {
            ResponseEntity.status(HttpStatus.CREATED).body(nuevoId)
        } else {
            ResponseEntity.status(HttpStatus.CONFLICT).body("El email ya está registrado")
        }
    }

    // 4. PUT /usuarios/{id} — Actualizar
    @PutMapping("/{id}")
    fun actualizar(
        @PathVariable id: Int,
        @RequestBody usuario: UsuarioDTO
    ): ResponseEntity<String> {
        val filas = service.actualizarUsuario(id, usuario)
        return if (filas > 0) {
            ResponseEntity.ok("Usuario actualizado correctamente")
        } else {
            ResponseEntity.notFound().build()
        }
    }

    // 5. DELETE /usuarios/{id} — Eliminar
    @DeleteMapping("/{id}")
    fun eliminar(@PathVariable id: Int): ResponseEntity<String> {
        val filas = service.borrarUsuario(id)
        return if (filas > 0) {
            ResponseEntity.ok("Usuario eliminado")
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
```

## 3 `ResponseEntity<T>`: Controlando la Respuesta HTTP

`ResponseEntity<T>` es la clase de Spring Boot para construir respuestas HTTP con total control sobre el código de estado, las cabeceras y el cuerpo.

| Ktor | Spring Boot | Resultado |
|---|---|---|
| `call.respond(HttpStatusCode.OK, objeto)` | `ResponseEntity.ok(objeto)` | 200 + JSON |
| `call.respond(HttpStatusCode.Created, id)` | `ResponseEntity.status(HttpStatus.CREATED).body(id)` | 201 + id |
| `call.respond(HttpStatusCode.NotFound, ...)` | `ResponseEntity.notFound().build()` | 404 vacío |
| `call.respond(HttpStatusCode.Conflict, msg)` | `ResponseEntity.status(HttpStatus.CONFLICT).body(msg)` | 409 + mensaje |

## 4 Tabla de Códigos de Estado

| Código | Nombre | Cuándo lo devolvemos |
|---|---|---|
| **200** | OK | `GET` exitoso y `PUT`/`DELETE` con resultado |
| **201** | Created | `POST` exitoso (nuevo recurso creado) |
| **404** | Not Found | ID no existe en la BD |
| **409** | Conflict | Email duplicado en `POST` |
| **401** | Unauthorized | Operación protegida sin sesión activa |
| **403** | Forbidden | Sesión activa pero sin permisos suficientes |

## 5 Probando la API con Postman

Antes de probar, inserta usuarios de prueba directamente en MySQL:

```sql
INSERT INTO usuarios(nombre, mail, password, fecha_nacimiento)
VALUES ('Atenea', 'atenea@mail.com', 'pass123', '2000-05-15');

INSERT INTO usuarios(nombre, mail, password, fecha_nacimiento)
VALUES ('Perséfone', 'persefone@mail.com', 'pass123', '2004-01-01');
```

La API escucha en `http://localhost:8080`:

| Método | URL | Body (JSON) | Resultado esperado |
|---|---|---|---|
| `GET` | `/usuarios` | — | 200 + lista JSON |
| `GET` | `/usuarios/1` | — | 200 + usuario / 404 |
| `POST` | `/usuarios` | `{"id":0,"nombre":"Ícaro","mail":"icaro@mail.com","password":"pass123","fechaNacimiento":"2003-10-20"}` | 201 + nuevo ID |
| `PUT` | `/usuarios/1` | `{"id":1,"nombre":"Atenea","mail":"atenea@olimpo.com","password":"atenea123","fechaNacimiento":"2000-05-15"}` | 200 |
| `DELETE` | `/usuarios/2` | — | 200 / 404 |

!!! info "Nota sobre Postman"
    En `POST` y `PUT` selecciona **Body → raw → JSON**. Los nombres de los campos deben coincidir exactamente con `UsuarioDTO` (respetando mayúsculas, p. ej. `fechaNacimiento`).

!!! danger "¿Spring Security bloquea todas las peticiones?"
    Por defecto, Spring Security exige autenticación para **todo**. En la siguiente sección configuramos `SecurityFilterChain` para abrir las rutas públicas y proteger solo las de escritura. Si ves un `403 Forbidden` en un `GET /usuarios`, es normal en este punto.

---

## 🎯 Práctica 3. Implementando el Controlador REST

!!! warning "🎯 Práctica 3. Implementando el Controlador REST"

    1. Crea `controllers/UsuarioController.kt` con los 5 endpoints.
    2. Arranca la aplicación.
    3. Prueba en Postman los endpoints `GET` (puede que recibas 403 hasta configurar Security).
    4. Una vez que configures Spring Security en la siguiente sección, prueba todos los endpoints.

---

!!! example "Autoevaluación"

    **Pregunta 11: En Spring Boot, `@PathVariable` y `@RequestBody` extraen datos de la petición. ¿Cuál es la diferencia entre ambas?**

    A) `@PathVariable` lee datos del cuerpo JSON; `@RequestBody` lee datos de la URL.

    B) `@PathVariable` extrae un segmento de la URL (p. ej. el `{id}` de `/usuarios/{id}`); `@RequestBody` deserializa el cuerpo JSON de la petición en un objeto Kotlin.

    C) `@PathVariable` solo funciona con `@GetMapping`; `@RequestBody` solo funciona con `@PostMapping`.

    D) Son equivalentes; se usan indistintamente según la preferencia del desarrollador.

    ??? quote "Solución"
        ❌ A) Es exactamente al revés.

        ✅ B) `@PathVariable` mapea un segmento variable de la ruta URL — por ejemplo, en `GET /usuarios/5`, extrae el `5` y lo convierte a `Int`. `@RequestBody` deserializa el JSON del cuerpo de la petición (disponible en `POST`/`PUT`) en el tipo de objeto indicado. Son complementarios: uno lee la URL, el otro lee el cuerpo.

        ❌ C) No hay restricción por tipo de mapping. `@PathVariable` se usa habitualmente en `GET`, `PUT` y `DELETE` (para identificar el recurso). `@RequestBody` se usa en `POST` y `PUT` (para recibir el contenido).

        ❌ D) No son equivalentes. Cada una extrae información de una parte distinta de la petición HTTP.

    **Pregunta 12: `ResponseEntity.notFound().build()` devuelve un 404 con cuerpo vacío. ¿Cuándo es preferible a lanzar una excepción?**

    A) Nunca; siempre es mejor lanzar una excepción porque Spring la convierte automáticamente en la respuesta HTTP correcta.

    B) `ResponseEntity` es la opción adecuada cuando el controlador necesita devolver distintos códigos HTTP según el resultado del servicio (200 o 404 en la misma función). Las excepciones son más adecuadas cuando el caso de error es excepcional y quieres centralizar el manejo con `@ExceptionHandler`.

    C) `ResponseEntity` solo se puede usar en `@GetMapping`; en `@PostMapping` hay que lanzar excepciones.

    D) `ResponseEntity` solo funciona con tipos primitivos; para objetos complejos hay que lanzar excepciones.

    ??? quote "Solución"
        ❌ A) Lanzar excepciones es válido, pero no siempre es la opción más clara. Un 404 previsible ("el usuario con ese ID no existe") no es un caso excepcional; es un flujo normal de la aplicación y se maneja bien con `ResponseEntity`.

        ✅ B) `ResponseEntity` brilla cuando una función puede tener múltiples respuestas HTTP válidas. El patrón `return if (x != null) ResponseEntity.ok(x) else ResponseEntity.notFound().build()` es claro y explícito. Las excepciones personalizadas (con `@ResponseStatus` o `@ExceptionHandler`) son más adecuadas para casos de error transversales (errores de BD, validaciones de seguridad, etc.).

        ❌ C) `ResponseEntity` funciona con cualquier tipo de mapping HTTP. No hay restricción por método.

        ❌ D) `ResponseEntity<T>` es genérica — `T` puede ser cualquier tipo: primitivo, `UsuarioDTO`, `List<UsuarioDTO>`, `String`, etc.
