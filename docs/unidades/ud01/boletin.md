# Boletín de Ejercicios — Repaso de Kotlin

!!! abstract "Objetivos del boletín"
    Este boletín repasa los conceptos fundamentales de la UD1: variables, tipos, condicionales, bucles, colecciones, funciones y POO. Los ejercicios están ordenados de menor a mayor dificultad.

---

## 🎯 Ejercicio 1. Calculadora de IMC

!!! warning "🎯 Ejercicio 1. Calculadora de IMC"

    Escribe un programa que pida al usuario su **peso** (en kg) y su **altura** (en metros) y calcule el **Índice de Masa Corporal** (IMC = peso / altura²).

    Muestra el resultado con dos decimales y clasifícalo según la siguiente tabla:

    | IMC | Clasificación |
    |---|---|
    | < 18.5 | Bajo peso |
    | 18.5 – 24.9 | Peso normal |
    | 25.0 – 29.9 | Sobrepeso |
    | ≥ 30.0 | Obesidad |

    Ejemplo de ejecución:
    ```
    Introduce tu peso (kg): 70
    Introduce tu altura (m): 1.75
    Tu IMC es: 22.86 — Peso normal
    ```

??? quote "Solución"
    ```kotlin
    fun main() {
        print("Introduce tu peso (kg): ")
        val peso = readln().toDouble()
        print("Introduce tu altura (m): ")
        val altura = readln().toDouble()

        val imc = peso / (altura * altura)
        val clasificacion = when {
            imc < 18.5 -> "Bajo peso"
            imc < 25.0 -> "Peso normal"
            imc < 30.0 -> "Sobrepeso"
            else       -> "Obesidad"
        }

        println("Tu IMC es: ${"%.2f".format(imc)} — $clasificacion")
    }
    ```

---

## 🎯 Ejercicio 2. Calificación con letras

!!! warning "🎯 Ejercicio 2. Calificación con letras"

    Escribe una función `notaALetra(nota: Int): String` que reciba una nota numérica (0–10) y devuelva su calificación según este criterio:

    | Nota | Calificación |
    |---|---|
    | 9 – 10 | Sobresaliente |
    | 7 – 8 | Notable |
    | 5 – 6 | Aprobado |
    | 0 – 4 | Suspenso |

    Crea un `main` que pruebe la función con al menos cinco notas distintas.

    Nota: si el valor está fuera del rango 0–10, la función debe devolver `"Nota inválida"`.

??? quote "Solución"
    ```kotlin
    fun notaALetra(nota: Int): String = when {
        nota < 0 || nota > 10 -> "Nota inválida"
        nota >= 9  -> "Sobresaliente"
        nota >= 7  -> "Notable"
        nota >= 5  -> "Aprobado"
        else       -> "Suspenso"
    }

    fun main() {
        val notas = listOf(10, 8, 6, 3, -1, 11)
        notas.forEach { nota ->
            println("Nota $nota → ${notaALetra(nota)}")
        }
    }
    ```

---

## 🎯 Ejercicio 3. FizzBuzz

!!! warning "🎯 Ejercicio 3. FizzBuzz"

    Escribe un programa que imprima los números del **1 al 50** con las siguientes reglas:

    - Si el número es múltiplo de **3**, imprime `Fizz`.
    - Si el número es múltiplo de **5**, imprime `Buzz`.
    - Si el número es múltiplo de **3 y de 5**, imprime `FizzBuzz`.
    - En cualquier otro caso, imprime el número.

    Las primeras líneas de salida esperadas:
    ```
    1  2  Fizz  4  Buzz  Fizz  7  8  Fizz  Buzz  11  Fizz  13  14  FizzBuzz ...
    ```

??? quote "Solución"
    ```kotlin
    fun main() {
        for (i in 1..50) {
            val resultado = when {
                i % 15 == 0 -> "FizzBuzz"
                i % 3  == 0 -> "Fizz"
                i % 5  == 0 -> "Buzz"
                else        -> i.toString()
            }
            print("$resultado  ")
        }
        println()
    }
    ```

