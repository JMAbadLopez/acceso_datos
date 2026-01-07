package edu.gva.es.services

import edu.gva.es.data.UsuariosDAO
import edu.gva.es.domain.UsuarioDTO

/**
 * Capa de Servicio: Aquí reside la lógica de negocio.
 * Se encarga de validar datos y coordinar las llamadas al DAO.
 */
object UsuariosService {

    /**
     * Valida las credenciales de un usuario.
     * @return El usuario si el email y password coinciden, null en caso contrario.
     */
    fun buscarPorEmail(mail: String, pass: String): UsuarioDTO? {
        val usuario = UsuariosDAO.seleccionarPorEmail(mail)
        // Regla de negocio: Validamos la contraseña en la capa de servicio
        return if (usuario != null && usuario.password == pass) usuario else null
    }

    /**
     * Obtiene todos los usuarios. Podría aplicar filtros o lógica adicional.
     */
    fun listarUsuarios(): List<UsuarioDTO> {
        return UsuariosDAO.seleccionarTodos()
    }

    /**
     * Busca un usuario por ID.
     */
    fun buscarPorId(id: Int): UsuarioDTO? {
        return UsuariosDAO.seleccionarPorId(id)
    }

    /**
     * Lógica para crear un usuario.
     * Aquí podríamos verificar si el email ya existe antes de insertar.
     */
    fun registrarUsuario(usuario: UsuarioDTO): Int {
        // Ejemplo de regla de negocio: Comprobar si el mail ya está en la lista
        val existe = UsuariosDAO.seleccionarTodos().any { it.mail == usuario.mail }
        if (existe) return -1

        return UsuariosDAO.insertar(usuario)
    }

    /**
     * Actualiza un usuario existente.
     */
    fun actualizarUsuario(id: Int, usuario: UsuarioDTO): Int {
        return UsuariosDAO.actualizar(id, usuario)
    }

    /**
     * Elimina un usuario.
     */
    fun borrarUsuario(id: Int): Int {
        return UsuariosDAO.eliminar(id)
    }
}