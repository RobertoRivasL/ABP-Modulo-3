package com.alkemy.tdd.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

/**
 * Clase de configuración centralizada para manejo de base de datos
 * Proporciona métodos para configurar diferentes entornos de base de datos
 */
public class DatabaseConfig {
    
    private static final Logger logger = Logger.getLogger(DatabaseConfig.class.getName());
    
    // Configuración por defecto
    private static final String DEFAULT_DB_URL = "jdbc:sqlite:database.db";
    private static final String TEST_DB_URL = "jdbc:sqlite::memory:";
    
    // Estado actual de la configuración
    private static DatabaseEnvironment currentEnvironment = DatabaseEnvironment.DEVELOPMENT;
    
    /**
     * Enumeración para los diferentes entornos de base de datos
     */
    public enum DatabaseEnvironment {
        DEVELOPMENT("jdbc:sqlite:database.db"),
        TEST("jdbc:sqlite:test.db"),
        IN_MEMORY("jdbc:sqlite::memory:");
        
        private final String url;
        
        DatabaseEnvironment(String url) {
            this.url = url;
        }
        
        public String getUrl() {
            return url;
        }
    }
    
    /**
     * Configura el entorno de base de datos
     * @param environment el entorno a configurar
     */
    public static void configurarEntorno(DatabaseEnvironment environment) {
        currentEnvironment = environment;
        logger.info("Configurando entorno de base de datos: " + environment.name());
    }
    
    /**
     * Obtiene la URL de la base de datos para el entorno actual
     * @return la URL de la base de datos
     */
    public static String getDbUrl() {
        return currentEnvironment.getUrl();
    }
    
    /**
     * Obtiene una conexión a la base de datos
     * @return conexión a la base de datos
     * @throws SQLException si hay error al conectar
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(getDbUrl());
    }
    
    /**
     * Inicializa la base de datos creando las tablas necesarias
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
     * Limpia todas las tablas de la base de datos
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
     * Verifica si una tabla existe en la base de datos
     * @param connection conexión a la base de datos
     * @param tableName nombre de la tabla
     * @return true si la tabla existe
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
     * Cierra y limpia recursos de la base de datos
     */
    public static void cerrarRecursos() {
        // Para SQLite no hay recursos adicionales que cerrar
        // pero mantenemos el método para consistencia
        logger.info("Recursos de base de datos cerrados");
    }
    
    /**
     * Obtiene el entorno actual de la base de datos
     * @return el entorno actual
     */
    public static DatabaseEnvironment getCurrentEnvironment() {
        return currentEnvironment;
    }
    
    /**
     * Método de utilidad para verificar si hay conexión a la base de datos
     * @return true si hay conexión disponible
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
