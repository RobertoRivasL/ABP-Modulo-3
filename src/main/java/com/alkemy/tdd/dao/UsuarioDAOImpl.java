package com.alkemy.tdd.dao;

import com.alkemy.tdd.model.Usuario;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación concreta de la interfaz UsuarioDAO para base de datos SQLite.
 * <p>
 * Esta clase proporciona operaciones CRUD (Create, Read, Update, Delete) 
 * para la entidad Usuario utilizando JDBC y SQLite como base de datos.
 * </p>
 * 
 * <p>
 * Características principales:
 * - Manejo de conexiones JDBC
 * - Operaciones SQL preparadas para prevenir inyección SQL
 * - Manejo robusto de excepciones
 * - Mapeo automático de ResultSet a objetos Usuario
 * - Soporte para fechas LocalDateTime
 * </p>
 * 
 * <p>
 * Esta implementación es thread-safe a nivel de método, pero no mantiene
 * estado entre llamadas. Cada operación abre y cierra su propia conexión.
 * </p>
 * 
 * @author Roberto Rivas
 * @version 1.0.0
 * @since 2025-07-14
 */
public class UsuarioDAOImpl implements UsuarioDAO {
    
    /**
     * URL de conexión a la base de datos SQLite.
     */
    private final String databaseUrl;
    
    /**
     * Constructor que recibe la URL de la base de datos.
     * 
     * @param databaseUrl URL de conexión a la base de datos SQLite
     * @throws IllegalArgumentException si la URL es null o vacía
     */
    public UsuarioDAOImpl(String databaseUrl) {
        if (databaseUrl == null || databaseUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("La URL de la base de datos no puede ser null o vacía");
        }
        this.databaseUrl = databaseUrl;
    }
    
    /**
     * Obtiene una conexión a la base de datos.
     * <p>
     * Utiliza DriverManager para crear una nueva conexión cada vez.
     * Es responsabilidad del llamador cerrar la conexión.
     * </p>
     * 
     * @return Connection objeto de conexión a la base de datos
     * @throws SQLException si hay error al conectar a la base de datos
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(databaseUrl);
    }
    
    /**
     * {@inheritDoc}
     * <p>
     * Implementación específica para SQLite que:
     * - Utiliza una sentencia INSERT preparada
     * - Retorna las claves generadas automáticamente
     * - Asigna el ID generado al objeto usuario
     * </p>
     * 
     * @param usuario El usuario a guardar
     * @return El usuario con el ID asignado
     * @throws RuntimeException si ocurre un error durante la inserción
     */
    @Override
    public Usuario guardar(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser null");
        }
        
        String sql = "INSERT INTO usuarios (nombre, email, fecha_creacion) VALUES (?, ?, ?)";
        
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.setString(1, usuario.getNombre());
            statement.setString(2, usuario.getEmail());
            statement.setObject(3, usuario.getFechaCreacion());
            
