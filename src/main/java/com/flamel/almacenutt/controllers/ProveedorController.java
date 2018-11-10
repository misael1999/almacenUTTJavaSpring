package com.flamel.almacenutt.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.flamel.almacenutt.models.entity.Proveedor;
import com.flamel.almacenutt.models.service.ProveedorService;
import com.flamel.almacenutt.util.CustomErrorType;
import com.flamel.almacenutt.util.CustomResponseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/v1")
public class ProveedorController {

    @Autowired
    private ProveedorService proveedorService;


    // LISTA DE PROVEEDORES
    @RequestMapping(value = "/proveedores", method = RequestMethod.GET)
    public ResponseEntity<?> getProveedores() {

        return new ResponseEntity<>(new CustomResponseType("Lista de proveedores",
                "proveedores",
                proveedorService.listAllProveedores(), "").getResponse(),
                HttpStatus.OK);
    }


    // AGREGAR UN NUEVO PROVEEDOR
    @RequestMapping(value = "/proveedores", method = RequestMethod.POST)
    public ResponseEntity<?> createProveedor(@RequestBody Proveedor proveedor) {
        if (proveedor.getNombre() == null || proveedor.getNombre().isEmpty()) {
            return new ResponseEntity<>
                    (new CustomErrorType("Nombre obligatorio",
                            "El nombre tiene que ser obligatorio").getResponse(),
                            HttpStatus.CONFLICT);
        }
        if (proveedor.getIdUsuario() == null || proveedor.getIdUsuario() == 0) {
            return new ResponseEntity<>(new CustomErrorType("Id usuario es obligatorio",
                    "El id usuario tiene que ser obligatorio").getResponse(),
                    HttpStatus.CONFLICT);
        }

        proveedorService.saveProveedor(proveedor);

        return new ResponseEntity<>(new CustomResponseType("Se ha guardado el proveedor " + proveedor.getNombre(),
                "",
                "",
                "Proveedor guardado").getResponse(), HttpStatus.CREATED);
    }



    // BUSCAR PROVEEDOR POR NOMBRE
    @RequestMapping(value = "/proveedores/{nombre}", method = RequestMethod.GET)
    public ResponseEntity<?> getProveedorByNombre(@PathVariable("nombre") String nombre) {

        Proveedor proveedor = proveedorService.getProveedorByNombre(nombre);

        if (proveedor == null) {
            return new ResponseEntity<>
                    (new CustomErrorType("Proveedor no encontrado",
                            "No existe el proveedor con el nombre " + nombre).getResponse(),
                            HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(new CustomResponseType("Proveedor encontrado",
                "proveedor",
                proveedor,"Se encontro al proveedor " + nombre).getResponse(),
                HttpStatus.OK);
    }

}
