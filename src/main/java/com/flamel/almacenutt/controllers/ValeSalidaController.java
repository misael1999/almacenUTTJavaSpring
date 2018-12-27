package com.flamel.almacenutt.controllers;

import com.flamel.almacenutt.models.entity.*;
import com.flamel.almacenutt.models.service.FacturaService;
import com.flamel.almacenutt.models.service.ProductoService;
import com.flamel.almacenutt.models.service.UsuarioService;
import com.flamel.almacenutt.models.service.ValeSalidaService;
import com.flamel.almacenutt.util.CustomErrorType;
import com.flamel.almacenutt.util.CustomResponseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/v1")
public class ValeSalidaController {

    @Autowired
    private ValeSalidaService valeSalidaService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private FacturaService facturaService;

    @Autowired
    private UsuarioService usuarioService;

    // VALES DE SALIDAS

    @RequestMapping(value = "/vales/page/{page}", method = RequestMethod.GET)
    public ResponseEntity<?> getValesSalidas(@PathVariable("page") Integer page,
                                             @RequestParam(value = "entregadas", required = false) String entregadas,
                                             @RequestParam(value = "ordenar", required = true) String ordenar) {

        Sort sort = null;
        if (ordenar.equals("desc")) {
            sort = Sort.by("fechaEntrega").descending();
        } else if (ordenar.equals("asc")) {
            sort = Sort.by("fechaEntrega").ascending();
        }

        Page<ValeSalida> valeSalidas = valeSalidaService.listValeSalidaActivas(PageRequest.of(page, 15, sort));

        return new ResponseEntity<>(new CustomResponseType("Lista de vales activas",
                "vales", valeSalidas, "").getResponse(),
                HttpStatus.OK);
    }

    // OBTENER VALE POR ID DE VALE
    @RequestMapping(value = "/vales/id/{idVale}", method = RequestMethod.GET)
    public ResponseEntity<?> getValeByNumeroRequisicion(@PathVariable("idVale") Long idVale) {
        return new ResponseEntity<>(new CustomResponseType("Vale de salida", "vale",
                valeSalidaService.getValeSalidaById(idVale), "").getResponse(),
                HttpStatus.OK);
    }

    // OBTENER COINCIDENCIAS DE VALES DE SALIDA
    @RequestMapping(value = "/vales/todo/{termino}", method = RequestMethod.GET)
    public ResponseEntity<?> getValeLikeTermino(@PathVariable("termino") String termino) {

        return new ResponseEntity<>(new CustomResponseType("vales encontradas",
                "vales", valeSalidaService.getValesByTerminoLike(termino),
                "").getResponse(), HttpStatus.OK);
    }


    // OBTENER VALES POR AREA
    @RequestMapping(value = "/vales/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getValesSalidas(@PathVariable("id") Long idArea) {

        return new ResponseEntity<>(new CustomResponseType("Vales", "vales",
                valeSalidaService.findValeSalidaByIdArea(idArea), "").getResponse(),
                HttpStatus.OK);
    }


