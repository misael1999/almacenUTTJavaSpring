package com.flamel.almacenutt.models.service;

import com.flamel.almacenutt.models.entity.Usuario;

import java.util.List;

public interface UsuarioService {

    void saveUsuario(Usuario usuario);
     List<Usuario> usuarioList();
     Usuario findByNombreUsuario(String nombreUsuario);
     Usuario findUsuarioByid(Long idUsuario);

}
