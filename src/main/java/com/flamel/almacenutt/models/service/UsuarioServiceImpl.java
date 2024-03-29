package com.flamel.almacenutt.models.service;

import com.flamel.almacenutt.models.dao.AreaDao;
import com.flamel.almacenutt.models.dao.PrivilegioDao;
import com.flamel.almacenutt.models.dao.UsuarioDao;
import com.flamel.almacenutt.models.entity.Area;
import com.flamel.almacenutt.models.entity.Privilegio;
import com.flamel.almacenutt.models.entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    UsuarioDao usuarioDao;
    @Autowired
    AreaDao areaDao;

    @Autowired
    PrivilegioDao privilegioDao;

    @Override
    public List<Usuario> findAllUsuarios() {
        return usuarioDao.findAllUsuarios();
    }

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
        return usuarioDao.findByNombreUsuarioStatus(nombreUsuario);
    }

    @Override
    public Usuario findUsuarioByid(Long idUsuario) {
        return usuarioDao.findById(idUsuario).orElse(null);
    }

    @Override
    public List<Area> findAllAreas() {
        return areaDao.listAllArea();
    }

    @Override
    public void saveArea(Area area) {
        areaDao.save(area);
    }

    @Override
    public Area findAreaByid(Long idArea) {
        return areaDao.findById(idArea).orElse(null);
    }

    @Override
    public Area getAreaNombre(String nombre) {
        return areaDao.getAreaNombre(nombre);
    }

    @Override
    public List<Privilegio> getPrivilegios() {
        return privilegioDao.findAll();
    }


}
