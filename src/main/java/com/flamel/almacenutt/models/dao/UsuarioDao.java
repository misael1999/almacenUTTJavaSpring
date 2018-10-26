package com.flamel.almacenutt.models.dao;

import com.flamel.almacenutt.models.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioDao extends JpaRepository<Usuario, Long> {

    public Usuario findByNombreUsuario(String nombreUsuario);

}
