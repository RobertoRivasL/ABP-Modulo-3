package com.alkemy.tdd.dao;

import com.alkemy.tdd.model.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioDAO {
    Usuario guardar(Usuario usuario);
    Optional<Usuario> buscarPorId(Long id);
    List<Usuario> buscarTodos();
    boolean existePorEmail(String email);
    Usuario actualizar(Usuario usuario);
    void eliminar(Long id);
}