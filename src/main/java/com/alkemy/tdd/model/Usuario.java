package com.alkemy.tdd.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Clase modelo que representa un Usuario en el sistema.
 * <p>
 * Esta clase encapsula la información básica de un usuario, incluyendo
 * su identificador único, nombre, email y fecha de creación.
 * </p>
 * 
 * <p>
 * La clase implementa equals() y hashCode() basados en el ID del usuario,
 * y toString() para una representación legible del objeto.
 * </p>
 * 
 * <p>
 * Invariantes de la clase:
 * - El email debe ser único en el sistema
 * - La fecha de creación se asigna automáticamente
 * - El ID puede ser null para usuarios nuevos (será asignado por la base de datos)
 * </p>
 * 
 * @author Roberto Rivas
 * @version 1.0.0
 * @since 2025-07-14
 */
public class Usuario {
    
    /**
     * Identificador único del usuario.
     * Puede ser null para usuarios nuevos antes de ser persistidos.
     */
    private Long id;
    
    /**
     * Nombre del usuario.
     * No puede ser null o vacío.
     */
    private String nombre;
    
    /**
     * Email del usuario.
     * Debe ser único en el sistema y no puede ser null o vacío.
     */
    private String email;
    
    /**
     * Fecha y hora de creación del usuario.
     * Se asigna automáticamente al crear la instancia.
     */
    private LocalDateTime fechaCreacion;
    
    /**
     * Constructor por defecto.
     * Inicializa la fecha de creación con la fecha y hora actuales.
     */
    public Usuario() {
        this.fechaCreacion = LocalDateTime.now();
    }
    
    /**
     * Constructor con nombre y email.
     * La fecha de creación se asigna automáticamente.
     * 
     * @param nombre El nombre del usuario
     * @param email El email del usuario
     */
    public Usuario(String nombre, String email) {
        this();
        this.nombre = nombre;
        this.email = email;
    }
    
    /**
     * Constructor completo con ID, nombre y email.
     * La fecha de creación se asigna automáticamente.
     * 
     * @param id El ID del usuario
     * @param nombre El nombre del usuario
     * @param email El email del usuario
     */
    public Usuario(Long id, String nombre, String email) {
        this(nombre, email);
        this.id = id;
    }
    
    // Getters y Setters
    
    /**
     * Obtiene el ID del usuario.
     * 
     * @return El ID del usuario, puede ser null para usuarios nuevos
     */
    public Long getId() { 
        return id; 
    }
    
    /**
     * Establece el ID del usuario.
     * 
     * @param id El ID del usuario
     */
    public void setId(Long id) { 
        this.id = id; 
    }
    
    /**
     * Obtiene el nombre del usuario.
     * 
     * @return El nombre del usuario
     */
    public String getNombre() { 
        return nombre; 
    }
    
    /**
     * Establece el nombre del usuario.
     * 
     * @param nombre El nombre del usuario
     */
    public void setNombre(String nombre) { 
        this.nombre = nombre; 
    }
    
    /**
     * Obtiene el email del usuario.
     * 
     * @return El email del usuario
     */
    public String getEmail() { 
        return email; 
    }
    
    /**
     * Establece el email del usuario.
     * 
     * @param email El email del usuario
     */
    public void setEmail(String email) { 
        this.email = email; 
    }
    
    /**
     * Obtiene la fecha de creación del usuario.
     * 
     * @return La fecha y hora de creación del usuario
     */
    public LocalDateTime getFechaCreacion() { 
        return fechaCreacion; 
    }
    
    /**
     * Establece la fecha de creación del usuario.
     * 
     * @param fechaCreacion La fecha y hora de creación del usuario
     */
    public void setFechaCreacion(LocalDateTime fechaCreacion) { 
        this.fechaCreacion = fechaCreacion; 
    }
    
    /**
     * Compara este usuario con otro objeto para determinar igualdad.
     * <p>
     * Dos usuarios son considerados iguales si tienen el mismo email,
     * ya que el email debe ser único en el sistema.
     * </p>
     * 
     * @param o El objeto a comparar
     * @return true si los objetos son iguales, false en caso contrario
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(email, usuario.email);
    }
    
    /**
     * Genera un código hash para este usuario basado en su email.
     * 
     * @return El código hash del usuario
     */
    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
    
    /**
     * Retorna una representación en String del usuario.
     * 
     * @return Una cadena que representa el usuario con todos sus campos
     */
    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", fechaCreacion=" + fechaCreacion +
                '}';
    }
}