package com.alkemy.tdd.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de pruebas unitarias para {@link DatabaseConfig}.
 * 
 * <p>Esta clase verifica el comportamiento correcto de la configuración 
 * de la base de datos en diferentes entornos y operaciones relacionadas
 * con la gestión de conexiones SQLite.</p>
 * 
 * <p>Características de las pruebas:</p>
 * <ul>
 * <li>Verifica la configuración de diferentes entornos (Development, Test, In-Memory)</li>
 * <li>Prueba la inicialización y limpieza de la base de datos</li>
 * <li>Valida la disponibilidad de conexiones</li>
 * <li>Asegura el correcto cierre de recursos</li>
 * </ul>
 * 
 * <p>Setup y Teardown:</p>
 * <ul>
 * <li>Guarda el entorno original antes de cada prueba</li>
 * <li>Restaura el entorno original después de cada prueba</li>
 * <li>Mantiene aislamiento entre pruebas</li>
 * </ul>
 * 
 * @author Roberto Rivas
 * @version 1.0.0
 * @since 2025-07-14
 * @see DatabaseConfig
 */
class DatabaseConfigTest {
    
    /**
     * Entorno de base de datos original antes de ejecutar cada prueba.
     * Se utiliza para restaurar el estado después de cada test.
     */
    private DatabaseConfig.DatabaseEnvironment originalEnvironment;
    
    /**
     * Configuración inicial antes de cada prueba.
     * Guarda el entorno actual para restaurarlo posteriormente.
     */
    @BeforeEach
    void setUp() {
        originalEnvironment = DatabaseConfig.getCurrentEnvironment();
    }
    
    /**
     * Limpieza después de cada prueba.
     * Restaura el entorno original para mantener aislamiento entre pruebas.
     */
    @AfterEach
    void tearDown() {
        DatabaseConfig.configurarEntorno(originalEnvironment);
    }
    
    /**
     * Verifica que se pueda configurar correctamente el entorno de desarrollo.
     * 
     * <p>Valida que:</p>
     * <ul>
     * <li>La URL de la base de datos sea la correcta para desarrollo</li>
     * <li>El entorno actual se actualice apropiadamente</li>
     * </ul>
     */
    @Test
    void deberiaConfigurarEntornoDevelopment() {
        // When
        DatabaseConfig.configurarEntorno(DatabaseConfig.DatabaseEnvironment.DEVELOPMENT);
        
        // Then
        assertEquals("jdbc:sqlite:database.db", DatabaseConfig.getDbUrl());
        assertEquals(DatabaseConfig.DatabaseEnvironment.DEVELOPMENT, 
                    DatabaseConfig.getCurrentEnvironment());
    }
    
    /**
     * Verifica que se pueda configurar correctamente el entorno de pruebas.
     * 
     * <p>Valida que:</p>
     * <ul>
     * <li>La URL de la base de datos sea la correcta para pruebas</li>
     * <li>El entorno actual se actualice apropiadamente</li>
     * </ul>
     */
    @Test
    void deberiaConfigurarEntornoTest() {
        // When
        DatabaseConfig.configurarEntorno(DatabaseConfig.DatabaseEnvironment.TEST);
        
        // Then
        assertEquals("jdbc:sqlite:test.db", DatabaseConfig.getDbUrl());
        assertEquals(DatabaseConfig.DatabaseEnvironment.TEST, 
                    DatabaseConfig.getCurrentEnvironment());
    }
    
    /**
     * Verifica que se pueda configurar correctamente el entorno en memoria.
     * 
     * <p>Valida que:</p>
     * <ul>
     * <li>La URL de la base de datos sea la correcta para base de datos en memoria</li>
     * <li>El entorno actual se actualice apropiadamente</li>
     * </ul>
     */
    @Test
    void deberiaConfigurarEntornoInMemory() {
        // When
        DatabaseConfig.configurarEntorno(DatabaseConfig.DatabaseEnvironment.IN_MEMORY);
        
        // Then
        assertEquals("jdbc:sqlite::memory:", DatabaseConfig.getDbUrl());
        assertEquals(DatabaseConfig.DatabaseEnvironment.IN_MEMORY, 
                    DatabaseConfig.getCurrentEnvironment());
    }
    
    /**
     * Verifica que la inicialización de la base de datos no lance excepciones.
     * 
     * <p>Utiliza una base de datos en memoria para evitar efectos secundarios
     * en el sistema de archivos durante las pruebas.</p>
     */
    @Test
    void deberiaInicializarBaseDatos() {
        // Given
        DatabaseConfig.configurarEntorno(DatabaseConfig.DatabaseEnvironment.IN_MEMORY);
        
        // When & Then (no debería lanzar excepción)
        assertDoesNotThrow(() -> DatabaseConfig.inicializarBaseDatos());
    }
    
    /**
     * Verifica que la limpieza de la base de datos no lance excepciones.
     * 
     * <p>Se asegura de que la base de datos esté inicializada antes de
     * intentar limpiarla para simular un escenario realista.</p>
     */
    @Test
    void deberiaLimpiarBaseDatos() {
        // Given
        DatabaseConfig.configurarEntorno(DatabaseConfig.DatabaseEnvironment.IN_MEMORY);
        DatabaseConfig.inicializarBaseDatos();
        
        // When & Then (no debería lanzar excepción)
        assertDoesNotThrow(() -> DatabaseConfig.limpiarBaseDatos());
    }
    
    /**
     * Verifica que se pueda determinar si una conexión está disponible.
     * 
     * <p>Utiliza una base de datos en memoria inicializada para garantizar
     * que la conexión esté realmente disponible durante la prueba.</p>
     */
    @Test
    void deberiaVerificarConexionDisponible() {
        // Given
        DatabaseConfig.configurarEntorno(DatabaseConfig.DatabaseEnvironment.IN_MEMORY);
        DatabaseConfig.inicializarBaseDatos();
        
        // When
        boolean disponible = DatabaseConfig.isConnectionAvailable();
        
        // Then
        assertTrue(disponible);
    }
    
    /**
     * Verifica que el cierre de recursos no lance excepciones.
     * 
     * <p>Esta prueba asegura que el método de limpieza de recursos
     * sea robusto y no falle incluso si no hay recursos activos.</p>
     */
    @Test
    void deberiaCerrarRecursos() {
        // When & Then (no debería lanzar excepción)
        assertDoesNotThrow(() -> DatabaseConfig.cerrarRecursos());
    }
}