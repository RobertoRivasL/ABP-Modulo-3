package com.alkemy.tdd.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UsuarioExceptionTest {
    
    @Test
    void deberiaCrearExcepcionConMensaje() {
        // Given
        String mensaje = "Error de prueba";
        
        // When
        UsuarioException exception = new UsuarioException(mensaje);
        
        // Then
        assertEquals(mensaje, exception.getMessage());
        assertNull(exception.getCause());
    }
    
    @Test
    void deberiaCrearExcepcionConMensajeYCausa() {
        // Given
        String mensaje = "Error de prueba";
        Throwable causa = new RuntimeException("Causa original");
        
        // When
        UsuarioException exception = new UsuarioException(mensaje, causa);
        
        // Then
        assertEquals(mensaje, exception.getMessage());
        assertEquals(causa, exception.getCause());
    }
}