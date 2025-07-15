package com.alkemy.tdd.exception;

/**
 * Excepción específica para errores relacionados con operaciones de Usuario.
 * <p>
 * Esta excepción se lanza cuando ocurren errores específicos del dominio
 * de usuarios, como violaciones de reglas de negocio, datos inválidos,
 * o problemas de integridad de datos.
 * </p>
 * 
 * <p>
 * Al ser una RuntimeException, no requiere ser declarada en las firmas
 * de métodos, lo que simplifica el código cliente.
 * </p>
 * 
 * @author Roberto Rivas
 * @version 1.0.0
 * @since 2025-07-14
 */
public class UsuarioException extends RuntimeException {
    
    /**
     * Construye una nueva excepción con el mensaje especificado.
     * 
     * @param message El mensaje de error que describe la excepción
     */
    public UsuarioException(String message) {
        super(message);
    }
    
    /**
     * Construye una nueva excepción con el mensaje especificado y la causa.
     * 
     * @param message El mensaje de error que describe la excepción
     * @param cause La causa de la excepción (puede ser null)
     */
    public UsuarioException(String message, Throwable cause) {
        super(message, cause);
    }
}
