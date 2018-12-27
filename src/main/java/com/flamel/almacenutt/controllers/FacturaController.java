package com.flamel.almacenutt.controllers;

import com.flamel.almacenutt.models.entity.*;
import com.flamel.almacenutt.models.service.FacturaService;
import com.flamel.almacenutt.models.service.ProductoService;
import com.flamel.almacenutt.models.service.ProveedorService;
import com.flamel.almacenutt.models.service.UsuarioService;
import com.flamel.almacenutt.util.CustomErrorType;
import com.flamel.almacenutt.util.CustomResponseType;
import org.apache.tomcat.util.http.fileupload.FileUploadBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/v1")
public class FacturaController {

    @Autowired
    private FacturaService facturaService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ProveedorService proveedorService;

    @RequestMapping(value = "/facturas/page/{page}", method = RequestMethod.GET)
    public ResponseEntity<?> getFacturas(@PathVariable("page") Integer page,
                                         @RequestParam(value = "entregadas", required = false) String entregadas,
                                         @RequestParam(value = "ordenar", required = true) String ordenar) {

        Sort sort = null;
        if (ordenar.equals("desc")) {
            sort = Sort.by("fechaExpedicion").descending();
        } else if (ordenar.equals("asc")) {
            sort = Sort.by("fechaExpedicion").ascending();
        }

        if (entregadas != null) {
            return new ResponseEntity<>(new CustomResponseType("Lista de facturas",
                    "facturas",
                    facturaService.listFacturasEntregadas(PageRequest.of(page, 15, sort)), "").getResponse(),
                    HttpStatus.OK);
        }

        return new ResponseEntity<>(new CustomResponseType("Lista de facturas",
                "facturas",
                facturaService.listFacturasActivas(PageRequest.of(page, 15, sort)), "").getResponse(),
                HttpStatus.OK);
    }

    // OBTENER FACTURA POR FOLIO
    @RequestMapping(value = "facturas/{folio}", method = RequestMethod.GET)
    public ResponseEntity<?> getFacturaByFolio(@PathVariable("folio") String folio) {
        Factura factura = facturaService.getFacturaByFolio(folio);
        return new ResponseEntity<>(new CustomResponseType("Factura encontrada",
                "factura", factura, "Factura encontrada").getResponse(), HttpStatus.OK);
    }

    // OBTENER FACTURA QUE CONCIDA POR PROVEEDOR O FOLIO
    @RequestMapping(value = "/facturas/todo/{termino}", method = RequestMethod.GET)
    public ResponseEntity<?> getFacturaLikeTermino(@PathVariable("termino") String termino) {

        return new ResponseEntity<>(new CustomResponseType("Facturas encontradas",
                "facturas", facturaService.findFacturaLikeTermino(termino),
                "").getResponse(), HttpStatus.OK);
    }


    // AGREGAR FACTURA