            int affectedRows = statement.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("No se pudo guardar el usuario, no se afectaron filas");
            }
            
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    usuario.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("No se pudo obtener el ID del usuario guardado");
                }
            }
            
            return usuario;
            
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar usuario: " + e.getMessage(), e);
        }
    }
    
    /**
     * Busca un usuario por su identificador único.
     * 
     * <p>Este método ejecuta una consulta SQL para buscar un usuario específico
     * en la base de datos SQLite utilizando su ID como criterio de búsqueda.</p>
     * 
     * <p>Implementación específica para SQLite:</p>
     * <ul>
     * <li>Utiliza PreparedStatement para evitar SQL injection</li>
     * <li>Maneja conexiones de forma segura con try-with-resources</li>
     * <li>Mapea el ResultSet a un objeto Usuario si existe</li>
     * <li>Retorna Optional.empty() si no se encuentra el usuario</li>
     * </ul>
     * 
     * @param id El identificador único del usuario a buscar. No puede ser null.
     * @return Optional conteniendo el usuario si existe, Optional.empty() si no existe
     * @throws RuntimeException Si ocurre un error de acceso a la base de datos
     */
    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        String sql = "SELECT id, nombre, email, fecha_creacion FROM usuarios WHERE id = ?";
        
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, id);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapearUsuario(resultSet));
                }
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar usuario por ID: " + e.getMessage(), e);
        }
        
        return Optional.empty();
    }
    
    /**
     * Recupera todos los usuarios de la base de datos.
     * 
     * <p>Este método ejecuta una consulta SQL para obtener todos los usuarios
     * almacenados en la base de datos, ordenados por fecha de creación descendente.</p>
     * 
     * <p>Implementación específica para SQLite:</p>
     * <ul>
     * <li>Utiliza ORDER BY fecha_creacion DESC para obtener usuarios más recientes primero</li>
     * <li>Maneja conexiones de forma segura con try-with-resources</li>
     * <li>Mapea cada fila del ResultSet a un objeto Usuario</li>
     * <li>Retorna una lista vacía si no hay usuarios</li>
     * </ul>
     * 
     * @return Lista de todos los usuarios en la base de datos, ordenados por fecha de creación descendente
     * @throws RuntimeException Si ocurre un error de acceso a la base de datos
     */
    @Override
    public List<Usuario> buscarTodos() {
        String sql = "SELECT id, nombre, email, fecha_creacion FROM usuarios ORDER BY fecha_creacion DESC";
        List<Usuario> usuarios = new ArrayList<>();
        
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                usuarios.add(mapearUsuario(resultSet));
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar todos los usuarios: " + e.getMessage(), e);
        }
        
        return usuarios;
    }
    
    /**
     * Verifica si existe un usuario con el email especificado.
     * 
     * <p>Este método ejecuta una consulta SQL para contar cuántos usuarios
     * existen en la base de datos con el email proporcionado.</p>
     * 
     * <p>Implementación específica para SQLite:</p>
     * <ul>
     * <li>Utiliza COUNT(*) para optimizar la consulta</li>
     * <li>Utiliza PreparedStatement para evitar SQL injection</li>
     * <li>Maneja conexiones de forma segura con try-with-resources</li>
     * <li>Retorna true si el conteo es mayor a 0</li>
     * </ul>
     * 
     * @param email El email a verificar. No puede ser null ni vacío.
     * @return true si existe un usuario con ese email, false en caso contrario
     * @throws RuntimeException Si ocurre un error de acceso a la base de datos
     */
    @Override
    public boolean existePorEmail(String email) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE email = ?";
        
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, email);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar email: " + e.getMessage(), e);
        }
        
        return false;
    }
    
    /**
     * Actualiza un usuario existente en la base de datos.
     * 
     * <p>Este método ejecuta una consulta SQL UPDATE para modificar los datos
     * de un usuario existente en la base de datos.</p>
     * 
     * <p>Implementación específica para SQLite:</p>
     * <ul>
     * <li>Actualiza solo los campos nombre y email</li>
     * <li>Mantiene el ID y fecha_creacion sin cambios</li>
     * <li>Utiliza PreparedStatement para evitar SQL injection</li>
     * <li>Maneja conexiones de forma segura con try-with-resources</li>
     * <li>Verifica que se haya actualizado exactamente un registro</li>
     * <li>Lanza excepción si no se encuentra el usuario</li>
     * </ul>
     * 
     * @param usuario El usuario con los datos actualizados. Debe tener un ID válido.
     * @return El mismo usuario actualizado
     * @throws RuntimeException Si no se encuentra el usuario o si ocurre un error de base de datos
     */
    @Override
    public Usuario actualizar(Usuario usuario) {
        String sql = "UPDATE usuarios SET nombre = ?, email = ? WHERE id = ?";
        
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, usuario.getNombre());
            statement.setString(2, usuario.getEmail());
            statement.setLong(3, usuario.getId());
            
            int affectedRows = statement.executeUpdate();
            
            if (affectedRows == 0) {
                throw new RuntimeException("No se encontró usuario con ID: " + usuario.getId());
            }
            
            return usuario;
            
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar usuario: " + e.getMessage(), e);
        }
    }
    
    /**
     * Elimina un usuario de la base de datos por su ID.
     * 
     * <p>Este método ejecuta una consulta SQL DELETE para eliminar un usuario
     * específico de la base de datos utilizando su ID como criterio.</p>
     * 
     * <p>Implementación específica para SQLite:</p>
     * <ul>
     * <li>Utiliza PreparedStatement para evitar SQL injection</li>
     * <li>Maneja conexiones de forma segura con try-with-resources</li>
     * <li>Verifica que se haya eliminado exactamente un registro</li>
     * <li>Lanza excepción si no se encuentra el usuario</li>
     * </ul>
     * 
     * @param id El ID del usuario a eliminar. No puede ser null.
     * @throws RuntimeException Si no se encuentra el usuario o si ocurre un error de base de datos
     */
    @Override
    public void eliminar(Long id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, id);
            
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new RuntimeException("No se encontró usuario con ID: " + id);
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar usuario: " + e.getMessage(), e);
        }
    }
    
    /**
     * Mapea un ResultSet a un objeto Usuario.
     * 
     * <p>Este método privado utilitario convierte una fila del ResultSet
     * en un objeto Usuario completamente inicializado.</p>
     * 
     * <p>Características específicas para SQLite:</p>
     * <ul>
     * <li>Maneja diferentes tipos de datos para fecha_creacion</li>
     * <li>Convierte String, Timestamp y LocalDateTime apropiadamente</li>
     * <li>Extrae todos los campos: id, nombre, email, fecha_creacion</li>
     * <li>Crea un nuevo objeto Usuario para cada fila</li>
     * </ul>
     * 
     * @param resultSet ResultSet con los datos del usuario de la consulta SQL
     * @return Usuario objeto Usuario completamente inicializado con los datos del ResultSet
     * @throws SQLException si hay error al leer los datos del ResultSet
     */
    private Usuario mapearUsuario(ResultSet resultSet) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(resultSet.getLong("id"));
        usuario.setNombre(resultSet.getString("nombre"));
        usuario.setEmail(resultSet.getString("email"));
        
        // Manejo de fecha - SQLite puede retornar diferentes tipos
        Object fechaObj = resultSet.getObject("fecha_creacion");
        if (fechaObj instanceof String) {
            usuario.setFechaCreacion(LocalDateTime.parse((String) fechaObj));
        } else if (fechaObj instanceof Timestamp) {
            usuario.setFechaCreacion(((Timestamp) fechaObj).toLocalDateTime());
        } else if (fechaObj instanceof LocalDateTime) {
            usuario.setFechaCreacion((LocalDateTime) fechaObj);
        }
        
        return usuario;
    }
}