---

## 🎯 Ejercicio 4. Sucesión de Fibonacci

!!! warning "🎯 Ejercicio 4. Sucesión de Fibonacci"

    Escribe una función `fibonacci(n: Int): List<Int>` que devuelva los primeros `n` términos de la sucesión de Fibonacci.

    Recuerda: cada término es la suma de los dos anteriores. Los dos primeros son 0 y 1.

    ```
    fibonacci(8) → [0, 1, 1, 2, 3, 5, 8, 13]
    ```

    Llama a la función desde `main` y muestra el resultado.

??? quote "Solución"
    ```kotlin
    fun fibonacci(n: Int): List<Int> {
        if (n <= 0) return emptyList()
        if (n == 1) return listOf(0)

        val lista = mutableListOf(0, 1)
        for (i in 2 until n) {
            lista.add(lista[i - 1] + lista[i - 2])
        }
        return lista
    }

    fun main() {
        println(fibonacci(8))   // [0, 1, 1, 2, 3, 5, 8, 13]
        println(fibonacci(12))
    }
    ```

---

## 🎯 Ejercicio 5. Palíndromo

!!! warning "🎯 Ejercicio 5. Palíndromo"

    Escribe una función `esPalindromo(texto: String): Boolean` que determine si una cadena de texto es un palíndromo (se lee igual de izquierda a derecha que de derecha a izquierda), ignorando mayúsculas, minúsculas y espacios.

    ```
    esPalindromo("Anita lava la tina") → true
    esPalindromo("Kotlin")             → false
    esPalindromo("A man a plan")       → false
    ```

    Pruébala con al menos cuatro palabras o frases distintas.

??? quote "Solución"
    ```kotlin
    fun esPalindromo(texto: String): Boolean {
        val limpio = texto.lowercase().replace(" ", "")
        return limpio == limpio.reversed()
    }

    fun main() {
        val pruebas = listOf(
            "Anita lava la tina",
            "Kotlin",
            "reconocer",
            "hola mundo"
        )
        pruebas.forEach { t ->
            println("\"$t\" → ${esPalindromo(t)}")
        }
    }
    ```

---

## 🎯 Ejercicio 6. Estadísticas de una lista

!!! warning "🎯 Ejercicio 6. Estadísticas de una lista"

    Dada la siguiente lista de notas de una clase:
    ```kotlin
    val notas = listOf(6, 8, 4, 9, 5, 7, 3, 10, 6, 8, 5, 7)
    ```

    Usando funciones de extensión de Kotlin (sin bucles explícitos), calcula y muestra:

    1. La nota **máxima** y la nota **mínima**.
    2. La nota **media** (con dos decimales).
    3. Cuántos alumnos han **aprobado** (nota ≥ 5).
    4. La lista de notas **ordenada de mayor a menor**.
    5. Las notas **únicas** (sin repetidos), también ordenadas.

??? quote "Solución"
    ```kotlin
    fun main() {
        val notas = listOf(6, 8, 4, 9, 5, 7, 3, 10, 6, 8, 5, 7)

        println("Máxima: ${notas.max()}")
        println("Mínima: ${notas.min()}")
        println("Media:  ${"%.2f".format(notas.average())}")
        println("Aprobados: ${notas.count { it >= 5 }}")
        println("Ordenadas (desc): ${notas.sortedDescending()}")
        println("Únicas (asc):     ${notas.toSortedSet()}")
    }
    ```

---

## 🎯 Ejercicio 7. Frecuencia de caracteres

!!! warning "🎯 Ejercicio 7. Frecuencia de caracteres"

    Escribe un programa que pida al usuario una **frase** y muestre cuántas veces aparece cada letra (ignorando espacios y sin distinguir mayúsculas/minúsculas). Ordena la salida por frecuencia descendente.

    Ejemplo para `"Hola Kotlin"`:
    ```
    l → 2
    o → 2
    h → 1
    a → 1
    k → 1
    t → 1
    i → 1
    n → 1
    ```

