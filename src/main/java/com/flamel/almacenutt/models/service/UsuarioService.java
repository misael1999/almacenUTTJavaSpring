package com.flamel.almacenutt.models.service;

import com.flamel.almacenutt.models.entity.Area;
import com.flamel.almacenutt.models.entity.Usuario;

import java.util.List;

public interface UsuarioService {

    List<Usuario> findAllUsuarios();
    void saveUsuario(Usuario usuario);
    List<Usuario> usuarioList();
    Usuario findByNombreUsuario(String nombreUsuario);
    Usuario findUsuarioByid(Long idUsuario);

    List<Area> findAllAreas();
    void saveArea();
    Area findAreaByid(Long idArea);
    Area findAreaByNombre(String nombre);

}
