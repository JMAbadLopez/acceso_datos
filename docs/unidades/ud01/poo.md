# Programación Orientada a Objetos

## 1. Clases y Objetos

La **Programación Orientada a Objetos (POO)** organiza el código en **objetos** que combinan datos (propiedades) y comportamiento (métodos). Las **clases** son las plantillas para crear objetos.

### 1.1. Constructor primario

La forma más concisa de definir una clase en Kotlin es con el constructor primario en la cabecera de la clase:

```kotlin
class Estudiante(val nombre: String, val edad: Int) {

    fun imprimirDatos() {
        println("Nombre: $nombre, Edad: $edad")
    }

    fun esMenorDeEdad(): Boolean = edad < 18
}

fun main() {
    val e1 = Estudiante("Pol", 16)
    e1.imprimirDatos()
    println("¿Menor de edad? ${e1.esMenorDeEdad()}")

    val e2 = Estudiante("Eli", 20)
    e2.imprimirDatos()
    println("¿Menor de edad? ${e2.esMenorDeEdad()}")
}
```

### 1.2. Propiedades dentro del cuerpo

```kotlin
class Estudiante {
    var nombre: String = ""
    var edad: Int = 0

    fun imprimirDatos() = println("Nombre: $nombre, Edad: $edad")
    fun esMenorDeEdad(): Boolean = edad < 18
}

fun main() {
    val e = Estudiante()
    e.nombre = "Pol"
    e.edad = 16
    e.imprimirDatos()
}
```

### 1.3. Constructor primario + constructor secundario

```kotlin
class Estudiante(val nombre: String, val edad: Int, val direccion: String) {

    // Constructor secundario: llama al primario con un valor por defecto
    constructor(nombre: String, edad: Int) : this(nombre, edad, "Sin dirección")

    fun imprimirDatos() {
        println("Nombre: $nombre | Edad: $edad | Dirección: $direccion")
    }

    fun esMenorDeEdad(): Boolean = edad < 18
}

fun main() {
    val e1 = Estudiante("Pol", 16, "Calle Mayor 1")
    val e2 = Estudiante("Eli", 20)  // usa el constructor secundario
    e1.imprimirDatos()
    e2.imprimirDatos()
}
```

---

## 2. Getters y Setters

En Kotlin todas las propiedades `var` tienen un getter y un setter **automáticos**. Puedes personalizarlos cuando necesites añadir lógica:

```kotlin
class Producto {
    var nombre: String = "Sin nombre"
    var precio: Double = 0.0
        get() {
            println("Obteniendo precio...")
            return field
        }
        set(value) {
            println("Asignando precio: $value")
            field = if (value >= 0) value else 0.0  // no permite precios negativos
        }
}

fun main() {
    val p = Producto()
    p.precio = 120.0
    println("Precio: ${p.precio}")
    p.precio = -50.0
    println("Precio: ${p.precio}")  // 0.0 (valor negativo rechazado)
}
```

**Setter privado** — solo la propia clase puede modificar la propiedad:

```kotlin
class Usuario(val nombre: String) {
    var edad: Int = 0
        private set  // solo modificable desde dentro de la clase

    fun cumpleaños() { edad++ }
}

fun main() {
    val u = Usuario("Pol")
    u.cumpleaños()
    println("Edad: ${u.edad}")
    // u.edad = 30  ← Error de compilación
}
```

**Propiedad calculada con `get` personalizado:**

```kotlin
import kotlin.math.PI

class Circulo(val radio: Double) {
    val area: Double
        get() = PI * radio * radio
}

fun main() {
    val c = Circulo(5.0)
    println("Área: ${c.area}")  // 78.53981633974483
}
```

---

## 3. Relación entre Clases

En una aplicación real las clases se relacionan entre sí. Una clase puede contener objetos de otras clases como propiedades:

```kotlin
class Curso(val nombre: String, val duracionSemanas: Int) {
    fun info(): String = "Curso: $nombre ($duracionSemanas semanas)"
}

class Estudiante(
    val nombre: String,
    val edad: Int,
    val curso: Curso
) {
    fun imprimirDatos() {
        println("Nombre: $nombre | Edad: $edad")
        println(curso.info())
    }
}

fun main() {
    val cursoKotlin = Curso("Kotlin Básico", 6)
    val e = Estudiante("Pol", 16, cursoKotlin)
    e.imprimirDatos()
}
```

---

## 4. Modificadores de Acceso

En Kotlin todo es **público por defecto**. Para controlar la visibilidad se usan los modificadores:

| Modificador | Visibilidad |
|---|---|
| `public` (por defecto) | Visible desde cualquier lugar |
| `private` | Solo visible dentro de la clase |
| `protected` | Visible en la clase y sus subclases |
| `internal` | Visible dentro del mismo módulo |

```kotlin
class CuentaBancaria(val titular: String) {
    private var saldo: Double = 0.0

    fun depositar(cantidad: Double) { saldo += cantidad }
    fun mostrarSaldo() = println("Saldo: $saldo")
}

fun main() {
    val cuenta = CuentaBancaria("María")
    cuenta.depositar(100.0)
    cuenta.mostrarSaldo()
    // cuenta.saldo = 1000.0  ← Error: saldo es private
}
```

---

## 5. Herencia

La **herencia** permite que una clase (subclase) herede propiedades y métodos de otra (superclase).

