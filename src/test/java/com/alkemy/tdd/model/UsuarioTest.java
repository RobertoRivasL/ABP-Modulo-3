package com.alkemy.tdd.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {
    
    @Test
    void deberiaCompararUsuariosPorEmail() {
        // Given
        Usuario usuario1 = new Usuario("Juan", "juan@email.com");
        Usuario usuario2 = new Usuario("Pedro", "juan@email.com"); // Mismo email
        Usuario usuario3 = new Usuario("Juan", "otro@email.com");
        
        // When & Then
        assertEquals(usuario1, usuario2); // Mismo email = iguales
        assertNotEquals(usuario1, usuario3); // Email diferente
        assertNotEquals(usuario1, null);
        assertNotEquals(usuario1, "string");
        assertEquals(usuario1, usuario1); // Reflexivo
    }
    
    @Test
    void deberiaGenerarHashCodeConsistente() {
        // Given
        Usuario usuario1 = new Usuario("Juan", "juan@email.com");
        Usuario usuario2 = new Usuario("Pedro", "juan@email.com");
        Usuario usuario3 = new Usuario("Juan", "otro@email.com");
        
        // When & Then
        assertEquals(usuario1.hashCode(), usuario2.hashCode()); // Mismo email
        assertNotEquals(usuario1.hashCode(), usuario3.hashCode()); // Email diferente
    }
    
    @Test
    void deberiaGenerarToStringCorrectamente() {
        // Given
        Usuario usuario = new Usuario(1L, "Juan Pérez", "juan@email.com");
        
        // When
        String resultado = usuario.toString();
        
        // Then
        assertTrue(resultado.contains("Juan Pérez"));
        assertTrue(resultado.contains("juan@email.com"));
        assertTrue(resultado.contains("id=1"));
        assertTrue(resultado.contains("Usuario{"));
    }
    
    @Test
    void deberiaCrearUsuarioConConstructorCompleto() {
        // Given & When
        Usuario usuario = new Usuario(1L, "Test", "test@email.com");
        
        // Then
        assertEquals(1L, usuario.getId());
        assertEquals("Test", usuario.getNombre());
        assertEquals("test@email.com", usuario.getEmail());
        assertNotNull(usuario.getFechaCreacion());
    }
    
    @Test
    void deberiaCrearUsuarioVacio() {
        // Given & When
        Usuario usuario = new Usuario();
        
        // Then
        assertNull(usuario.getId());
        assertNull(usuario.getNombre());
        assertNull(usuario.getEmail());
        assertNotNull(usuario.getFechaCreacion());
    }
}