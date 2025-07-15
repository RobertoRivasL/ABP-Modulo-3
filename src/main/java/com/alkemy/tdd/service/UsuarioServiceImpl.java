package com.alkemy.tdd.service;

import com.alkemy.tdd.dao.UsuarioDAO;
import com.alkemy.tdd.exception.UsuarioException;
import com.alkemy.tdd.model.Usuario;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Implementación del servicio de Usuario.
 * <p>
 * Esta clase implementa la lógica de negocio para las operaciones relacionadas
 * con usuarios, incluyendo validaciones, transformaciones y coordinación
 * con la capa de acceso a datos.
 * </p>
 * 
 * <p>
 * Características principales:
 * - Validación de datos de entrada
 * - Verificación de reglas de negocio
 * - Manejo de excepciones específicas del dominio
 * - Interacción con UsuarioDAO para persistencia
 * </p>
 * 
 * @author Roberto Rivas
 * @version 1.0.0
 * @since 2025-07-14
 */
public class UsuarioServiceImpl implements UsuarioService {
    
    /**
     * Mensaje de error cuando el usuario es null.
     */
    private static final String ERROR_USUARIO_NULL = "El usuario no puede ser null";
    
    /**
     * Mensaje de error cuando el nombre es obligatorio.
     */
    private static final String ERROR_NOMBRE_OBLIGATORIO = "El nombre es obligatorio";
    
    /**
     * Mensaje de error cuando el email es obligatorio.
     */
    private static final String ERROR_EMAIL_OBLIGATORIO = "El email es obligatorio";
    
    /**
     * Mensaje de error cuando el formato del email no es válido.
     */
    private static final String ERROR_EMAIL_FORMATO = "El formato del email no es válido";
    
    /**
     * Mensaje de error cuando el email ya existe.
     */
    private static final String ERROR_EMAIL_DUPLICADO = "Ya existe un usuario con este email";
    
    /**
     * Mensaje de error cuando el usuario no se encuentra.
     */
    private static final String ERROR_USUARIO_NO_ENCONTRADO = "Usuario no encontrado";
    
    /**
     * Patrón regex para validar el formato del email.
     */
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    
    /**
     * DAO para acceso a datos de usuarios.
     */
    private final UsuarioDAO usuarioDAO;
    
    /**
     * Constructor que inyecta la dependencia del DAO.
     * 
     * @param usuarioDAO El DAO para acceso a datos de usuarios. No puede ser null.
     * @throws IllegalArgumentException si usuarioDAO es null
     */
    public UsuarioServiceImpl(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Usuario crearUsuario(Usuario usuario) {
        validarUsuario(usuario);
        validarEmailUnico(usuario.getEmail());
        
        return usuarioDAO.guardar(usuario);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Usuario obtenerUsuario(Long id) {
        if (id == null) {
            throw new UsuarioException("El ID no puede ser null");
        }
        
        Optional<Usuario> usuario = usuarioDAO.buscarPorId(id);
        return usuario.orElseThrow(() -> new UsuarioException(ERROR_USUARIO_NO_ENCONTRADO));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Usuario actualizarUsuario(Usuario usuario) {
        validarUsuario(usuario);
        
        if (usuario.getId() == null) {
            throw new UsuarioException("El ID del usuario es obligatorio para actualizar");
        }
        
        // Verificar que el usuario existe
        obtenerUsuario(usuario.getId());
        
        // Verificar que el email no esté siendo usado por otro usuario
        Optional<Usuario> usuarioConEmail = usuarioDAO.buscarTodos().stream()
            .filter(u -> u.getEmail().equals(usuario.getEmail()) && !u.getId().equals(usuario.getId()))
            .findFirst();
            
        if (usuarioConEmail.isPresent()) {
            throw new UsuarioException(ERROR_EMAIL_DUPLICADO);
        }
        
        return usuarioDAO.actualizar(usuario);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void eliminarUsuario(Long id) {
        // Verificar que el usuario existe antes de eliminar
        obtenerUsuario(id);
        usuarioDAO.eliminar(id);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Usuario> listarUsuarios() {
        return usuarioDAO.buscarTodos();
    }
    
    /**
     * Valida un usuario completo.
     * <p>
     * Este método realiza todas las validaciones necesarias sobre un objeto
     * Usuario, incluyendo nombre y email.
     * </p>
     * 
     * @param usuario El usuario a validar. No puede ser null.
     * @throws UsuarioException si el usuario es null o si alguna de sus
     * propiedades no cumple con las reglas de negocio
     */
    private void validarUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new UsuarioException(ERROR_USUARIO_NULL);
        }
        
        validarNombre(usuario.getNombre());
        validarEmail(usuario.getEmail());
    }
    
    /**
     * Valida el nombre de un usuario.
     * <p>
     * El nombre es considerado válido si no es null, no está vacío y no
     * contiene solo espacios en blanco.
     * </p>
     * 
     * @param nombre El nombre a validar.
     * @throws UsuarioException si el nombre es null, vacío o contiene solo
     * espacios en blanco
     */
    private void validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new UsuarioException(ERROR_NOMBRE_OBLIGATORIO);
        }
    }
    
    /**
     * Valida el email de un usuario.
     * <p>
     * El email es considerado válido si no es null, no está vacío, tiene un
     * formato correcto y es único en el sistema.
     * </p>
     * 
     * @param email El email a validar.
     * @throws UsuarioException si el email es null, vacío, tiene un formato
     * inválido o ya está siendo usado por otro usuario
     */
    private void validarEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new UsuarioException(ERROR_EMAIL_OBLIGATORIO);
        }
        
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new UsuarioException(ERROR_EMAIL_FORMATO);
        }
    }
    
    /**
     * Valida que un email sea único en el sistema.
     * <p>
     * Este método verifica que no exista otro usuario con el mismo email.
     * </p>
     * 
     * @param email El email a validar.
     * @throws UsuarioException si ya existe un usuario con el mismo email
     */
    private void validarEmailUnico(String email) {
        if (usuarioDAO.existePorEmail(email)) {
            throw new UsuarioException(ERROR_EMAIL_DUPLICADO);
        }
    }
}
