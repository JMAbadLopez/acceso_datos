package org.example.ejemplos

import java.io.*

// Clase Persona (serializable completamente)
class Persona(val nombre: String, val edad: Int) : Serializable

// Clase Usuario con un atributo que NO se serializa
class Usuario(
    val nombre: String,
    @Transient val clave: String // Este campo no se guardará
) : Serializable

fun main() {

    val rutaPersona = "documentos/persona.obj"
    val rutaUsuario = "documentos/usuario.obj"

    // Asegurar que el directorio exista
    val directorio = File("documentos")

    if (!directorio.exists()) {
        directorio.mkdirs()
    }

    // --- Serializar Persona ---
    val persona = Persona("Pol", 30)
    try {
        ObjectOutputStream(FileOutputStream(rutaPersona)).use { oos ->
            oos.writeObject(persona)
        }
        println ("Persona serializada.")
    } catch (e: IOException) {
        println ("Error al serializar Persona: ${e.message}")
    }

    // --- Deserializar Persona ---
    try {
        val personaLeida = ObjectInputStream(FileInputStream(rutaPersona)).use { ois ->
            ois.readObject() as Persona
        }
        println ("Persona deserializada:")
        println ("Nombre: ${personaLeida.nombre}, Edad: ${personaLeida.edad}")
    } catch (e: Exception) {
        println ("Error al deserializar Persona: ${e.message}")
    }

    // --- Serializar Usuario ---
    val usuario = Usuario("Eli", "1234")
    try {
        ObjectOutputStream(FileOutputStream(rutaUsuario)).use { oos ->
            oos.writeObject(usuario)
        }
        println ("Usuario serializado.")
    } catch (e: IOException) {
        println ("Error al serializar Usuario: ${e.message}")
    }
    // --- Deserializar Usuario ---
    try {
        val usuarioLeido = ObjectInputStream(FileInputStream(rutaUsuario)).use { ois ->
            ois.readObject() as Usuario
        }
        println ("Usuario deserializado:")
        println ("Nombre: ${usuarioLeido.nombre}, Clave: ${usuarioLeido.clave}")
    } catch (e: Exception) {
        println ("Error al deserializar Usuario: ${e.message}")
    }
}