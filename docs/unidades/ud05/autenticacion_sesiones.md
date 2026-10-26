# Autenticación y Sesiones

En esta sección añadimos seguridad a la API con **Spring Security**. El resultado es el mismo que en la versión Ktor (sesiones basadas en cookies, rutas de lectura públicas, escritura protegida), pero la configuración es declarativa mediante la clase `SecurityFilterChain`.

## 1 ¿Por qué Spring Security?

HTTP es *stateless*: el servidor olvida quién eres en cada petición. Las **sesiones** resuelven esto guardando un identificador en una cookie que el navegador/Postman envía automáticamente en cada petición posterior.

Spring Security intercepta **todas** las peticiones antes de que lleguen al controlador y verifica si el usuario tiene permiso. Es un "portero" global que se activa solo con tener la dependencia en el proyecto.

!!! info "Comparativa Ktor vs Spring Boot — Autenticación"

    | Aspecto | Ktor | Spring Boot |
    |---|---|---|
    | Configuración | `install(Authentication) { session<UserSession>(...) }` | `@Bean fun securityFilterChain(http: HttpSecurity)` |
    | Sesión | `data class UserSession(val email: String)` | `HttpSession` de Spring (gestionada automáticamente) |
    | Login | Ruta `POST /auth/login` manual | Ruta `POST /auth/login` manual |
    | Logout | `call.sessions.clear<UserSession>()` | `http.logout { ... }` + `SecurityContextHolder` |
    | Ruta protegida | `authenticate("auth-session") { ... }` | `.requestMatchers("/usuarios/**").authenticated()` |

## 2 Endpoints de Login y Logout

Añade las rutas de autenticación al controlador de seguridad. Primero necesitamos que el `UsuariosDAO` busque por email (igual que en Ktor):

!!! success "🔍 Añadir a `UsuariosDAO.kt`"

```kotlin
fun seleccionarPorEmail(email: String): UsuarioDTO? = transaction {
    UsuariosTable
        .selectAll()
        .where { UsuariosTable.mail eq email }
        .firstOrNull()
        ?.let { toDTO(it) }
}
```

Y el modelo `LoginRequest` para el cuerpo del login:

!!! success "🔍 Nuevo fichero: `domain/LoginRequest.kt`"

```kotlin
package com.tuproyecto.usuariosapi.domain

data class LoginRequest(
    val mail: String,
    val password: String
)
```

Añade al `UsuarioService` la validación de credenciales:

!!! success "🔍 Añadir a `UsuarioService.kt`"

```kotlin
fun buscarPorEmail(mail: String, password: String): UsuarioDTO? {
    val usuario = UsuariosDAO.seleccionarPorEmail(mail)
    return if (usuario != null && usuario.password == password) usuario else null
}
```

Ahora el controlador de autenticación:

!!! success "🔍 Nuevo fichero: `controllers/AuthController.kt`"

```kotlin
package com.tuproyecto.usuariosapi.controllers

import com.tuproyecto.usuariosapi.domain.LoginRequest
import com.tuproyecto.usuariosapi.services.UsuarioService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val service: UsuarioService
) {

    // POST /auth/login
    @PostMapping("/login")
    fun login(
        @RequestBody request: LoginRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<String> {
        val usuario = service.buscarPorEmail(request.mail, request.password)

        return if (usuario != null) {
            // Creamos la sesión HTTP (Spring la gestiona automáticamente con una cookie)
            val session = httpRequest.getSession(true)
            session.setAttribute("userMail", usuario.mail)
            ResponseEntity.ok("Login exitoso. Bienvenido, ${usuario.nombre}")
        } else {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas")
        }
    }

    // POST /auth/logout
    @PostMapping("/logout")
    fun logout(httpRequest: HttpServletRequest): ResponseEntity<String> {
        httpRequest.getSession(false)?.invalidate()
        SecurityContextHolder.clearContext()
        return ResponseEntity.ok("Sesión cerrada correctamente.")
    }
}
```

