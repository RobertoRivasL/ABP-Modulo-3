package com.alkemy.tdd.service;

import com.alkemy.tdd.dao.UsuarioDAO;
import com.alkemy.tdd.exception.UsuarioException;
import com.alkemy.tdd.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Clase de pruebas unitarias para UsuarioService.
 * <p>
 * Esta clase contiene pruebas exhaustivas para verificar el comportamiento
 * de la lógica de negocio implementada en UsuarioService, utilizando
 * mocks para aislar las dependencias.
 * </p>
 * 
 * <p>
 * Las pruebas cubren:
 * - Casos de éxito para todas las operaciones CRUD
 * - Validaciones de datos de entrada
 * - Manejo de errores y excepciones
 * - Reglas de negocio específicas
 * </p>
 * 
 * @author Roberto Rivas
 * @version 1.0.0
 * @since 2025-07-14
 */
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    /**
     * Mock del DAO para aislar las pruebas del servicio.
     */
    @Mock
    private UsuarioDAO usuarioDAO;
    
    /**
     * Instancia del servicio bajo prueba.
     */
    private UsuarioService usuarioService;
    
    /**
     * Configuración inicial para cada prueba.
     * <p>
     * Se ejecuta antes de cada método de prueba para inicializar
     * el estado necesario.
     * </p>
     */
    @BeforeEach
    void setUp() {
        usuarioService = new UsuarioServiceImpl(usuarioDAO);
    }
    
    // ===== TESTS PARA CREAR USUARIO =====
    
    @Test
    void deberiaCrearUsuarioValidoCorrectamente() {
        // Given
        Usuario usuario = new Usuario("Juan Pérez", "juan.perez@email.com");
        Usuario usuarioGuardado = new Usuario(1L, "Juan Pérez", "juan.perez@email.com");
        
        when(usuarioDAO.existePorEmail("juan.perez@email.com")).thenReturn(false);
        when(usuarioDAO.guardar(any(Usuario.class))).thenReturn(usuarioGuardado);
        
        // When
        Usuario resultado = usuarioService.crearUsuario(usuario);
        
        // Then
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Juan Pérez", resultado.getNombre());
        assertEquals("juan.perez@email.com", resultado.getEmail());
        verify(usuarioDAO).guardar(usuario);
    }
    
    @Test
    void deberiaLanzarExcepcionCuandoUsuarioEsNull() {
        // When & Then
        UsuarioException exception = assertThrows(UsuarioException.class,
            () -> usuarioService.crearUsuario(null));
        
        assertEquals("El usuario no puede ser null", exception.getMessage());
        verify(usuarioDAO, never()).guardar(any());
    }
    
    @Test
    void deberiaLanzarExcepcionCuandoNombreEsNull() {
        // Given
        Usuario usuario = new Usuario(null, "juan.perez@email.com");
        
        // When & Then
        UsuarioException exception = assertThrows(UsuarioException.class,
            () -> usuarioService.crearUsuario(usuario));
        
        assertEquals("El nombre es obligatorio", exception.getMessage());
        verify(usuarioDAO, never()).guardar(any());
    }
    
    @Test
    void deberiaLanzarExcepcionCuandoNombreEsVacio() {
        // Given
        Usuario usuario = new Usuario("", "juan.perez@email.com");
        
        // When & Then
        UsuarioException exception = assertThrows(UsuarioException.class,
            () -> usuarioService.crearUsuario(usuario));
        
        assertEquals("El nombre es obligatorio", exception.getMessage());
        verify(usuarioDAO, never()).guardar(any());
    }
    
    @Test
    void deberiaLanzarExcepcionCuandoEmailEsNull() {
        // Given
        Usuario usuario = new Usuario("Juan Pérez", null);
        
        // When & Then
        UsuarioException exception = assertThrows(UsuarioException.class,
            () -> usuarioService.crearUsuario(usuario));
        
        assertEquals("El email es obligatorio", exception.getMessage());
        verify(usuarioDAO, never()).guardar(any());
    }
    
    @Test
    void deberiaLanzarExcepcionCuandoEmailEsInvalido() {
        // Given
        Usuario usuario = new Usuario("Juan Pérez", "email-invalido");
        
        // When & Then
        UsuarioException exception = assertThrows(UsuarioException.class,
            () -> usuarioService.crearUsuario(usuario));
        
        assertEquals("El formato del email no es válido", exception.getMessage());
        verify(usuarioDAO, never()).guardar(any());
    }
    
    @Test
    void deberiaLanzarExcepcionCuandoEmailYaExiste() {
        // Given
        Usuario usuario = new Usuario("Juan Pérez", "juan.perez@email.com");
        
        when(usuarioDAO.existePorEmail("juan.perez@email.com")).thenReturn(true);
        
        // When & Then
        UsuarioException exception = assertThrows(UsuarioException.class,
            () -> usuarioService.crearUsuario(usuario));
        
        assertEquals("Ya existe un usuario con este email", exception.getMessage());
        verify(usuarioDAO, never()).guardar(any());
    }
    
    // ===== TESTS PARA OBTENER USUARIO =====
    
    @Test
    void deberiaObtenerUsuarioPorId() {
        // Given
        Long userId = 1L;
        Usuario usuario = new Usuario(userId, "Juan Pérez", "juan.perez@email.com");
        
        when(usuarioDAO.buscarPorId(userId)).thenReturn(Optional.of(usuario));
        
        // When
        Usuario resultado = usuarioService.obtenerUsuario(userId);
        
        // Then
        assertNotNull(resultado);
        assertEquals(userId, resultado.getId());
        assertEquals("Juan Pérez", resultado.getNombre());
        verify(usuarioDAO).buscarPorId(userId);
    }
    
    @Test
    void deberiaLanzarExcepcionCuandoUsuarioNoExiste() {
        // Given
        Long userId = 999L;
        
        when(usuarioDAO.buscarPorId(userId)).thenReturn(Optional.empty());
        
        // When & Then
        UsuarioException exception = assertThrows(UsuarioException.class,
            () -> usuarioService.obtenerUsuario(userId));
        
        assertEquals("Usuario no encontrado", exception.getMessage());
        verify(usuarioDAO).buscarPorId(userId);
    }
    
    @Test
    void deberiaLanzarExcepcionCuandoIdEsNull() {
        // When & Then
        UsuarioException exception = assertThrows(UsuarioException.class,
            () -> usuarioService.obtenerUsuario(null));
        
        assertEquals("El ID no puede ser null", exception.getMessage());
        verify(usuarioDAO, never()).buscarPorId(any());
    }
    
    // ===== TESTS PARA LISTAR USUARIOS =====
    
    @Test
    void deberiaListarTodosLosUsuarios() {
        // Given
        List<Usuario> usuarios = Arrays.asList(
            new Usuario(1L, "Usuario1", "user1@email.com"),
            new Usuario(2L, "Usuario2", "user2@email.com")
        );
        
        when(usuarioDAO.buscarTodos()).thenReturn(usuarios);
        
        // When
        List<Usuario> resultado = usuarioService.listarUsuarios();
        
        // Then
        assertEquals(2, resultado.size());
        assertEquals("Usuario1", resultado.get(0).getNombre());
        assertEquals("Usuario2", resultado.get(1).getNombre());
        verify(usuarioDAO).buscarTodos();
    }
    
    @Test
    void deberiaRetornarListaVaciaCuandoNoHayUsuarios() {
        // Given
        when(usuarioDAO.buscarTodos()).thenReturn(Arrays.asList());
        
        // When
        List<Usuario> resultado = usuarioService.listarUsuarios();
        
        // Then
        assertTrue(resultado.isEmpty());
        verify(usuarioDAO).buscarTodos();
    }
    
    // ===== TESTS PARA ELIMINAR USUARIO =====
    
    @Test
    void deberiaEliminarUsuarioExistente() {
        // Given
        Long userId = 1L;
        Usuario usuario = new Usuario(userId, "Juan Pérez", "juan.perez@email.com");
        
        when(usuarioDAO.buscarPorId(userId)).thenReturn(Optional.of(usuario));
        doNothing().when(usuarioDAO).eliminar(userId);
        
        // When
        usuarioService.eliminarUsuario(userId);
        
        // Then
        verify(usuarioDAO).buscarPorId(userId);
        verify(usuarioDAO).eliminar(userId);
    }
    
    @Test
    void deberiaLanzarExcepcionAlEliminarUsuarioInexistente() {
        // Given
        Long userId = 999L;
        
        when(usuarioDAO.buscarPorId(userId)).thenReturn(Optional.empty());
        
        // When & Then
        UsuarioException exception = assertThrows(UsuarioException.class,
            () -> usuarioService.eliminarUsuario(userId));
        
        assertEquals("Usuario no encontrado", exception.getMessage());
        verify(usuarioDAO).buscarPorId(userId);
        verify(usuarioDAO, never()).eliminar(anyLong());
    }

    @Test
