package com.flamel.almacenutt.models.dao;

import com.flamel.almacenutt.models.entity.Area;
import com.flamel.almacenutt.models.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioDao extends JpaRepository<Usuario, Long> {
    Usuario findByNombreUsuario(String nombreUsuario);

    @Query("select u from Usuario u order by u.idUsuario desc")
    List<Usuario> findAllUsuarios();
}

