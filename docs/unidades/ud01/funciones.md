# Funciones y Lambdas

## 1. Funciones

Las **funciones** son bloques de código con nombre que realizan una tarea específica. Permiten organizar y reutilizar el código evitando repeticiones.

### 1.1. Función sin parámetros ni retorno

```kotlin
fun saludar() {
    println("¡Hola, bienvenidos a la clase!")
}
saludar()
```

### 1.2. Función con parámetros

```kotlin
fun saludarEstudiante(nombre: String) {
    println("Hola, $nombre")
}
saludarEstudiante("Pol")
```

### 1.3. Función con valor de retorno

```kotlin
fun sumar(a: Int, b: Int): Int {
    return a + b
}
val suma = sumar(2, 3)
println(suma)  // 5
```

### 1.4. Forma simplificada (expresión única)

Cuando la función se reduce a una sola expresión, se puede escribir en una línea:

```kotlin
fun multiplicar(a: Int, b: Int) = a * b
println(multiplicar(3, 5))  // 15
```

### 1.5. Parámetros con valor por defecto

```kotlin
fun saludar(nombre: String = "Mundo") {
    println("Hola, $nombre")
}
saludar()       // Hola, Mundo
saludar("Eli")  // Hola, Eli
```

### 1.6. Argumentos nombrados

Permiten pasar los parámetros en cualquier orden usando su nombre:

```kotlin
fun mostrarMensaje(mensaje: String, veces: Int = 1) {
    repeat(veces) { println(mensaje) }
}
mostrarMensaje("Hola a todos", 2)
mostrarMensaje(veces = 3, mensaje = "¡Kotlin!")
```

### 1.7. Funciones con Array como parámetro y retorno

!!! success "🔍 Ejecutar y Analizar"

```kotlin
fun duplicarValores(numeros: Array<Int>): Array<Int> {
    return Array(numeros.size) { i -> numeros[i] * 2 }
}

fun main() {
    val original = arrayOf(1, 2, 3)
    val duplicados = duplicarValores(original)
    println("Original:   ${original.joinToString()}")    // 1, 2, 3
    println("Duplicados: ${duplicados.joinToString()}")  // 2, 4, 6
}
```

### 1.8. Funciones locales

Una **función local** se define dentro de otra función y solo puede usarse allí:

```kotlin
fun procesarTexto(texto: String) {
    fun limpiar(cadena: String): String = cadena.trim().lowercase()
    val resultado = limpiar(texto)
    println("Texto procesado: $resultado")
}
procesarTexto("   Hola Mundo   ")  // hola mundo
```

### 1.9. `vararg` — Número variable de argumentos

Un parámetro `vararg` acepta cualquier número de argumentos del mismo tipo:

```kotlin
fun saludarVarios(vararg nombres: String) {
    for (nombre in nombres) {
        println("Hola, $nombre")
    }
}
saludarVarios("Pol", "Eli", "Ade")

// Combinar con otros parámetros
fun mostrarNumeros(titulo: String, vararg numeros: Int) {
    println(titulo)
    for (n in numeros) { println(n) }
}
mostrarNumeros("Números:", 3, 5, 7)

// Pasar un array existente con el operador *spread*
val lista = intArrayOf(1, 2, 3)
saludarVarios(*arrayOf("X", "Y"))  // * expande el array
```

### 1.10. Funciones de orden superior

Una **función de orden superior** recibe o devuelve funciones como si fueran valores:

!!! success "🔍 Ejecutar y Analizar"

```kotlin
// Recibe una función como parámetro
fun opera(a: Int, b: Int, op: (Int, Int) -> Int): Int = op(a, b)

fun main() {
    val suma  = opera(3, 4) { x, y -> x + y }
    val resta = opera(10, 5) { x, y -> x - y }
    println("Suma: $suma")    // 7
    println("Resta: $resta")  // 5
}
```

```kotlin
// Devuelve una función como valor
fun crearMultiplicador(factor: Int): (Int) -> Int {
    return { numero -> numero * factor }
}

val porTres = crearMultiplicador(3)
println(porTres(5))   // 15
println(porTres(10))  // 30
```

---

## 2. Funciones Lambda

Las **funciones lambda** son funciones anónimas (sin nombre). Se usan para operaciones cortas y mejoran la legibilidad del código.

### Sintaxis básica

```kotlin
// Lambda con parámetro
val saludar = { nombre: String -> println("Hola, $nombre") }
saludar("Pol")

// Lambda sin parámetros
val decirHola = { println("Hola") }
decirHola()

// Lambda con múltiples parámetros y retorno
val sumar = { a: Int, b: Int -> a + b }
println(sumar(3, 4))  // 7
```

### Lambdas con colecciones

Las lambdas brillan especialmente cuando se usan con las funciones de extensión de las colecciones:

!!! success "🔍 Ejecutar y Analizar"

```kotlin
val numeros = intArrayOf(1, 2, 3, 4, 5)

// forEach — recorrer todos los elementos
numeros.forEach { println(it) }

// map — transformar cada elemento en otro valor
val dobles = numeros.map { it * 2 }
println(dobles)  // [2, 4, 6, 8, 10]

// filter — quedarse solo con los que cumplen la condición
val pares = numeros.filter { it % 2 == 0 }
println(pares)  // [2, 4]

// Encadenamiento
val resultado = numeros
    .filter { it > 2 }
    .map { it * 10 }
println(resultado)  // [30, 40, 50]
```

!!! info "`it` — el parámetro implícito"
    Cuando una lambda tiene un único parámetro, no es necesario nombrarlo. Kotlin lo expone automáticamente con el nombre `it`.

### Tabla de funciones lambda más usadas

| Función | Qué hace | Ejemplo |
|---|---|---|
| `forEach { }` | Itera sobre cada elemento | `lista.forEach { println(it) }` |
| `map { }` | Transforma cada elemento | `lista.map { it * 2 }` |
| `filter { }` | Filtra elementos por condición | `lista.filter { it > 0 }` |
| `any { }` | `true` si algún elemento cumple la condición | `lista.any { it > 10 }` |
| `all { }` | `true` si todos cumplen la condición | `lista.all { it > 0 }` |
| `count { }` | Cuenta los que cumplen la condición | `lista.count { it % 2 == 0 }` |
| `sortedBy { }` | Devuelve una nueva lista ordenada | `lista.sortedBy { it.nombre }` |
| `firstOrNull { }` | Primer elemento que cumple o `null` | `lista.firstOrNull { it > 5 }` |