??? quote "Solución"
    ```kotlin
    fun main() {
        print("Introduce una frase: ")
        val frase = readln().lowercase().replace(" ", "")

        val frecuencia = mutableMapOf<Char, Int>()
        for (c in frase) {
            frecuencia[c] = (frecuencia[c] ?: 0) + 1
        }

        frecuencia.entries
            .sortedByDescending { it.value }
            .forEach { (letra, veces) -> println("$letra → $veces") }
    }
    ```

---

## 🎯 Ejercicio 8. Agenda de contactos

!!! warning "🎯 Ejercicio 8. Agenda de contactos"

    Crea una `data class Contacto(val nombre: String, val telefono: String, val email: String)`.

    Implementa un programa que:

    1. Cree una agenda con al menos **5 contactos** predefinidos.
    2. Pida al usuario un nombre y **busque el contacto** en la agenda (sin distinguir mayúsculas).
    3. Si lo encuentra, muestre sus datos; si no, lo indique.
    4. Muestre la **lista completa** ordenada alfabéticamente por nombre.

??? quote "Solución"
    ```kotlin
    data class Contacto(val nombre: String, val telefono: String, val email: String)

    fun main() {
        val agenda = mutableListOf(
            Contacto("Pol",  "600111222", "pol@mail.com"),
            Contacto("Eli",  "600333444", "eli@mail.com"),
            Contacto("Ade",  "600555666", "ade@mail.com"),
            Contacto("Fer",  "600777888", "fer@mail.com"),
            Contacto("Sam",  "600999000", "sam@mail.com")
        )

        print("Buscar contacto: ")
        val busqueda = readln()
        val encontrado = agenda.firstOrNull {
            it.nombre.lowercase() == busqueda.lowercase()
        }

        if (encontrado != null) {
            println("Nombre:    ${encontrado.nombre}")
            println("Teléfono:  ${encontrado.telefono}")
            println("Email:     ${encontrado.email}")
        } else {
            println("Contacto \"$busqueda\" no encontrado.")
        }

        println("\nAgenda completa (A–Z):")
        agenda.sortedBy { it.nombre }.forEach {
            println("  ${it.nombre} — ${it.telefono}")
        }
    }
    ```

---

## 🎯 Ejercicio 9. Cuentas bancarias

!!! warning "🎯 Ejercicio 9. Cuentas bancarias"

    Diseña un sistema de cuentas bancarias usando herencia:

    - Clase base `Cuenta(val titular: String, var saldo: Double)` con los métodos `depositar(cantidad: Double)`, `retirar(cantidad: Double): Boolean` (devuelve `false` si no hay saldo suficiente) y `mostrarInfo()`.
    - Subclase `CuentaCorriente(titular: String, saldo: Double)`: sin características adicionales (hereda todo de `Cuenta`).
    - Subclase `CuentaAhorro(titular: String, saldo: Double, val tasaInteres: Double)`: añade el método `aplicarInteres()` que incrementa el saldo según la tasa de interés.

    Crea al menos dos cuentas de cada tipo, realiza operaciones y muestra el resultado final.

??? quote "Solución"
    ```kotlin
    open class Cuenta(val titular: String, var saldo: Double) {

        fun depositar(cantidad: Double) {
            saldo += cantidad
            println("Depósito de $cantidad en cuenta de $titular. Saldo: $saldo")
        }

        fun retirar(cantidad: Double): Boolean {
            return if (cantidad <= saldo) {
                saldo -= cantidad
                println("Retirada de $cantidad. Saldo restante: $saldo")
                true
            } else {
                println("Fondos insuficientes para retirar $cantidad (saldo: $saldo)")
                false
            }
        }

        open fun mostrarInfo() = println("[$titular] Saldo: $saldo €")
    }

    class CuentaCorriente(titular: String, saldo: Double) : Cuenta(titular, saldo)

    class CuentaAhorro(
        titular: String,
        saldo: Double,
        val tasaInteres: Double
    ) : Cuenta(titular, saldo) {

        fun aplicarInteres() {
            val intereses = saldo * tasaInteres
            saldo += intereses
            println("Intereses aplicados (+${"%.2f".format(intereses)}). Nuevo saldo: $saldo")
        }

        override fun mostrarInfo() {
            super.mostrarInfo()
            println("  Tasa de interés: ${tasaInteres * 100}%")
        }
    }

    fun main() {
        val cc = CuentaCorriente("Pol", 500.0)
        val ca = CuentaAhorro("Eli", 1000.0, 0.03)

        cc.depositar(200.0)
        cc.retirar(100.0)
        cc.retirar(700.0)  // fondos insuficientes

        ca.depositar(500.0)
        ca.aplicarInteres()

        println("\n--- Resumen ---")
        cc.mostrarInfo()
        ca.mostrarInfo()
    }
    ```

