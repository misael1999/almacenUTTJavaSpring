package com.flamel.almacenutt.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.flamel.almacenutt.models.entity.PrivilegioUsuario;
import com.flamel.almacenutt.models.entity.Proveedor;
import com.flamel.almacenutt.models.entity.Usuario;
import com.flamel.almacenutt.models.service.ProveedorService;
import com.flamel.almacenutt.models.service.UsuarioService;
import com.flamel.almacenutt.util.CustomErrorType;
import com.flamel.almacenutt.util.CustomResponseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/v1")
public class ProveedorController {

    @Autowired
    private ProveedorService proveedorService;

    @Autowired
    private UsuarioService usuarioService;


    // LISTA DE PROVEEDORES
    @RequestMapping(value = "/proveedores", method = RequestMethod.GET)
    public ResponseEntity<?> getProveedores() {

        return new ResponseEntity<>(new CustomResponseType("Lista de proveedores",
                "proveedores",
                proveedorService.findAllProveedores(), "").getResponse(),
                HttpStatus.OK);
    }

    // LISTA DE PROVEEDORES PAGINADO

    @RequestMapping(value = "/proveedores/page/{page}", method = RequestMethod.GET)
    public ResponseEntity<?> getProveedores(@PathVariable("page") Integer page) {

        return new ResponseEntity<>(new CustomResponseType("Lista de proveedores",
                "proveedores",
                proveedorService.findAllProveedores(PageRequest.of(page, 15)), "").getResponse(),
                HttpStatus.OK);
    }



    // AGREGAR UN NUEVO PROVEEDOR
    @RequestMapping(value = "/proveedores", method = RequestMethod.POST)
    public ResponseEntity<?> createProveedor(@RequestBody Proveedor proveedor, Authentication authentication) {

        Usuario usuario = usuarioService.findByNombreUsuario(authentication.getName());
        boolean privilegio = false;
        for (PrivilegioUsuario privilegioUsuario: usuario.getPrivilegios()) {
            if (privilegioUsuario.getPrivilegio().getNombre().equals("agregar proveedores")) {
                privilegio = true;
                break;
            }
        }
        if (!privilegio) {
            return new ResponseEntity<>(new CustomErrorType("No tienes permisos para esta accion", "No has ingresado ni un producto").getResponse(), HttpStatus.CONFLICT);
        }


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


        proveedor.setIdUsuario(usuario.getIdUsuario());
        proveedorService.saveProveedor(proveedor);

        return new ResponseEntity<>(new CustomResponseType("Se ha guardado el proveedor " + proveedor.getNombre(),
                "",
                "",
                "Proveedor guardado").getResponse(), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/proveedores/todo/{nombre}", method = RequestMethod.GET)
    public ResponseEntity<?> getProveedorByNombreTypehead(@PathVariable("nombre") String nombre) {

        if (nombre == null | nombre.isEmpty()) {
            return new ResponseEntity<>(new CustomErrorType("Debe de ingresar el nombre", "El nombre es obligatorio").getResponse(), HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(
                proveedorService.findProveedorByNombreTypehead(nombre), HttpStatus.OK);
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

    // ACTUALIZAR Y ELIMINAR PROVEEDOR (CAMBIO DE STATUS)

    @RequestMapping(value = "/proveedores", method = RequestMethod.PATCH)
    public ResponseEntity<?> updateProveedor(@RequestBody Proveedor proveedor, Authentication authentication) {

        if (proveedor.getIdProveedor() == null || proveedor.getIdProveedor() == 0) {
            return new ResponseEntity<>(new CustomErrorType("El id del proveeodor tiene que ser obligatorio",
                    "El id es obligatorio").getResponse(),
                    HttpStatus.CONFLICT);
        }

        Usuario usuario = usuarioService.findByNombreUsuario(authentication.getName());
        if (usuario.getRole().equals("ROLE_USER")) {
            return new ResponseEntity<>(new CustomErrorType("No tienes permisos para esta accion",
                    "Accion denegada").getResponse(),
                    HttpStatus.CONFLICT);
        }
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