## 3 Configuración de Spring Security: `SecurityConfig.kt`

Este es el equivalente al `plugins/Security.kt` de Ktor. Define qué rutas son públicas y cuáles requieren autenticación.

!!! success "🔍 Nuevo fichero: `security/SecurityConfig.kt`"

```kotlin
package com.tuproyecto.usuariosapi.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            // Desactivamos CSRF (necesario para APIs REST con Postman)
            .csrf { it.disable() }

            // Definimos qué rutas requieren autenticación
            .authorizeHttpRequests { auth ->
                // Rutas de autenticación: públicas
                auth.requestMatchers("/auth/**").permitAll()

                // Lectura de usuarios: pública
                auth.requestMatchers(HttpMethod.GET, "/usuarios", "/usuarios/**").permitAll()

                // Escritura (POST, PUT, DELETE): requiere sesión activa
                auth.requestMatchers(HttpMethod.POST, "/usuarios").authenticated()
                auth.requestMatchers(HttpMethod.PUT, "/usuarios/**").authenticated()
                auth.requestMatchers(HttpMethod.DELETE, "/usuarios/**").authenticated()

                // Todo lo demás: denegado
                auth.anyRequest().denyAll()
            }

            // Configuración de sesiones
            .sessionManagement { session ->
                session.maximumSessions(1)
            }

            // Respuesta personalizada para acceso denegado (equivalente al challenge de Ktor)
            .exceptionHandling { ex ->
                ex.authenticationEntryPoint { _: HttpServletRequest, response: HttpServletResponse, _ ->
                    response.status = HttpServletResponse.SC_UNAUTHORIZED
                    response.contentType = "text/plain;charset=UTF-8"
                    response.writer.write("401: No autorizado. Debes iniciar sesión.")
                }
            }

        return http.build()
    }
}
```

!!! warning "Sesión manual vs Spring Security nativo"
    En esta implementación usamos `HttpSession` directamente (como en Ktor) para mantener la similitud conceptual. En proyectos de producción se usa `UsernamePasswordAuthenticationFilter` o un `UserDetailsService` para integración completa con el ecosistema Spring Security.

## 4 Estructura Final del Proyecto

```text
src/main/kotlin/com/tuproyecto/usuariosapi/
├── UsuariosApiApplication.kt
├── data/
│   ├── UsuariosTable.kt
│   └── UsuariosDAO.kt
├── domain/
│   ├── UsuarioDTO.kt
│   └── LoginRequest.kt       ← NUEVA
├── services/
│   └── UsuarioService.kt
├── controllers/
│   ├── UsuarioController.kt
│   └── AuthController.kt     ← NUEVA
└── security/
    └── SecurityConfig.kt     ← NUEVA
```

## 5 Guía de Pruebas en Postman

| Método | URL | Body JSON | Requiere Login | Resultado esperado |
|---|---|---|---|---|
| `POST` | `/auth/login` | `{"mail":"atenea@mail.com","password":"pass123"}` | No | 200 + cookie de sesión |
| `GET` | `/usuarios` | — | No | 200 + lista JSON |
| `POST` | `/usuarios` | `{"id":0,"nombre":"Ícaro","mail":"icaro@mail.com","password":"pass123","fechaNacimiento":"2003-10-20"}` | **Sí** | 201 (con login) / 401 (sin login) |
| `POST` | `/auth/logout` | — | No | 200 |
| `DELETE` | `/usuarios/1` | — | **Sí** | 401 (sin login) / 200 (con login) |

!!! danger "Activa las cookies en Postman"
    En Postman, asegúrate de que las cookies están habilitadas (pestaña **Cookies** o el icono del candado). Spring Security gestiona la sesión mediante la cookie `JSESSIONID`, que Postman debe enviar automáticamente en cada petición tras el login.

---

## 🎯 Práctica 4. Auditoría de Seguridad