---

## 🎯 Ejercicio 10. Catálogo de videojuegos

!!! warning "🎯 Ejercicio 10. Catálogo de videojuegos"

    Crea una `data class Videojuego(val titulo: String, val genero: String, val anio: Int, val nota: Double)`.

    A continuación, construye un **catálogo** con los siguientes juegos y realiza las operaciones indicadas **usando únicamente funciones lambda** (`filter`, `map`, `sortedBy`, `maxByOrNull`, etc.):

    | Título | Género | Año | Nota |
    |---|---|---|---|
    | The Legend of Zelda: BOTW | Aventura | 2017 | 9.3 |
    | Red Dead Redemption 2 | Acción | 2018 | 9.5 |
    | Hades | Roguelike | 2020 | 9.0 |
    | Hollow Knight | Metroidvania | 2017 | 8.9 |
    | Celeste | Plataformas | 2018 | 9.0 |

    1. Muestra los juegos con **nota ≥ 9.0**, ordenados de mayor a menor nota.
    2. Lista los **títulos** (solo el nombre) de los juegos publicados en **2017 o después**.
    3. Calcula la **nota media** del catálogo.
    4. Encuentra el juego con la **nota más alta**.

??? quote "Solución"
    ```kotlin
    data class Videojuego(
        val titulo: String,
        val genero: String,
        val anio: Int,
        val nota: Double
    )

    fun main() {
        val catalogo = listOf(
            Videojuego("The Legend of Zelda: BOTW", "Aventura",     2017, 9.3),
            Videojuego("Red Dead Redemption 2",     "Acción",       2018, 9.5),
            Videojuego("Hades",                     "Roguelike",    2020, 9.0),
            Videojuego("Hollow Knight",             "Metroidvania", 2017, 8.9),
            Videojuego("Celeste",                   "Plataformas",  2018, 9.0)
        )

        // 1. Nota ≥ 9.0, de mayor a menor
        println("=== Nota ≥ 9.0 ===")
        catalogo
            .filter { it.nota >= 9.0 }
            .sortedByDescending { it.nota }
            .forEach { println("  ${it.titulo} ★${it.nota}") }

        // 2. Títulos de 2017 en adelante
        println("\n=== Desde 2017 ===")
        catalogo
            .filter { it.anio >= 2017 }
            .map { it.titulo }
            .forEach { println("  $it") }

        // 3. Nota media
        val media = catalogo.map { it.nota }.average()
        println("\nNota media del catálogo: ${"%.2f".format(media)}")

        // 4. Mejor valorado
        val mejor = catalogo.maxByOrNull { it.nota }
        println("Mejor valorado: ${mejor?.titulo} (★${mejor?.nota})")
    }
    ```

    Salida esperada:
    ```
    === Nota ≥ 9.0 ===
      Red Dead Redemption 2 ★9.5
      The Legend of Zelda: BOTW ★9.3
      Hades ★9.0
      Celeste ★9.0

    === Desde 2017 ===
      The Legend of Zelda: BOTW
      Red Dead Redemption 2
      Hades
      Hollow Knight
      Celeste

    Nota media del catálogo: 9.14
    Mejor valorado: Red Dead Redemption 2 (★9.5)
    ```
