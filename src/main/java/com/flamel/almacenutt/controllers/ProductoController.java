package com.flamel.almacenutt.controllers;

import com.flamel.almacenutt.models.entity.Producto;
import com.flamel.almacenutt.models.entity.Proveedor;
import com.flamel.almacenutt.models.entity.Usuario;
import com.flamel.almacenutt.models.service.ProductoService;
import com.flamel.almacenutt.models.service.UsuarioService;
import com.flamel.almacenutt.util.CustomErrorType;
import com.flamel.almacenutt.util.CustomResponseType;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/v1")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private UsuarioService usuarioService;


    // Obtener todos los productos almacenados
    @RequestMapping(value = "/productos/page/{page}", method = RequestMethod.GET)
    public ResponseEntity<?> getProductos(@PathVariable("page") Integer page) {
        Page<Producto> productos = null;

         productos = productoService.findAllProductos(PageRequest.of(page, 15));
        return new ResponseEntity<>(new CustomResponseType("Lista de productos",
                "productos", productos, "").getResponse(),
                HttpStatus.OK);
    }


    // Obtener todos los productos que coincidan con su descripcion
    @RequestMapping(value = "/productos/todos/{descripcion}", method = RequestMethod.GET)
    public ResponseEntity<?> getProductosByDescripcionLike(@PathVariable("descripcion") String descripcion) {

        return new ResponseEntity<>(new CustomResponseType("Productos encontrados",
                "productos",
                productoService.findAllProductosByDescripcionLike(descripcion), "").getResponse(),
                HttpStatus.OK);
    }


    // AGREGAR NUEVO PRODUCTO
    @RequestMapping(value = "/productos", method = RequestMethod.POST)
    public ResponseEntity<?> createProducto(@RequestBody Producto producto, Authentication authentication) {
        if (producto.getDescripcion().isEmpty() || producto.getIdUsuario() == 0 || producto.getCantidad() == 0 || producto.getPrecio() == 0 || producto.getUnidadMedida().isEmpty()) {
            return new ResponseEntity<>(new CustomErrorType("Campos vacios", "No puede haver campos vacios").getResponse(), HttpStatus.CONFLICT);
        }

        Usuario usuario = usuarioService.findByNombreUsuario(authentication.getName());
        if (usuario.getRole().equals("ROLE_USER")) {
            return new ResponseEntity<>(new CustomErrorType("No tienes permisos para esta accion",
                    "Accion denegada").getResponse(),
                    HttpStatus.CONFLICT);
        }
        producto.setIdUsuario(usuario.getIdUsuario());
        try {
        productoService.saveProducto(producto);
        } catch (DataAccessException e) {
            return new ResponseEntity<>(new CustomErrorType("Ocurrio un error al guardar el producto, contacte con el administrador del sistema ",
                    "Error").getResponse(),
                    HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(new CustomResponseType("Se agreago el producto " + producto.getDescripcion(), "producto", "", "Producto agregado").getResponse(), HttpStatus.CREATED);
    }

    // ELIMINAR PROVEEDOR CAMBIO DE STATUS

    @RequestMapping(value = "/productos", method = RequestMethod.PATCH)
    public ResponseEntity<?> updateProducto(@RequestBody Producto producto, Authentication authentication) {

        if (producto.getClave() == null || producto.getClave().isEmpty()) {
            return new ResponseEntity<>(new CustomErrorType("El id del producto tiene que ser obligatorio",
                    "El id es obligatorio").getResponse(),
                    HttpStatus.CONFLICT);
        }

        Usuario usuario = usuarioService.findByNombreUsuario(authentication.getName());
        if (usuario.getRole().equals("ROLE_USER")) {
            return new ResponseEntity<>(new CustomErrorType("No tienes permisos para esta accion",
                    "Accion denegada").getResponse(),
                    HttpStatus.CONFLICT);
        }
        try {
            productoService.saveProducto(producto);
        } catch (DataAccessException e) {
            return new ResponseEntity<>(new CustomErrorType("Ocurrio un error al actualizar el producto, contacte con el administrador del sistema ",
                    "Error").getResponse(),
                    HttpStatus.CONFLICT);
        }
        if (!producto.getStatus()) {
            return new ResponseEntity<>(new CustomResponseType("Se ha eliminado el producto " + producto.getDescripcion(),
                    "", "",
                    "Producto eliminado").getResponse(), HttpStatus.OK);
        }

        return new ResponseEntity<>(new CustomResponseType("Se actualizo el producto " + producto.getDescripcion(),
                "", "",
                "Se actualizo el producto").getResponse(), HttpStatus.OK);
    }


}
