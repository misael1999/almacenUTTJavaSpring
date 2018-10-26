package com.flamel.almacenutt.models.service;

import com.flamel.almacenutt.models.entity.Usuario;

import java.util.List;

public interface UsuarioService {

    public List<Usuario> usuarioList();
    public Usuario findByNombreUsuario(String nombreUsuario);

}
