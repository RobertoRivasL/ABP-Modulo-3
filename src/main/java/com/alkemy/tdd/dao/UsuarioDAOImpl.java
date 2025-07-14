package com.alkemy.tdd.dao;

import com.alkemy.tdd.model.Usuario;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación de UsuarioDAO para SQLite
 * Maneja operaciones CRUD para la entidad Usuario
 */
public class UsuarioDAOImpl implements UsuarioDAO {
    
    private final String databaseUrl;
    
    /**
     * Constructor que recibe la URL de la base de datos
     * @param databaseUrl URL de conexión a la base de datos
     */
    public UsuarioDAOImpl(String databaseUrl) {
        this.databaseUrl = databaseUrl;
    }
    
    /**
     * Obtiene una conexión a la base de datos
     * @return Connection objeto de conexión
     * @throws SQLException si hay error al conectar
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(databaseUrl);
    }
    
    @Override
    public Usuario guardar(Usuario usuario) {
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
     * Mapea un ResultSet a un objeto Usuario
     * @param resultSet ResultSet con los datos del usuario
     * @return Usuario objeto Usuario mapeado
     * @throws SQLException si hay error al leer los datos
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
