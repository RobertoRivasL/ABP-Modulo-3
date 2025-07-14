package com.alkemy.tdd.service;

import com.alkemy.tdd.model.Usuario;
import java.util.List;

public interface UsuarioService {
    Usuario crearUsuario(Usuario usuario);
    Usuario obtenerUsuario(Long id);
    Usuario actualizarUsuario(Usuario usuario);
    void eliminarUsuario(Long id);
    List<Usuario> listarUsuarios();
}
