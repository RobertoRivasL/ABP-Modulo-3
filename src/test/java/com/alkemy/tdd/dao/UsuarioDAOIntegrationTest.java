package com.alkemy.tdd.dao;

import com.alkemy.tdd.config.DatabaseConfig;
import com.alkemy.tdd.dao.UsuarioDAO;
import com.alkemy.tdd.dao.UsuarioDAOImpl;
import com.alkemy.tdd.model.Usuario;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioDAOIntegrationTest {
    
    private UsuarioDAO usuarioDAO;
    
    @BeforeAll
    static void configurarEntorno() {
        // Configurar base de datos de pruebas con archivo temporal
        DatabaseConfig.configurarEntorno(DatabaseConfig.DatabaseEnvironment.TEST);
    }
    
    @BeforeEach
    void setUp() {
        // Inicializar y limpiar base de datos antes de cada prueba
        DatabaseConfig.inicializarBaseDatos();
        usuarioDAO = new UsuarioDAOImpl(DatabaseConfig.getDbUrl());
        DatabaseConfig.limpiarBaseDatos();
    }
    
    @Test
    void deberiaGuardarUsuarioEnBaseDatos() {
        // Given
        Usuario usuario = new Usuario("Juan Pérez", "juan.test@email.com");
        
        // When
        Usuario usuarioGuardado = usuarioDAO.guardar(usuario);
        
        // Then
        assertNotNull(usuarioGuardado.getId());
        assertEquals("Juan Pérez", usuarioGuardado.getNombre());
        assertEquals("juan.test@email.com", usuarioGuardado.getEmail());
        assertNotNull(usuarioGuardado.getFechaCreacion());
    }
    
    @Test
    void deberiaBuscarUsuarioPorId() {
        // Given
        Usuario usuario = new Usuario("María González", "maria.test@email.com");
        Usuario usuarioGuardado = usuarioDAO.guardar(usuario);
        
        // When
        Optional<Usuario> resultado = usuarioDAO.buscarPorId(usuarioGuardado.getId());
        
        // Then
        assertTrue(resultado.isPresent());
        assertEquals("María González", resultado.get().getNombre());
        assertEquals("maria.test@email.com", resultado.get().getEmail());
    }
    
    @Test
    void deberiaRetornarVacioSiUsuarioNoExiste() {
        // When
        Optional<Usuario> resultado = usuarioDAO.buscarPorId(999L);
        
        // Then
        assertFalse(resultado.isPresent());
    }
    
    @Test
    void deberiaListarTodosLosUsuarios() {
        // Given
        usuarioDAO.guardar(new Usuario("Usuario1", "user1@test.com"));
        usuarioDAO.guardar(new Usuario("Usuario2", "user2@test.com"));
        usuarioDAO.guardar(new Usuario("Usuario3", "user3@test.com"));
        
        // When
        List<Usuario> usuarios = usuarioDAO.buscarTodos();
        
        // Then
        assertEquals(3, usuarios.size());
        assertTrue(usuarios.stream().anyMatch(u -> u.getNombre().equals("Usuario1")));
        assertTrue(usuarios.stream().anyMatch(u -> u.getNombre().equals("Usuario2")));
        assertTrue(usuarios.stream().anyMatch(u -> u.getNombre().equals("Usuario3")));
    }
    
    @Test
    void deberiaVerificarSiEmailExiste() {
        // Given
        usuarioDAO.guardar(new Usuario("Test User", "test@email.com"));
        
        // When & Then
        assertTrue(usuarioDAO.existePorEmail("test@email.com"));
        assertFalse(usuarioDAO.existePorEmail("noexiste@email.com"));
    }
    
    @Test
    void deberiaActualizarUsuario() {
        // Given
        Usuario usuario = usuarioDAO.guardar(new Usuario("Original", "original@test.com"));
        usuario.setNombre("Actualizado");
        usuario.setEmail("actualizado@test.com");
        
        // When
        Usuario usuarioActualizado = usuarioDAO.actualizar(usuario);
        
        // Then
        assertEquals("Actualizado", usuarioActualizado.getNombre());
        assertEquals("actualizado@test.com", usuarioActualizado.getEmail());
        
        // Verificar en BD
        Optional<Usuario> verificacion = usuarioDAO.buscarPorId(usuario.getId());
        assertTrue(verificacion.isPresent());
        assertEquals("Actualizado", verificacion.get().getNombre());
    }
    
    @Test
    void deberiaEliminarUsuario() {
        // Given
        Usuario usuario = usuarioDAO.guardar(new Usuario("Para Eliminar", "eliminar@test.com"));
        
        // When
        usuarioDAO.eliminar(usuario.getId());
        
        // Then
        Optional<Usuario> resultado = usuarioDAO.buscarPorId(usuario.getId());
        assertFalse(resultado.isPresent());
    }
}
