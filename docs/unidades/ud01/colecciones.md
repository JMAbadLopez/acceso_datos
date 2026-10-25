# Colecciones y Estructuras de Datos

Las **colecciones** son estructuras que permiten almacenar y manipular grupos de datos de forma organizada. Las principales en Kotlin son: `Array`, `List`, `Set` y `Map`.

## 1. Array

Colección **ordenada de tamaño fijo**. Todos sus elementos son del mismo tipo y se accede a ellos mediante un índice. Se usa cuando el número de elementos es conocido y no va a cambiar.

### Crear un Array

```kotlin
// 1. Array vacío de tamaño fijo (valores inicializados a 0)
val calificaciones = IntArray(5)
calificaciones[0] = 95

// 2. Con valores directamente
val edades = intArrayOf(15, 18, 20)

// 3. Con arrayOf (funciona con cualquier tipo, incluido String)
val numeros = arrayOf(10, 20, 30, 40, 50)
val nombres = arrayOf("Pol", "Eli", "Ade")
```

### Operaciones básicas

```kotlin
println(numeros[0])              // Acceso por índice → 10
numeros[2] = 99                  // Modificar un elemento
println("Tamaño: ${numeros.size}")  // Tamaño del array
```

### Recorrer un Array

!!! success "🔍 Ejecutar y Analizar"

```kotlin
val numeros = arrayOf(10, 20, 30, 40, 50)

// Por valor (más habitual)
for (numero in numeros) {
    println(numero)
}

// Por índice (idiomático en Kotlin)
for (i in numeros.indices) {
    println("[$i] = ${numeros[i]}")
}
```

---

## 2. List (Lista)

Colección **ordenada que permite duplicados**. Puede ser **inmutable** (`List`, no se puede modificar) o **mutable** (`MutableList`). Es la colección más utilizada en Kotlin.

```kotlin
// Lista inmutable
val nombres = listOf("Pol", "Eli", "Ade")
println(nombres)  // [Pol, Eli, Ade]
println(nombres[1])  // Eli

// Lista mutable
val nombresMutable = mutableListOf("Pol", "Eli")
nombresMutable.add("Ade")           // Añadir al final
nombresMutable.add(1, "Fer")        // Insertar en posición 1
nombresMutable[0] = "Sam"           // Modificar por índice
nombresMutable.remove("Sam")        // Eliminar por valor
nombresMutable.removeAt(0)          // Eliminar por índice
println("Tamaño: ${nombresMutable.size}")
nombresMutable.clear()              // Vaciar la lista
```

!!! success "🔍 Ejecutar y Analizar"
    Gestión básica de una lista de nombres:

```kotlin
fun main() {
    val alumnos = mutableListOf("Pol", "Eli", "Ade", "Fer")
    println("Lista inicial: $alumnos")

    alumnos.add("Sam")
    println("Después de añadir: $alumnos")

    alumnos.remove("Ade")
    println("Después de eliminar: $alumnos")

    alumnos.sort()
    println("Ordenada: $alumnos")
}
```

    Salida esperada:
    ```
    Lista inicial: [Pol, Eli, Ade, Fer]
    Después de añadir: [Pol, Eli, Ade, Fer, Sam]
    Después de eliminar: [Pol, Eli, Fer, Sam]
    Ordenada: [Eli, Fer, Pol, Sam]
    ```

---

## 3. Set (Conjunto)

Colección **sin duplicados** y sin orden garantizado. Útil cuando solo interesa saber si un elemento está presente o no. También puede ser inmutable (`Set`) o mutable (`MutableSet`).

```kotlin
// Set inmutable: "Rojo" solo se guarda una vez
val colores = setOf("Rojo", "Verde", "Rojo")
println(colores)  // [Rojo, Verde]

// Set mutable
val frutas = mutableSetOf("Manzana", "Banana", "Uva")
frutas.add("Naranja")            // Añadir
frutas.add("Banana")             // No tiene efecto (ya existe)
frutas.remove("Uva")             // Eliminar

// Comprobar si existe un elemento
if ("Banana" in frutas) {
    println("Sí hay Banana")
}

// Recorrer
for (fruta in frutas) {
    println("Fruta: $fruta")
}
```

---

## 4. Map (Mapa / Diccionario)

Colección de pares **clave → valor**. Cada clave es única. Puede ser inmutable (`Map`) o mutable (`MutableMap`). Muy útil para representar relaciones o búsquedas rápidas por clave.

```kotlin
// Map inmutable
val edades = mapOf("Pol" to 18, "Ade" to 20)
println(edades["Ade"])   // 20
println(edades["Fer"])   // null (clave no encontrada)

// Map mutable
val datos = mutableMapOf<String, Int>()
datos["Pol"] = 25
datos["Eli"] = 20
datos["Pol"] = 26        // Sobreescribe el valor de "Pol"

println("Pol tiene ${datos["Pol"]} años")

// Recorrer pares clave-valor
for ((nombre, edad) in datos) {
    println("$nombre → $edad años")
}
```

!!! success "🔍 Ejecutar y Analizar"
    Frecuencia de palabras con un Map:

```kotlin
fun main() {
    val texto = "hola mundo hola kotlin mundo mundo"
    val palabras = texto.split(" ")
    val frecuencia = mutableMapOf<String, Int>()

    for (palabra in palabras) {
        frecuencia[palabra] = (frecuencia[palabra] ?: 0) + 1
    }

    println("Frecuencia de palabras:")
    for ((palabra, veces) in frecuencia) {
        println("  $palabra → $veces")
    }
}
```

    Salida esperada:
    ```
    Frecuencia de palabras:
      hola → 2
      mundo → 3
      kotlin → 1
    ```

---

## 5. Resumen Comparativo

| Estructura | Ordenada | Permite duplicados | Acceso | Modificable |
|---|---|---|---|---|
| `Array` | ✅ Sí | ✅ Sí | Por índice | Fijo (no crece) |
| `List` | ✅ Sí | ✅ Sí | Por índice | Si es `MutableList` |
| `Set` | ❌ No | ❌ No | Por valor | Si es `MutableSet` |
| `Map` | ❌ No | ❌ en claves | Por clave | Si es `MutableMap` |