    @RequestMapping(value = "/facturas", method = RequestMethod.POST)
    public ResponseEntity<?> crearFactura(@RequestBody() Factura factura, Authentication authentication) {

        Proveedor proveedor = proveedorService.getProveedorByNombre(factura.getProveedor().getNombre());
        if (proveedor == null) {
            return new ResponseEntity<>(new CustomErrorType("El proveedor no existe", "No existe el proveedor").getResponse(), HttpStatus.CONFLICT);
        }

        factura.setProveedor(proveedor);

        Usuario usuario = usuarioService.findByNombreUsuario(authentication.getName());
        Boolean privilegio = false;
        for (PrivilegioUsuario privilegioUsuario : usuario.getPrivilegios()) {
            if (privilegioUsuario.getPrivilegio().getNombre().equals("agregar facturas")) {
                privilegio = true;
                break;
            }
        }
        if (!privilegio) {
            return new ResponseEntity<>(new CustomErrorType("No tienes permisos para esta accion", "No has ingresado ni un producto").getResponse(), HttpStatus.CONFLICT);
        }

        if (factura.getFolio() == null || factura.getFolio().isEmpty() || factura.getFechaExpedicion() == null || factura.getProveedor().getIdProveedor() == null) {
            return new ResponseEntity<>(new CustomErrorType("Ingresa los campos obligatorios", "No has ingresado los campos obligatorios").getResponse(), HttpStatus.CONFLICT);
        }
        Factura facturaExist = facturaService.getFacturaByFolio(factura.getFolio());

        if (facturaExist != null) {
            return new ResponseEntity<>(new CustomErrorType("La factura con el folio " + factura.getFolio() + " ya existe",
                    "La factura ya existe").getResponse(),
                    HttpStatus.CONFLICT);
        }

        if (factura.getItems().size() == 0) {
            return new ResponseEntity<>(new CustomErrorType("Ingresa al menos un producto", "No has ingresado ni un producto").getResponse(), HttpStatus.CONFLICT);
        }

        List<FacturaProducto> facturaItems = new ArrayList<>(factura.getItems());
        factura.getItems().clear();

        for (int i = 0; i < facturaItems.size(); i++) {
            Producto producto = facturaItems.get(i).getProducto();
            Producto productoAux = productoService.getProductoByClaveAndDescripcion(producto.getClave().toLowerCase(), producto.getDescripcion().toLowerCase());
            FacturaProducto facturaProducto = new FacturaProducto();
            // si el producto no existe
            if (productoAux == null) {
                producto.setIdUsuario(factura.getIdUsuario());
                producto.setCantidad(facturaItems.get(i).getCantidad());
                producto.setIdProveedor(factura.getProveedor().getIdProveedor());
                try {
                    productoService.saveProducto(producto);
                } catch (DataAccessException e) {
                    return new ResponseEntity<>(new CustomErrorType("Ocurrio un error al guardar la factura, contacte con el administrador del sistema ",
                            "Error").getResponse(),
                            HttpStatus.CONFLICT);
                }

                facturaProducto.setProducto(producto);
                facturaProducto.setCantidadRestante(facturaItems.get(i).getCantidad());
                facturaProducto.setCantidad(facturaItems.get(i).getCantidad());
                factura.addItemFactura(facturaProducto);
            } else {
                // si el producto existe
                productoAux.setCantidad(productoAux.getCantidad() + facturaItems.get(i).getCantidad());
                productoAux.setDescripcion(producto.getDescripcion());
                facturaProducto.setProducto(productoAux);
                facturaProducto.setCantidad(facturaItems.get(i).getCantidad());
                facturaProducto.setCantidadRestante(facturaItems.get(i).getCantidad());
                factura.addItemFactura(facturaProducto);
            }
        }
        factura.setIdUsuario(usuario.getIdUsuario());
        try {
            facturaService.saveFactura(factura);
        } catch (DataAccessException e) {
            return new ResponseEntity<>(new CustomErrorType("Ocurrio un error al guardar la factura, contacte con el administrador del sistema ",
                    "Error").getResponse(),
                    HttpStatus.CONFLICT);
        }


        return new ResponseEntity<>(new CustomResponseType("Ha sido registrada la factura  con el folio " + factura.getFolio(),
                "folio", factura.getFolio(), "Factura registrada").getResponse(), HttpStatus.OK);

    }

