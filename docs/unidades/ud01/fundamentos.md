# Fundamentos del Lenguaje

## 1. Variables

Una **variable** es un espacio en memoria que guarda un dato. En Kotlin existen dos formas de declararlas:

### 1.1. `val` — Inmutable

Una vez asignado el valor, no puede cambiarse (equivalente a `final` en Java).

```kotlin
val pi = 3.1416
println("El valor de PI es $pi")
```

### 1.2. `var` — Mutable

El valor puede modificarse en cualquier momento.

```kotlin
var contador = 0
contador += 1
println("Contador: $contador")  // Salida: Contador: 1
```

### 1.3. Tipos de datos

Kotlin infiere el tipo automáticamente, aunque también puedes declararlo de forma explícita:

| Tipo | Descripción | Ejemplo |
|---|---|---|
| `Byte` | Entero de 8 bits | `val a: Byte = 1` |
| `Short` | Entero de 16 bits | `val b: Short = 100` |
| `Int` | Entero de 32 bits (por defecto) | `val c: Int = 1000` |
| `Long` | Entero de 64 bits | `val d: Long = 100000L` |
| `Float` | Decimal de 32 bits | `val e: Float = 1.5f` |
| `Double` | Decimal de 64 bits (por defecto) | `val f: Double = 3.14` |
| `Char` | Carácter individual | `val letra: Char = 'A'` |
| `String` | Cadena de texto | `val saludo: String = "Hola"` |
| `Boolean` | `true` o `false` | `val esMayor = true` |

### 1.4. Operadores aritméticos

| Operador | Nombre | Ejemplo | Resultado |
|---|---|---|---|
| `+` | Suma | `5 + 3` | `8` |
| `-` | Resta | `10 - 4` | `6` |
| `*` | Multiplicación | `6 * 2` | `12` |
| `/` | División | `9 / 3` | `3` |
| `%` | Módulo (resto) | `10 % 3` | `1` |

!!! info "División entre enteros"
    Una división entre dos enteros devuelve un entero: `7 / 2` → `3`.
    Para obtener decimales, al menos uno de los operandos debe ser `Double` o `Float`: `7.0 / 2` → `3.5`.

### 1.5. Null Safety

Por defecto las variables en Kotlin **no pueden ser nulas**. El siguiente código no compila:

```kotlin
var nombre: String = "Pol"
nombre = null  // ❌ Error de compilación
```

Para permitir valores nulos se añade `?` al tipo:

```kotlin
var nombre: String? = "Pol"
nombre = null  // ✅ Correcto
```

Kotlin ofrece tres operadores para trabajar con nullables de forma segura:

| Operador | Nombre | Descripción |
|---|---|---|
| `?.` | Llamada segura | Ejecuta el método solo si el valor no es `null` |
| `?:` | Operador Elvis | Devuelve un valor por defecto si el valor es `null` |
| `!!` | Not-null assertion | Fuerza el acceso; lanza `NullPointerException` si es `null` |

!!! success "🔍 Ejecutar y Analizar"
    Ejecuta estos ejemplos y observa el comportamiento con `null`:

```kotlin
// Llamada segura (?.)
var nombre: String? = null
println(nombre?.length)   // null (no lanza error)
nombre = "Pol"
println(nombre?.length)   // 3

// Operador Elvis (?:)
var mostrar = nombre ?: "Desconocido"
println(mostrar)          // Pol

// Uso con if
if (nombre != null) {
    println("Hola, ${nombre.uppercase()}")
} else {
    println("Nombre no disponible")
}
```

---

## 2. Entrada y Salida Estándar

| Función | Descripción |
|---|---|
| `print()` | Imprime sin salto de línea al final |
| `println()` | Imprime con salto de línea al final |
| `readln()` | Lee una línea de texto del teclado (`String`, no nulo) |

!!! info "`readLine()` vs `readln()`"
    Desde Kotlin 1.6 se prefiere `readln()`, que devuelve `String` directamente (sin nullable). La función antigua `readLine()` devolvía `String?`.

!!! success "🔍 Ejecutar y Analizar"
    Crea un programa que pida la edad al usuario y calcule cuántos años tendrá el próximo año:

