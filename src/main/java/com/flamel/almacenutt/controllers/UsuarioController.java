package com.flamel.almacenutt.controllers;


import com.flamel.almacenutt.models.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/v1")
public class UsuarioController {

    @Autowired
    UsuarioService usuarioService;

    @RequestMapping(value = "/usuarios", method = RequestMethod.GET)
    public ResponseEntity<?> getUsuarios() {
        Map<String, Object> response = new HashMap<>();
        response.put("usuarios", usuarioService.usuarioList());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }



}


