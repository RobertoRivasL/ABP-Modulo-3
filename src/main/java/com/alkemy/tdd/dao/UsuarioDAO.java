package com.alkemy.tdd.dao;

import com.alkemy.tdd.model.Usuario;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz para operaciones de acceso a datos de Usuario (Data Access Object).
 * <p>
 * Esta interfaz define las operaciones CRUD (Create, Read, Update, Delete) 
 * para la entidad Usuario. Utiliza el patrón DAO para abstraer y encapsular 
 * todo el acceso a la fuente de datos.
 * </p>
 * 
 * <p>
 * La implementación de esta interfaz debe manejar:
 * - Conexiones a la base de datos
 * - Transacciones
 * - Manejo de excepciones
 * - Validaciones de datos
 * </p>
 * 
 * @author Roberto Rivas
 * @version 1.0.0
 * @since 2025-07-14
 */
public interface UsuarioDAO {
    
    /**
     * Guarda un nuevo usuario en la base de datos.
     * <p>
     * Si el usuario no tiene ID, se genera uno automáticamente.
     * Si el usuario ya tiene ID, actualiza el registro existente.
     * </p>
     * 
     * @param usuario El objeto Usuario a guardar. No puede ser null.
     * @return El usuario guardado con su ID asignado
     * @throws RuntimeException si hay un error al guardar el usuario
     * @throws IllegalArgumentException si el usuario es null o tiene datos inválidos
     */
    Usuario guardar(Usuario usuario);
    
    /**
     * Busca un usuario por su identificador único.
     * 
     * @param id El ID del usuario a buscar. No puede ser null.
     * @return Un Optional que contiene el usuario si existe, o vacío si no se encuentra
     * @throws RuntimeException si hay un error al buscar el usuario
     * @throws IllegalArgumentException si el ID es null
     */
    Optional<Usuario> buscarPorId(Long id);
    
    /**
     * Obtiene todos los usuarios de la base de datos.
     * 
     * @return Lista de todos los usuarios. Puede estar vacía si no hay usuarios.
     * @throws RuntimeException si hay un error al obtener los usuarios
     */
    List<Usuario> buscarTodos();
    
    /**
     * Verifica si existe un usuario con el email especificado.
     * <p>
     * Útil para validar la unicidad del email antes de crear o actualizar usuarios.
     * </p>
     * 
     * @param email El email a verificar. No puede ser null o vacío.
     * @return true si existe un usuario con ese email, false en caso contrario
     * @throws RuntimeException si hay un error al verificar el email
     * @throws IllegalArgumentException si el email es null o vacío
     */
    boolean existePorEmail(String email);
    
    /**
     * Actualiza un usuario existente en la base de datos.
     * <p>
     * El usuario debe tener un ID válido que corresponda a un registro existente.
     * </p>
     * 
     * @param usuario El objeto Usuario con los datos actualizados. No puede ser null y debe tener ID.
     * @return El usuario actualizado
     * @throws RuntimeException si hay un error al actualizar el usuario
     * @throws IllegalArgumentException si el usuario es null o no tiene ID
     */
    Usuario actualizar(Usuario usuario);
    
    /**
     * Elimina un usuario de la base de datos.
     * 
     * @param id El ID del usuario a eliminar. No puede ser null.
     * @throws RuntimeException si hay un error al eliminar el usuario o si no existe
     * @throws IllegalArgumentException si el ID es null
     */
    void eliminar(Long id);
}