!!! info "Clases `open` en Kotlin"
    Por seguridad, en Kotlin las clases y sus métodos **no se pueden heredar** por defecto. Se debe marcar explícitamente con `open` la clase o método que se desea permitir sobrescribir.

```kotlin
open class Animal(val nombre: String) {
    open fun hacerSonido() {
        println("$nombre hace un sonido genérico")
    }
}

class Perro(nombre: String) : Animal(nombre) {
    override fun hacerSonido() {
        super.hacerSonido()
        println("$nombre dice: ¡Guau!")
    }
}

class Gato(nombre: String) : Animal(nombre) {
    override fun hacerSonido() {
        super.hacerSonido()
        println("$nombre dice: ¡Miau!")
    }
}

fun main() {
    val animales: List<Animal> = listOf(
        Perro("Firulais"),
        Gato("Misu"),
        Animal("Criatura")
    )
    for (animal in animales) {
        animal.hacerSonido()
        println("---")
    }
}
```

!!! success "🔍 Ejecutar y Analizar"
    Observa cómo `filterIsInstance<T>()` permite filtrar por tipo en una lista polimórfica:

```kotlin
val soloPerros = animales.filterIsInstance<Perro>()
println("Hay ${soloPerros.size} perro(s)")
```

---

## 6. Funciones de Extensión

Las **funciones de extensión** permiten añadir nuevos métodos a una clase existente sin modificarla ni heredar de ella:

```kotlin
// Extensión de String
fun String.saludar(): String = "Hola, $this"

// Extensión de Int
fun Int.esPar(): Boolean = this % 2 == 0

// Extensión de una clase propia
fun Animal.nombreEnMayusculas(): String = nombre.uppercase()

fun main() {
    println("Pol".saludar())         // Hola, Pol
    println(4.esPar())               // true
    val gato = Animal("Misu")
    println(gato.nombreEnMayusculas())  // MISU
}
```

---

## 7. `data class`

Una `data class` está pensada exclusivamente para **almacenar datos**. Kotlin genera automáticamente `toString()`, `equals()`, `hashCode()` y `copy()`.

```kotlin
data class Libro(val titulo: String, val autor: String, val anio: Int)

fun main() {
    val l1 = Libro("1984", "George Orwell", 1949)
    val l2 = Libro("Cien años de soledad", "Gabriel García Márquez", 1967)
    val l3 = Libro("Fahrenheit 451", "Ray Bradbury", 1953)

    val biblioteca = listOf(l1, l2, l3)

    println("Ordenados por año:")
    biblioteca.sortedBy { it.anio }.forEach {
        println("  ${it.titulo} → ${it.anio}")
    }

    // copy() crea una copia con algunos campos modificados
    val edicionNueva = l1.copy(anio = 2025)
    println("\nOriginal:  $l1")
    println("Copia:     $edicionNueva")
    println("¿Iguales?  ${l1 == edicionNueva}")  // false
}
```

---

## 8. Sobrecarga de Operadores

Kotlin permite definir el comportamiento de los operadores (`+`, `-`, `==`, etc.) para nuestras propias clases usando la palabra clave `operator`:

```kotlin
data class Punto(val x: Int, val y: Int) {
    operator fun plus(otro: Punto): Punto = Punto(x + otro.x, y + otro.y)
}

fun main() {
    val p1 = Punto(2, 3)
    val p2 = Punto(4, 1)
    println(p1 + p2)  // Punto(x=6, y=4)
}
```

---

## 9. Excepciones

Una **excepción** es un error en tiempo de ejecución que interrumpe el flujo normal del programa. Se gestiona con `try-catch-finally`:

```kotlin
fun dividir(a: Int, b: Int): Int {
    if (b == 0) throw IllegalArgumentException("El divisor no puede ser 0")
    return a / b
}

fun main() {
    try {
        println(dividir(10, 2))   // 5
        println(dividir(10, 0))   // lanza excepción
    } catch (e: IllegalArgumentException) {
        println("Error: ${e.message}")
    } catch (e: Exception) {
        println("Error inesperado: ${e.message}")
    } finally {
        println("Bloque finally siempre se ejecuta")
    }
}
```

---

## 10. Packages e Imports

Un **package** agrupa clases, funciones y objetos relacionados bajo un mismo nombre. Se usa `import` para acceder a elementos de otros paquetes.

```kotlin
// Archivo: util/Saludos.kt
package util

fun saludar(nombre: String): String = "¡Hola, $nombre!"
fun despedir(nombre: String): String = "Adiós, $nombre."
```

```kotlin
// Archivo: util/Matematicas.kt
package util

fun sumar(a: Int, b: Int): Int = a + b
fun multiplicar(a: Int, b: Int): Int = a * b
```

```kotlin
// Archivo: app/Main.kt
package app

import util.saludar
import util.despedir
import util.sumar
import util.multiplicar

fun main() {
    val nombre = "Kotlin"
    println(saludar(nombre))
    println("Suma: ${sumar(3, 5)}")
    println("Multiplicación: ${multiplicar(4, 6)}")
    println(despedir(nombre))
}
```

!!! info "Variantes de import"
    - `import util.saludar` — importa una función concreta.
    - `import util.*` — importa todo el paquete.
    - `import util.saludar as saludo` — importa con alias.
