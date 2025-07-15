package com.alkemy.tdd.service;

import com.alkemy.tdd.model.Usuario;
import java.util.List;

/**
 * Interfaz de servicio para operaciones de negocio relacionadas con Usuario.
 * <p>
 * Esta interfaz define las operaciones de alto nivel para gestionar usuarios,
 * implementando la lógica de negocio y actuando como intermediario entre
 * la capa de presentación y la capa de acceso a datos.
 * </p>
 * 
 * <p>
 * El servicio se encarga de:
 * - Validaciones de negocio
 * - Transformación de datos
 * - Coordinación entre diferentes DAOs
 * - Manejo de transacciones
 * </p>
 * 
 * @author Roberto Rivas
 * @version 1.0.0
 * @since 2025-07-14
 */
public interface UsuarioService {
    
    /**
     * Crea un nuevo usuario en el sistema.
     * <p>
     * Este método valida los datos del usuario y lo persiste en la base de datos.
     * Incluye validaciones de negocio como verificar que el email no esté duplicado.
     * </p>
     * 
     * @param usuario El usuario a crear. No puede ser null.
     * @return El usuario creado con su ID asignado
     * @throws RuntimeException si hay un error al crear el usuario
     * @throws IllegalArgumentException si los datos del usuario son inválidos
     */
    Usuario crearUsuario(Usuario usuario);
    
    /**
     * Obtiene un usuario por su identificador único.
     * 
     * @param id El ID del usuario a obtener. No puede ser null.
     * @return El usuario encontrado
     * @throws RuntimeException si no se encuentra el usuario o hay un error de acceso
     * @throws IllegalArgumentException si el ID es null
     */
    Usuario obtenerUsuario(Long id);
    
    /**
     * Actualiza un usuario existente en el sistema.
     * <p>
     * Valida que el usuario exista y que los nuevos datos sean válidos
     * antes de proceder con la actualización.
     * </p>
     * 
     * @param usuario El usuario con los datos actualizados. No puede ser null y debe tener ID.
     * @return El usuario actualizado
     * @throws RuntimeException si hay un error al actualizar el usuario
     * @throws IllegalArgumentException si los datos del usuario son inválidos
     */
    Usuario actualizarUsuario(Usuario usuario);
    
    /**
     * Elimina un usuario del sistema.
     * 
     * @param id El ID del usuario a eliminar. No puede ser null.
     * @throws RuntimeException si hay un error al eliminar el usuario o si no existe
     * @throws IllegalArgumentException si el ID es null
     */
    void eliminarUsuario(Long id);
    
    /**
     * Obtiene todos los usuarios del sistema.
     * 
     * @return Lista de todos los usuarios. Puede estar vacía si no hay usuarios.
     * @throws RuntimeException si hay un error al obtener los usuarios
     */
    List<Usuario> listarUsuarios();
}
