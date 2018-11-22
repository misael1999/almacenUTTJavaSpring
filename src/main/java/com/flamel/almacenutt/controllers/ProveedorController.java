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
import org.springframework.web.bind.annotation.*;

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
                proveedorService.findAllProveedores(), "").getResponse(),
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

        if (proveedorService.getProveedorByNombre(proveedor.getNombre()) != null) {
            return new ResponseEntity<>(new CustomErrorType("El proveedor " + proveedor.getNombre() + " ya existe",
                    "Proveedor ya existe").getResponse(),
                    HttpStatus.CONFLICT);
        }

        proveedorService.saveProveedor(proveedor);

        return new ResponseEntity<>(new CustomResponseType("Se ha guardado el proveedor " + proveedor.getNombre(),
                "",
                "",
                "Proveedor guardado").getResponse(), HttpStatus.CREATED);
    }

    // BUSCAR TODOS LOS PROVEEDORES QUE COINCIDAN CON EL NOMBRE

    @RequestMapping(value = "/proveedores/{nombre}", method = RequestMethod.GET)
    public ResponseEntity<?> getProveedorLikeNombre(@PathVariable("nombre") String nombre) {

        if (nombre == null | nombre.isEmpty()) {
            return new ResponseEntity<>(new CustomErrorType("Debe de ingresar el nombre", "El nombre es obligatorio").getResponse(), HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(new CustomResponseType("Proveedores que coinciden con " + nombre,
                "proveedores",
                proveedorService.findAllProveedorLikeNombre(nombre),
                "Proveedores encontrados").getResponse(), HttpStatus.OK);
    }

    // ELIMINAR PROVEEDOR CAMBIO DE STATUS

    @RequestMapping(value = "/proveedores", method = RequestMethod.PATCH)
    public ResponseEntity<?> deleteProveedor(@RequestBody Proveedor proveedor) {

        if (proveedor.getIdProveedor() == null || proveedor.getIdProveedor() == 0) {
            return new ResponseEntity<>(new CustomErrorType("El id del proveeodor tiene que ser obligatorio",
                    "El id es obligatorio").getResponse(),
                    HttpStatus.CONFLICT);
        }

        System.out.println(proveedor.getNombre());

        proveedorService.saveProveedor(proveedor);
        if (!proveedor.getStatus()) {
            return new ResponseEntity<>(new CustomResponseType("Se ha eliminado el proveedor " + proveedor.getNombre(),
                    "", "",
                    "Proveedor eliminado").getResponse(), HttpStatus.OK);
        }

        return new ResponseEntity<>(new CustomResponseType("Se actualizo el proveedor " + proveedor.getNombre(),
                "", "",
                "Se actualizo el proveedor").getResponse(), HttpStatus.OK);
    }


}
