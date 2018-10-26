package com.flamel.almacenutt.models.service;

import com.flamel.almacenutt.models.dao.UsuarioDao;
import com.flamel.almacenutt.models.entity.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("jpaUserDetailsService")
public class JpaUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioDao usuarioDao;

    private Logger logger = LoggerFactory.getLogger(JpaUserDetailsService.class);

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String nombreUsuaio) throws UsernameNotFoundException {

        Usuario usuario = usuarioDao.findByNombreUsuario(nombreUsuaio);
        if (usuario == null) {
            logger.error("Error no existe el usuario");
            throw new UsernameNotFoundException("Usuario no existe");
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(usuario.getRol()));
        if (authorities.isEmpty()) {
            logger.error("Error en el login el usuario no tiene rol asignado");
            throw new UsernameNotFoundException("Usuario no existe");
        }

        System.out.println(usuario.getPassword());

    return new User(usuario.getNombreUsuario(), usuario.getPassword(), usuario.getEstado(), true, true,true, authorities);
    }
}
