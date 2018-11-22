package com.flamel.almacenutt.controllers;


import com.flamel.almacenutt.models.entity.Usuario;
import com.flamel.almacenutt.models.service.UsuarioService;
import com.flamel.almacenutt.util.ChangePassword;
import com.flamel.almacenutt.util.CustomErrorType;
import com.flamel.almacenutt.util.CustomResponseType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/v1")
public class UsuarioController {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @RequestMapping(value = "/usuarios", method = RequestMethod.GET)
    public ResponseEntity<?> getUsuarios() {
        Map<String, Object> response = new HashMap<>();
        response.put("usuarios", usuarioService.usuarioList());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/usuarios/password", method = RequestMethod.PATCH)
    public ResponseEntity<?> cambiarPassword(@RequestBody ChangePassword changePassword, Authentication authentication) {

        Usuario usuario = usuarioService.findByNombreUsuario(authentication.getName());

        if (usuario == null) {
            return new ResponseEntity<>(new CustomErrorType("No existe el usuario", "El usuario no existe").getResponse(), HttpStatus.CONFLICT);
        }

        if (!passwordEncoder.matches(changePassword.getPasswordAnterior(), usuario.getPassword())) {
            return new ResponseEntity<>(new CustomErrorType("La contraseña anterior no es correcta", "La contraseña anterior no es correcta").getResponse(), HttpStatus.CONFLICT);
        }

        usuario.setPassword(passwordEncoder.encode(changePassword.getPasswordNueva()));
        usuarioService.saveUsuario(usuario);

        return new ResponseEntity<>(new CustomResponseType("Se ha cambiado la contraseña", "", "", "Se cambio la contraseña").getResponse(), HttpStatus.OK);

    }

    // AREAS
    @RequestMapping(value = "/areas", method = RequestMethod.GET)
    public ResponseEntity<?> getAreas() {
        return new ResponseEntity<>(new CustomResponseType("Lista de areas", "areas", usuarioService.findAllAreas(), "Areas encontradas").getResponse(), HttpStatus.OK);
    }





}