    // EDITAR FACTURA
    @RequestMapping(value = "/facturas", method = RequestMethod.PATCH)
    public ResponseEntity<?> actualizarFactura(@RequestBody() Factura factura, Authentication authentication) {

        Usuario usuario = usuarioService.findByNombreUsuario(authentication.getName());
        Boolean privilegio = false;
        for (PrivilegioUsuario privilegioUsuario : usuario.getPrivilegios()) {
            if (privilegioUsuario.getPrivilegio().getNombre().equals("actualizar facturas")) {
                privilegio = true;
                break;
            }
        }
        if (!privilegio) {
            return new ResponseEntity<>(new CustomErrorType("No tienes permisos para esta accion", "No has ingresado ni un producto").getResponse(), HttpStatus.CONFLICT);
        }

        Proveedor proveedor = proveedorService.getProveedorByNombre(factura.getProveedor().getNombre());
        if (proveedor == null) {
            return new ResponseEntity<>(new CustomErrorType("El proveedor no existe", "No existe el proveedor").getResponse(), HttpStatus.CONFLICT);
        }

        // datos de la factura anterior para recuperar la cantidad anterior :v
        Factura facturaAux = facturaService.getFacturaByFolio(factura.getFolio());
        ArrayList<FacturaProducto> listProductos = new ArrayList<>(factura.getItems());
        ArrayList<FacturaProducto> listProductosAux = new ArrayList<>(facturaAux.getItems());
        factura.getItems().clear();
        // AGREGAR EL NUEVO PRODUCTO SI NO EXISTE
        for(FacturaProducto productoFactura : listProductos) {
            // VARIBLES PARA VERIFICAR SI EXISTE EL PRODUCTO
            String clave = productoFactura.getProducto().getClave();
            String descripcion = productoFactura.getProducto().getDescripcion();
            FacturaProducto facturaProducto = new FacturaProducto();
            Producto productoAux = productoService.getProductoByClaveAndDescripcion(clave, descripcion);
            // si no existe el producto
            if (productoAux == null) {
                Producto producto = productoFactura.getProducto();
                producto.setIdProveedor(proveedor.getIdProveedor());
                producto.setIdUsuario(usuario.getIdUsuario());
                productoService.saveProducto(producto);
                facturaProducto.setProducto(producto);
                facturaProducto.setCantidad(producto.getCantidad());
                facturaProducto.setCantidadRestante(producto.getCantidad());

                factura.addItemFactura(facturaProducto);
            } else {
                // SI EL PRODUCTO EXISTE LO ACTUALIZAMOS
                boolean encontrado = false;
                for (int i = 0; i < listProductosAux.size(); i++) {
                    if (productoAux.getClave().equals(listProductosAux.get(i).getProducto().getClave())) {
                        int nuevaCantidad = 0;
                        if (productoAux.getCantidad() < listProductosAux.get(i).getCantidad()) {
                            nuevaCantidad = listProductosAux.get(i).getCantidad() - productoAux.getCantidad();
                        } else {
                            nuevaCantidad = productoAux.getCantidad() - listProductosAux.get(i).getCantidad();
                        }
                        Integer diferencia = productoFactura.getCantidad() - listProductosAux.get(i).getCantidad();
                        // Validar si es posible disminuir productos de la factura
                        Integer nuevaCantidadRestante = listProductosAux.get(i).getCantidadRestante() + diferencia;
                        if ( nuevaCantidadRestante < 0) {
                            return new ResponseEntity<>(new CustomErrorType("No es posible disminiur la cantidad del producto " +
                                    listProductosAux.get(i).getProducto().getDescripcion() + " porque ya fueron entregadas", "No es posible").getResponse(), HttpStatus.CONFLICT);
                        }

                        productoAux.setDescripcion(productoFactura.getProducto().getDescripcion());
                        productoAux.setPrecio(productoFactura.getProducto().getPrecio());
                        productoAux.setUnidadMedida(productoFactura.getProducto().getUnidadMedida());
                        productoAux.setCantidad(nuevaCantidad + productoFactura.getCantidad());
                        productoAux.setIdProveedor(proveedor.getIdProveedor());
                        productoService.saveProducto(productoAux);
                        facturaProducto.setIdFacturaProducto(listProductosAux.get(i).getIdFacturaProducto());
                        facturaProducto.setProducto(productoAux);
                        facturaProducto.setCantidad(productoFactura.getCantidad());
                        facturaProducto.setCantidadRestante(nuevaCantidadRestante);
                        facturaProducto.setIdFacturaProducto(facturaProducto.getIdFacturaProducto());
                        factura.addItemFactura(facturaProducto);
                        encontrado = true;
                        listProductosAux.remove(i);
                    }
                }
                if (!encontrado) {
                    productoAux.setCantidad(productoAux.getCantidad() + productoFactura.getCantidad());
                    productoAux.setIdProveedor(proveedor.getIdProveedor());
                    productoService.saveProducto(productoAux);
                    facturaProducto.setProducto(productoAux);
                    facturaProducto.setCantidad(productoFactura.getCantidad());
                    facturaProducto.setCantidadRestante(productoFactura.getCantidad());
                    facturaProducto.setIdFacturaProducto(productoFactura.getIdFacturaProducto());
                    factura.addItemFactura(facturaProducto);
                }

            }
        }
        // LE RESTO CANTIDAD A LOS PRODUCTOS QUE FUERON ELIMINADOS DE LA FACTURA
        listProductosAux.forEach(productoBorrar -> {
            Producto productoDelete = productoBorrar.getProducto();
            productoDelete.setCantidad(productoDelete.getCantidad() - productoBorrar.getCantidad());
            productoService.saveProducto(productoDelete);
        });
        boolean completada = true;
        for (FacturaProducto facturaProducto : factura.getItems()) {
            if (facturaProducto.getCantidadRestante() > 0) {
                completada = false;
                break;
            }
        }

        if (!completada) {
            factura.setCompletada(false);
        } else {
            factura.setCompletada(true);
        }
        factura.setProveedor(proveedor);
        try {
            facturaService.saveFactura(factura);
        } catch (DataAccessException e) {
            return new ResponseEntity<>(new CustomErrorType("Ocurrio un error al actualizar la factura, contacte con el administrador del sistema ",
                    "Error").getResponse(),
                    HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(new CustomResponseType("Se ha actulizo la factura con el folio" + factura.getFolio(),
                "factura", "", "").getResponse(), HttpStatus.OK);
    }





}
