package com.flamel.almacenutt.models.service;

import com.flamel.almacenutt.models.dao.UsuarioDao;
import com.flamel.almacenutt.models.entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    UsuarioDao usuarioDao;


    @Override
    public void saveUsuario(Usuario usuario) {
        usuarioDao.save(usuario);
    }

    @Override
    public List<Usuario> usuarioList() {
        return usuarioDao.findAll();
    }

    @Override
    public Usuario findByNombreUsuario(String nombreUsuario) {
        return usuarioDao.findByNombreUsuario(nombreUsuario);
    }

    @Override
    public Usuario findUsuarioByid(Long idUsuario) {
        return usuarioDao.findById(idUsuario).orElse(null);
    }


}