    @RequestMapping(value = "/vales", method = RequestMethod.POST)
    public ResponseEntity<?> crearValeSalida(@RequestBody() ValeSalida vale, Authentication authentication) {


        Usuario usuario = usuarioService.findByNombreUsuario(authentication.getName());
        boolean privilegio = false;
        for (PrivilegioUsuario privilegioUsuario : usuario.getPrivilegios()) {
            if (privilegioUsuario.getPrivilegio().getNombre().equals("generar vales de salidas")) {
                privilegio = true;
                break;
            }
        }
        if (!privilegio) {
            return new ResponseEntity<>(new CustomErrorType("No tienes permisos para esta accion", "No has ingresado ni un producto").getResponse(), HttpStatus.CONFLICT);
        }

        if (vale.getArea() == null || vale.getNumeroRequisicion() == null || vale.getFactura() == null) {
            return new ResponseEntity<>(new CustomErrorType("Ingresa los campos obligatorios", "No has ingresado todos los campos").getResponse(),
                    HttpStatus.CONFLICT);
        }

        if (vale.getItems().size() == 0) {
            return new ResponseEntity<>(new CustomErrorType("Ingresa al menos un producto", "No has ingresado ni un producto").getResponse(),
                    HttpStatus.CONFLICT);
        }

        ValeSalida valeSalida = valeSalidaService.findValeSalidaByNumeroRequisicionAndArea_IdArea(vale.getNumeroRequisicion(), vale.getArea().getIdArea());

        if (valeSalida != null) {
            return new ResponseEntity<>(new CustomErrorType("Ya existe un vale de salida con el numero de requisicion " + vale.getNumeroRequisicion() + " para esa área"
                    , "Ya existe el vale").getResponse(),
                    HttpStatus.CONFLICT);
        }
        // Productos agregados al vale de salida
        List<ValeProducto> listValeProductos = vale.getItems();

        // VERFICAMOS SI TIENE LA CANTIDAD NECESARIA PARA SACAR LOS PRODUCTOS
        for (ValeProducto salidaProducto : listValeProductos) {
            FacturaProducto productoFacturaProducto = salidaProducto.getFacturaProducto();
            if (salidaProducto.getCantidadEntregada() > productoFacturaProducto.getCantidadRestante()) {
                return new ResponseEntity<>(new CustomErrorType("No hay cantidad de suficiente del producto " + salidaProducto.getFacturaProducto().getProducto().getDescripcion(),
                        "Cantidad insuficiente").getResponse(),
                        HttpStatus.CONFLICT);
            }
            // TERMINAMOS LA VERIFICACION ==============================

            // Actualizamos factura producto
            Producto producto = salidaProducto.getFacturaProducto().getProducto();
            producto.setCantidad(producto.getCantidad() - salidaProducto.getCantidadEntregada());
            productoService.saveProducto(producto);
            productoFacturaProducto.setCantidadRestante(productoFacturaProducto.getCantidadRestante() - salidaProducto.getCantidadEntregada());
            valeSalidaService.updateFacturaProducto(productoFacturaProducto);
        }
        // VERICAMOS QUE LA FACTURA NO CONTENGA PRODUCTOS
        boolean completada = true;
        Factura factura = facturaService.getFacturaByFolio(vale.getFactura().getFolio());
        for (FacturaProducto facturaProducto : factura.getItems()) {
            if (facturaProducto.getCantidadRestante() > 0) {
                completada = false;
                break;
            }
        }

        vale.setIdUsuario(usuario.getIdUsuario());

        if (completada) {
            factura.setCompletada(true);
            facturaService.saveFactura(factura);
        }

        try {
            valeSalidaService.saveValeSalida(vale);
        } catch (DataAccessException e) {
            return new ResponseEntity<>(new CustomErrorType("Ocurrio un error al generar el vale de salida, contacte con el administrador del sistema ",
                    "Error").getResponse(),
                    HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(new CustomResponseType("Se regitró el vale de salida con el número " + vale.getNumeroRequisicion(),
                "idVale", vale.getIdValeSalida(), "Vale de salida registrado").getResponse(), HttpStatus.OK);
    }

    @RequestMapping(value = "/vales", method = RequestMethod.PATCH)
    public ResponseEntity<?> updateValeSalida(@RequestBody() ValeSalida valeSalida, Authentication authentication) {

        if (valeSalida.getIdValeSalida() == null) {
            return new ResponseEntity<>(new CustomErrorType("El id es necesario", ""), HttpStatus.CONFLICT);
        }

        Usuario usuario = usuarioService.findByNombreUsuario(authentication.getName());
        boolean privilegio = false;
        for (PrivilegioUsuario privilegioUsuario : usuario.getPrivilegios()) {
            if (privilegioUsuario.getPrivilegio().getNombre().equals("actualizar vales de salidas")) {
                privilegio = true;
                break;
            }
        }
        if (!privilegio) {
            return new ResponseEntity<>(new CustomErrorType("No tienes permisos para esta accion", "No has ingresado ni un producto").getResponse(), HttpStatus.FORBIDDEN);
        }

        List<ValeProducto> valeProductoList = valeSalida.getItems();
        List<ValeProducto> valeProductoListAux = valeSalidaService.getValeSalidaById(valeSalida.getIdValeSalida()).getItems();

        for (ValeProducto valeProducto : valeProductoList) {
            Producto producto = valeProducto.getFacturaProducto().getProducto();
            FacturaProducto facturaProducto = valeProducto.getFacturaProducto();
            boolean exist = false;
            // VERIFICAMOS SI TENEMOS QUE ACTUALIZAR EL PRODUCTO A AGREGARLO
            for (ValeProducto productoListAux : valeProductoListAux) {
                // SI EL PRODUCTO EXISTE LO ACTUALIZAMOS
                if (valeProducto.getIdSalidaProducto() == productoListAux.getIdSalidaProducto()) {
                   Integer cantidadProductoActualizada = (producto.getCantidad() + productoListAux.getCantidadEntregada()) - valeProducto.getCantidadEntregada();
                    producto.setCantidad(cantidadProductoActualizada);
                    productoService.saveProducto(producto);
                    // cantidad restante
                    Integer nuevaCantidad = (facturaProducto.getCantidadRestante() + productoListAux.getCantidadEntregada()) - valeProducto.getCantidadEntregada();
                    facturaProducto.setCantidadRestante(nuevaCantidad);
                    valeSalidaService.updateFacturaProducto(facturaProducto);
                    valeProductoListAux.remove(productoListAux);
                    exist = true;
                    break;
                }
            }
            // SI EL PRODUCTO NO EXISTE
            if (!exist) {
                if (facturaProducto.getCantidadRestante() < valeProducto.getCantidadEntregada()) {
                    return new ResponseEntity<>(new CustomErrorType("No hay suficiente cantidad para el producto " + producto.getDescripcion(), "").getResponse(), HttpStatus.CONFLICT);
                }
                facturaProducto.setCantidadRestante(facturaProducto.getCantidadRestante() - valeProducto.getCantidadEntregada());
                valeSalidaService.updateFacturaProducto(facturaProducto);
            }
        }

        // devolver productos
        valeProductoListAux.forEach(valeProducto -> {

            FacturaProducto facturaProducto = valeProducto.getFacturaProducto();
            Producto producto = valeProducto.getFacturaProducto().getProducto();
            facturaProducto.setCantidadRestante(facturaProducto.getCantidadRestante() + valeProducto.getCantidadEntregada());
            producto.setCantidad(producto.getCantidad() + valeProducto.getCantidadEntregada());
            valeSalidaService.updateFacturaProducto(facturaProducto);
            productoService.saveProducto(producto);
        });

        valeSalida.setIdUsuario(usuario.getIdUsuario());
        valeSalidaService.saveValeSalida(valeSalida);

        return new ResponseEntity<>(new CustomResponseType("Se ha actualizado el vale de salida " + valeSalida.getIdValeSalida(), "", "", "Actualizado").getResponse(), HttpStatus.OK);

    }

    // UPLOAD FACTURA
    @RequestMapping(value = "/vales/upload", method = RequestMethod.PATCH)
    public ResponseEntity<?> uploadFactura(@RequestParam(value = "archivo") MultipartFile documento,
                                           @RequestParam(value = "idVale") Long idValeSalida, Authentication authentication) {

        Usuario usuario = usuarioService.findByNombreUsuario(authentication.getName());
        boolean privilegio = false;
        for (PrivilegioUsuario privilegioUsuario : usuario.getPrivilegios()) {
            if (privilegioUsuario.getPrivilegio().getNombre().equals("subir documentos")) {
                privilegio = true;
                break;
            }
        }
        if (!privilegio) {
            return new ResponseEntity<>(new CustomErrorType("No tienes permisos para esta accion", "No has ingresado ni un producto").getResponse(), HttpStatus.CONFLICT);
        }

        if (idValeSalida == null) {
            return new ResponseEntity<>(new CustomErrorType("El id es necesario",
                    "No selecciono un vale").getResponse(), HttpStatus.CONFLICT);
        }

        // NOS ASEGURAMOS QUE SELECCIONE UN ARCHIVO
        if (documento.isEmpty()) {
            return new ResponseEntity<>(new CustomErrorType("Debe de seleccionar un archivo",
                    "No selecciono un archivo").getResponse(), HttpStatus.CONFLICT);
        }



        String[] tipoValido = {"png", "PNG", "jpg", "JPG", "jpeg", "JPEG", "pdf", "PDF"};
        String tipoArchivo = documento.getContentType().split("/")[1];

        // VERIFICAMOS TIPO DE ARCHIVO
        if (!Arrays.asList(tipoValido).contains(tipoArchivo)) {
            return new ResponseEntity<>(new CustomErrorType("Tipo de archivo no permitido",
                    "Tipo de archivo no permitido").getResponse(), HttpStatus.CONFLICT);
        }

        ValeSalida valeSalida = valeSalidaService.getValeSalidaById(idValeSalida);


        // BORRAMOS EL ARCHIVO ANTERIOR
        if (valeSalida.getDocumento() != null) {
            String fileName = valeSalida.getDocumento();
            Path rutaAnterior = Paths.get("uploads/vales").resolve(fileName).toAbsolutePath();
            File f = rutaAnterior.toFile();
            if (f.exists()) {
                f.delete();
            }
        }

        //NOMBRE DEL DOCUMENTO
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String dateName = dateFormat.format(date);
        String nombreArchivo = valeSalida.getIdValeSalida() + "-vale-" + dateName + "." + documento.getContentType().split("/")[1];
        Path rutaArchivo = Paths.get("uploads/vales").resolve(nombreArchivo).toAbsolutePath();
        try {
            Files.copy(documento.getInputStream(), rutaArchivo);
        } catch (IOException e) {
            return new ResponseEntity<>(new CustomErrorType("Ocurrio un error al subir el archivo",
                    "Error al subir el archivo").getResponse(), HttpStatus.CONFLICT);
        }

        // ACTUALIZAMOS LA FACTURA CON EL DOCUMENTO
        valeSalida.setDocumento(nombreArchivo);
        try {
            valeSalidaService.saveValeSalida(valeSalida);
        } catch (DataAccessException e) {
            return new ResponseEntity<>(new CustomErrorType("Ocurrio un error al guardar el documento, contacte con el administrador del sistema ",
                    "Error").getResponse(),
                    HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(new CustomResponseType("Documento guardado",
                "", "", "").getResponse(), HttpStatus.CREATED);


    }

    // DESCARGAR DOCUMENTO DE LA FACTURA
    @RequestMapping(value = "/vales/descargar/documento/{nombre}", method = RequestMethod.GET)
    public ResponseEntity<?> getArchivo(@PathVariable(value = "nombre") String nombreArchivo, Authentication authentication) {

        Usuario usuario = usuarioService.findByNombreUsuario(authentication.getName());
        boolean privilegio = false;
        for (PrivilegioUsuario privilegioUsuario : usuario.getPrivilegios()) {
            if (privilegioUsuario.getPrivilegio().getNombre().equals("descargar documentos")) {
                privilegio = true;
                break;
            }
        }
        if (!privilegio) {
            String mensaje = "No tienes permisos";
            return new ResponseEntity<>(new CustomErrorType("No tienes permisos para esta accion", "sin permisos").getResponse(), HttpStatus.CONFLICT);
        }

        Path rutaArchivo = Paths.get("uploads/vales").resolve(nombreArchivo).toAbsolutePath();
        org.springframework.core.io.Resource recurso = null;
        try {
            recurso = new UrlResource(rutaArchivo.toUri());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        if (!recurso.exists() && !recurso.isReadable()) {
            return new ResponseEntity<>(new CustomErrorType("Error no se pudo cargar el archivo:" + nombreArchivo, "")
                    .getResponse(), HttpStatus.CONFLICT);
        }

        HttpHeaders cabecera = new HttpHeaders();
        String[] nombreDocumento = recurso.getFilename().split(Pattern.quote("."));
        String tipoArchivo = nombreDocumento[nombreDocumento.length - 1];
        if (tipoArchivo.equals("pdf")) {
            cabecera.add(HttpHeaders.CONTENT_TYPE, "application/pdf");
        } else {
            cabecera.add(HttpHeaders.CONTENT_TYPE, "image/" + tipoArchivo );
        }
        cabecera.add(HttpHeaders.CONTENT_DISPOSITION, "filename=\"" + recurso.getFilename() + "\"");

        return new ResponseEntity<Resource>(recurso, cabecera, HttpStatus.OK);

    }

    @RequestMapping(value = "/vales/documentos/{page}", method = RequestMethod.GET)
    public ResponseEntity<?> getFacturasWithDcouments(@PathVariable("page") Integer page) {

        return new ResponseEntity<>(new CustomResponseType("Vales con documentos",
                "vales",
                valeSalidaService.listValesWithDocuments(PageRequest.of(page, 10)),
                "Vales encontradas").getResponse(), HttpStatus.OK);
    }


}
