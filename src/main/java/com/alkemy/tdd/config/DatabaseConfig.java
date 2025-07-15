package com.alkemy.tdd.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

/**
 * Clase de configuración centralizada para manejo de base de datos.
 * <p>
 * Esta clase proporciona métodos para configurar diferentes entornos de base de datos,
 * gestionar conexiones, inicializar esquemas y realizar operaciones de mantenimiento.
 * </p>
 * 
 * <p>
 * Características principales:
 * - Soporte para múltiples entornos (desarrollo, pruebas, producción)
 * - Inicialización automática de esquemas
 * - Métodos utilitarios para gestión de base de datos
 * - Logging integrado para monitoreo
 * </p>
 * 
 * @author Roberto Rivas
 * @version 1.0.0
 * @since 2025-07-14
 */
public class DatabaseConfig {
    
    /**
     * Logger para la clase DatabaseConfig.
     */
    private static final Logger logger = Logger.getLogger(DatabaseConfig.class.getName());
    
    /**
     * URL de conexión por defecto para desarrollo.
     */
    private static final String DEFAULT_DB_URL = "jdbc:sqlite:database.db";
    
    /**
     * URL de conexión para pruebas en memoria.
     */
    private static final String TEST_DB_URL = "jdbc:sqlite::memory:";
    
    /**
     * Estado actual del entorno de la configuración.
     */
    private static DatabaseEnvironment currentEnvironment = DatabaseEnvironment.DEVELOPMENT;
    
    /**
     * Enumeración para los diferentes entornos de base de datos.
     * <p>
     * Define los diferentes entornos disponibles y sus respectivas
     * configuraciones de conexión.
     * </p>
     */
    public enum DatabaseEnvironment {
        /**
         * Entorno de desarrollo con base de datos persistente.
         */
        DEVELOPMENT("jdbc:sqlite:database.db"),
        /**
         * Entorno de pruebas con base de datos en archivo separado.
         */
        TEST("jdbc:sqlite:test.db"),
        /**
         * Entorno de pruebas en memoria, sin persistencia.
         */
        IN_MEMORY("jdbc:sqlite::memory:");
        
        /**
         * URL de conexión del entorno.
         */
        private final String url;
        
        /**
         * Constructor del enum.
         * 
         * @param url La URL de conexión para este entorno
         */
        DatabaseEnvironment(String url) {
            this.url = url;
        }
        
        /**
         * Obtiene la URL de conexión del entorno.
         * 
         * @return La URL de conexión
         */
        public String getUrl() {
            return url;
        }
    }
    
    /**
     * Configura el entorno de base de datos activo.
     * <p>
     * Permite cambiar entre diferentes entornos de base de datos
     * durante la ejecución de la aplicación.
     * </p>
     * 
     * @param environment El entorno a configurar. No puede ser null.
     */
    public static void configurarEntorno(DatabaseEnvironment environment) {
        currentEnvironment = environment;
        logger.info("Configurando entorno de base de datos: " + environment.name());
    }
    
    /**
     * Obtiene la URL de la base de datos para el entorno actual.
     * 
     * @return La URL de la base de datos configurada
     */
    public static String getDbUrl() {
        return currentEnvironment.getUrl();
    }
    
    /**
     * Obtiene una conexión a la base de datos.
     * <p>
     * Este método crea una nueva conexión usando la configuración
     * del entorno actual.
     * </p>
     * 
     * @return Conexión a la base de datos
     * @throws SQLException si hay error al conectar a la base de datos
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(getDbUrl());
    }
    
    /**
     * Inicializa la base de datos creando las tablas necesarias.
     * <p>
     * Este método crea la estructura de la base de datos si no existe,
     * incluyendo la tabla de usuarios con sus respectivas columnas.
     * </p>
     * 
     * @throws RuntimeException si hay error al inicializar la base de datos
     */
    public static void inicializarBaseDatos() {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            
            // Crear tabla usuarios si no existe
            String createTableSQL = "CREATE TABLE IF NOT EXISTS usuarios (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre VARCHAR(100) NOT NULL," +
                "email VARCHAR(100) NOT NULL UNIQUE," +
                "fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ")";
            
            statement.execute(createTableSQL);
            logger.info("Tabla usuarios creada o ya existe");
            
        } catch (SQLException e) {
            logger.severe("Error al inicializar base de datos: " + e.getMessage());
            throw new RuntimeException("Error al inicializar base de datos", e);
        }
    }
    
    /**
     * Limpia todas las tablas de la base de datos.
     * <p>
     * Este método elimina todos los datos de las tablas existentes
     * sin eliminar la estructura de la base de datos.
     * </p>
     * 
     * @throws RuntimeException si hay error al limpiar la base de datos
     */
    public static void limpiarBaseDatos() {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            
            // Verificar si la tabla existe antes de limpiar
            if (tablaExiste(connection, "usuarios")) {
                statement.execute("DELETE FROM usuarios");
                logger.info("Base de datos limpiada");
            } else {
                logger.info("Tabla usuarios no existe, no se necesita limpiar");
            }
            
        } catch (SQLException e) {
            logger.severe("Error al limpiar base de datos: " + e.getMessage());
            throw new RuntimeException("Error al limpiar base de datos", e);
        }
    }
    
    /**
     * Verifica si una tabla existe en la base de datos.
     * <p>
     * Este método consulta el catálogo de SQLite para determinar
     * si una tabla específica existe.
     * </p>
     * 
     * @param connection Conexión a la base de datos. No puede ser null.
     * @param tableName Nombre de la tabla a verificar. No puede ser null.
     * @return true si la tabla existe, false en caso contrario
     */
    private static boolean tablaExiste(Connection connection, String tableName) {
        try {
            String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name=?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, tableName);
                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            logger.warning("Error verificando si tabla existe: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Cierra y limpia recursos de la base de datos.
     * <p>
     * Para SQLite no hay recursos adicionales que cerrar,
     * pero se mantiene el método para consistencia y futura extensibilidad.
     * </p>
     */
    public static void cerrarRecursos() {
        // Para SQLite no hay recursos adicionales que cerrar
        // pero mantenemos el método para consistencia
        logger.info("Recursos de base de datos cerrados");
    }
    
    /**
     * Obtiene el entorno actual de la base de datos.
     * 
     * @return El entorno actual configurado
     */
    public static DatabaseEnvironment getCurrentEnvironment() {
        return currentEnvironment;
    }
    
    /**
     * Método de utilidad para verificar si hay conexión disponible a la base de datos.
     * <p>
     * Este método intenta establecer una conexión y verifica que esté disponible
     * y no esté cerrada.
     * </p>
     * 
     * @return true si hay conexión disponible, false en caso contrario
     */
    public static boolean isConnectionAvailable() {
        try (Connection connection = getConnection()) {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            logger.warning("No hay conexión disponible: " + e.getMessage());
            return false;
        }
    }
}
