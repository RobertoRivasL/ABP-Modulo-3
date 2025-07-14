package com.alkemy.tdd.service;

import com.alkemy.tdd.dao.UsuarioDAO;
import com.alkemy.tdd.exception.UsuarioException;
import com.alkemy.tdd.model.Usuario;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class UsuarioServiceImpl implements UsuarioService {
    
    private static final String ERROR_USUARIO_NULL = "El usuario no puede ser null";
    private static final String ERROR_NOMBRE_OBLIGATORIO = "El nombre es obligatorio";
    private static final String ERROR_EMAIL_OBLIGATORIO = "El email es obligatorio";
    private static final String ERROR_EMAIL_FORMATO = "El formato del email no es válido";
    private static final String ERROR_EMAIL_DUPLICADO = "Ya existe un usuario con este email";
    private static final String ERROR_USUARIO_NO_ENCONTRADO = "Usuario no encontrado";
    
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    
    private final UsuarioDAO usuarioDAO;
    
    public UsuarioServiceImpl(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }
    
    @Override
    public Usuario crearUsuario(Usuario usuario) {
        validarUsuario(usuario);
        validarEmailUnico(usuario.getEmail());
        
        return usuarioDAO.guardar(usuario);
    }
    
    @Override
    public Usuario obtenerUsuario(Long id) {
        if (id == null) {
            throw new UsuarioException("El ID no puede ser null");
        }
        
        Optional<Usuario> usuario = usuarioDAO.buscarPorId(id);
        return usuario.orElseThrow(() -> new UsuarioException(ERROR_USUARIO_NO_ENCONTRADO));
    }
    
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
    
    @Override
    public void eliminarUsuario(Long id) {
        // Verificar que el usuario existe antes de eliminar
        obtenerUsuario(id);
        usuarioDAO.eliminar(id);
    }
    
    @Override
    public List<Usuario> listarUsuarios() {
        return usuarioDAO.buscarTodos();
    }
    
    private void validarUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new UsuarioException(ERROR_USUARIO_NULL);
        }
        
        validarNombre(usuario.getNombre());
        validarEmail(usuario.getEmail());
    }
    
    private void validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new UsuarioException(ERROR_NOMBRE_OBLIGATORIO);
        }
    }
    
    private void validarEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new UsuarioException(ERROR_EMAIL_OBLIGATORIO);
        }
        
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new UsuarioException(ERROR_EMAIL_FORMATO);
        }
    }
    
    private void validarEmailUnico(String email) {
        if (usuarioDAO.existePorEmail(email)) {
            throw new UsuarioException(ERROR_EMAIL_DUPLICADO);
        }
    }
}