void deberiaActualizarUsuarioExistente() {
    // Given
    Usuario usuarioExistente = new Usuario(1L, "Original", "original@email.com");
    Usuario usuarioActualizado = new Usuario(1L, "Actualizado", "actualizado@test.com");
    
    when(usuarioDAO.buscarPorId(1L)).thenReturn(Optional.of(usuarioExistente));
    when(usuarioDAO.buscarTodos()).thenReturn(Arrays.asList(usuarioExistente));
    when(usuarioDAO.actualizar(any(Usuario.class))).thenReturn(usuarioActualizado);
    
    // When
    Usuario resultado = usuarioService.actualizarUsuario(usuarioActualizado);
    
    // Then
    assertEquals("Actualizado", resultado.getNombre());
    assertEquals("actualizado@test.com", resultado.getEmail());
    verify(usuarioDAO).buscarPorId(1L);
    verify(usuarioDAO).buscarTodos();
    verify(usuarioDAO).actualizar(usuarioActualizado);
}

@Test
void deberiaLanzarExcepcionAlActualizarSinId() {
    // Given
    Usuario usuario = new Usuario("Sin ID", "sin.id@email.com");
    
    // When & Then
    UsuarioException exception = assertThrows(UsuarioException.class,
        () -> usuarioService.actualizarUsuario(usuario));
    
    assertEquals("El ID del usuario es obligatorio para actualizar", exception.getMessage());
    verify(usuarioDAO, never()).actualizar(any());
}

@Test
void deberiaLanzarExcepcionAlActualizarConEmailDuplicado() {
    // Given
    Usuario usuarioExistente = new Usuario(1L, "Original", "original@email.com");
    Usuario otroUsuario = new Usuario(2L, "Otro", "duplicado@email.com");
    Usuario usuarioParaActualizar = new Usuario(1L, "Actualizado", "duplicado@email.com");
    
    when(usuarioDAO.buscarPorId(1L)).thenReturn(Optional.of(usuarioExistente));
    when(usuarioDAO.buscarTodos()).thenReturn(Arrays.asList(usuarioExistente, otroUsuario));
    
    // When & Then
    UsuarioException exception = assertThrows(UsuarioException.class,
        () -> usuarioService.actualizarUsuario(usuarioParaActualizar));
    
    assertEquals("Ya existe un usuario con este email", exception.getMessage());
    verify(usuarioDAO, never()).actualizar(any());
}
}