!!! warning "🎯 Práctica 4. Auditoría de Seguridad"

    Valida que la seguridad funciona correctamente siguiendo este orden:

    1. **Test de intrusión:** Intenta `DELETE /usuarios/1` sin haber hecho login. Resultado esperado: **401**.
    2. **Login:** Haz `POST /auth/login` con credenciales válidas. Resultado esperado: **200** + cookie `JSESSIONID` en Postman.
    3. **Operación protegida:** Con la sesión activa, repite el `DELETE /usuarios/1`. Resultado esperado: **200**.
    4. **Logout:** Ejecuta `POST /auth/logout`. Resultado esperado: **200**.
    5. **Post-logout:** Intenta de nuevo el `DELETE`. Resultado esperado: **401** (sesión destruida).

---

!!! example "Autoevaluación"

    **Pregunta 13: ¿Para qué sirve `.csrf { it.disable() }` en la configuración de Spring Security?**

    A) Desactiva el cifrado de las contraseñas, lo que hace la aplicación más rápida.

    B) Deshabilita la protección CSRF (Cross-Site Request Forgery), que está activa por defecto. Es necesario hacerlo en APIs REST que usan Postman u otros clientes HTTP que no son navegadores, porque no gestionan tokens CSRF automáticamente.

    C) Desactiva la autenticación completamente, permitiendo acceso libre a todas las rutas.

    D) Elimina la necesidad de usar HTTPS en el servidor.

    ??? quote "Solución"
        ❌ A) CSRF no tiene relación con el cifrado de contraseñas. `PasswordEncoder` es lo que gestiona el hashing.

        ✅ B) CSRF es un tipo de ataque donde un sitio malicioso hace que el navegador de la víctima envíe peticiones no deseadas a nuestra API. Spring Security lo previene mediante tokens. Pero cuando el cliente es Postman, una app móvil, u otro sistema backend, no hay "navegador" que pueda ser engañado, por lo que el token CSRF es innecesario y solo complica las pruebas. Para APIs REST el estándar es deshabilitar CSRF y usar otros mecanismos (sesiones, JWT, etc.).

        ❌ C) `.csrf { it.disable() }` solo afecta a la protección CSRF. Las reglas de `.authorizeHttpRequests` siguen activas.

        ❌ D) CSRF no tiene relación con HTTPS. Son mecanismos de seguridad completamente distintos.

    **Pregunta 14: En Spring Security, `.requestMatchers(HttpMethod.DELETE, "/usuarios/**").authenticated()` ¿qué garantiza?**

    A) Que solo los usuarios con rol "ADMIN" pueden borrar registros.

    B) Que cualquier petición `DELETE` a `/usuarios/*` (por ejemplo `/usuarios/5`) debe provenir de un cliente que haya iniciado sesión previamente. Si no hay sesión válida, Spring Security devuelve 401 sin llegar al controlador.

    C) Que el endpoint DELETE se ejecuta siempre, independientemente de si hay sesión o no.

    D) Que la petición DELETE se redirige a la página de login configurada en Spring Security.

    ??? quote "Solución"
        ❌ A) `.authenticated()` solo verifica que hay una sesión activa. Para restricción por roles se usa `.hasRole("ADMIN")` o `.hasAuthority("ADMIN")`.

        ✅ B) `.authenticated()` exige que el contexto de seguridad de Spring tenga un usuario autenticado. En nuestra implementación, eso ocurre cuando `HttpSession` tiene el atributo `userMail` establecido en el login. Si no hay sesión válida, el `authenticationEntryPoint` que configuramos devuelve un 401 con el mensaje personalizado, sin que la petición llegue al `@DeleteMapping` del controlador.

        ❌ C) Es exactamente lo contrario: sin sesión, la petición es bloqueada por el filtro de seguridad antes de llegar al controlador.

        ❌ D) Nuestra configuración no tiene `loginPage(...)`. En lugar de redirigir, devolvemos un 401 directamente mediante el `authenticationEntryPoint`.
