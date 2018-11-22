package com.flamel.almacenutt.controllers;

import com.flamel.almacenutt.models.entity.Producto;
import com.flamel.almacenutt.models.service.ProductoService;
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
public class ProductoController {

    @Autowired
    private ProductoService productoService;



    // Obtener todos los productos almacenados
    @RequestMapping(value = "/productos", method = RequestMethod.GET)
    public ResponseEntity<?> getProductos() {
        return new ResponseEntity<>(new CustomResponseType("Lista de productos",
                "productos",
                productoService.findAllProductos(), "").getResponse(),
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


    // Obtener el producto por su descripcion
    @RequestMapping(value = "/productos/{descripcion}", method = RequestMethod.GET)
    public ResponseEntity<?> getProductos(@PathVariable("descripcion") String descripcion) {

        return new ResponseEntity<>(new CustomResponseType("Producto encontrado",
                "producto",
                productoService.getProductoByDescripcion(descripcion), "").getResponse(),
                HttpStatus.OK);
    }

    @RequestMapping(value = "/productos", method = RequestMethod.POST)
    public ResponseEntity<?> createProducto(@RequestBody Producto producto) {
        if(producto.getDescripcion().isEmpty() || producto.getIdUsuario() == 0 || producto.getCantidad() == 0 || producto.getPrecio() == 0 || producto.getUnidad().isEmpty()) {
           return new ResponseEntity<>(new CustomErrorType("Campos vacios","No puede haver campos vacios").getResponse(), HttpStatus.CONFLICT);
        }
        productoService.saveProducto(producto);
        return new ResponseEntity<>(new CustomResponseType("Se agreago el producto " + producto.getDescripcion(), "producto", "", "Producto agregado").getResponse(),HttpStatus.CREATED);
    }
}