```kotlin
fun main() {
    print("Introduce tu edad: ")
    val entrada = readln()
    if (entrada.isNotBlank()) {
        val edad = entrada.toInt()
        println("Tendrás ${edad + 1} años el próximo año")
    } else {
        println("Edad no válida")
    }
}
```

---

## 3. Condicionales

### 3.1. Operadores relacionales y lógicos

| Operador | Significado | Ejemplo | Resultado |
|---|---|---|---|
| `==` | Igual a | `5 == 5` | `true` |
| `!=` | Distinto de | `5 != 3` | `true` |
| `>` | Mayor que | `10 > 7` | `true` |
| `<` | Menor que | `3 < 8` | `true` |
| `>=` | Mayor o igual que | `6 >= 6` | `true` |
| `<=` | Menor o igual que | `4 <= 9` | `true` |
| `&&` | AND | `(a > 0 && b > 0)` | `true` si ambas son `true` |
| `\|\|` | OR | `(a > 0 \|\| b > 0)` | `true` si alguna es `true` |
| `!` | NOT | `!activo` | Invierte el valor |

### 3.2. `if` / `else`

!!! success "🔍 Ejecutar y Analizar"

```kotlin
// Condicional simple
val edad = 18
if (edad >= 18) {
    println("Eres mayor de edad")
}

// if-else
val numero = 5
if (numero % 2 == 0) {
    println("Es par")
} else {
    println("Es impar")
}

// if como expresión (devuelve un valor)
val a = 10; val b = 7
val max = if (a > b) a else b
println("El mayor es $max")
```

### 3.3. `when`

`when` es el equivalente en Kotlin del `switch` de Java, pero mucho más potente: puede trabajar con valores, rangos y condiciones.

!!! success "🔍 Ejecutar y Analizar"

```kotlin
// when con valor concreto
val dia = 3
val nombreDia = when (dia) {
    1 -> "Lunes"
    2 -> "Martes"
    3 -> "Miércoles"
    4 -> "Jueves"
    5 -> "Viernes"
    else -> "Fin de semana"
}
println(nombreDia)  // Miércoles

// when con condiciones (sin argumento)
val nota = 85
val resultado = when {
    nota >= 90 -> "Sobresaliente"
    nota >= 70 -> "Notable"
    nota >= 50 -> "Aprobado"
    else       -> "Suspenso"
}
println(resultado)  // Notable
```

---

## 4. Estructuras Repetitivas

| Estructura | ¿Cuándo usarla? | ¿Evalúa condición primero? | ¿Se ejecuta al menos una vez? |
|---|---|---|---|
| `while` | Cuando no sabemos cuántas veces, depende de condición | Sí | No |
| `do-while` | Cuando queremos que se ejecute al menos una vez | No | Sí |
| `for` | Cuando sabemos cuántas veces o recorremos algo | Sí | Sí (si hay elementos) |
| `repeat` | Para repetir un número fijo de veces | — | — |

### 4.1. `while`

```kotlin
var contador = 1
while (contador <= 5) {
    println("Contador: $contador")
    contador++
}
```

### 4.2. `do-while`

```kotlin
var contador = 1
do {
    println("Contador: $contador")
    contador++
} while (contador <= 5)
```

### 4.3. `for`

```kotlin
// Rango cerrado (1 hasta 5 inclusive)
for (i in 1..5) {
    println("Iteración: $i")
}

// Rango abierto por la derecha (1 hasta 4)
for (i in 1..<5) {
    println(i)
}

// Con paso personalizado
for (i in 0..10 step 2) {
    println(i)  // 0, 2, 4, 6, 8, 10
}

// Hacia atrás
for (i in 5 downTo 1) {
    println(i)  // 5, 4, 3, 2, 1
}
```

### 4.4. `repeat`

```kotlin
repeat(3) {
    println("Hola")
}
```

!!! success "🔍 Ejecutar y Analizar"
    Escribe un programa que imprima la tabla de multiplicar del 7:

```kotlin
fun main() {
    val numero = 7
    for (i in 1..10) {
        println("$numero × $i = ${numero * i}")
    }
}
```

    Salida esperada:
    ```
    7 × 1 = 7
    7 × 2 = 14
    ...
    7 × 10 = 70
    ```
