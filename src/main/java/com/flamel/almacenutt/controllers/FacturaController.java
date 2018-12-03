package com.flamel.almacenutt.controllers;

import com.flamel.almacenutt.models.entity.Factura;
import com.flamel.almacenutt.models.entity.FacturaProducto;
import com.flamel.almacenutt.models.entity.Producto;
import com.flamel.almacenutt.models.service.FacturaService;
import com.flamel.almacenutt.models.service.ProductoService;
import com.flamel.almacenutt.util.CustomErrorType;
import com.flamel.almacenutt.util.CustomResponseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

@RestController
@RequestMapping("/v1")
public class FacturaController {

    @Autowired
    private FacturaService facturaService;

    @Autowired
    private ProductoService productoService;

    @RequestMapping(value = "/facturas/page/{page}", method = RequestMethod.GET)
    public ResponseEntity<?> getFacturas(@PathVariable("page") Integer page, @RequestParam(value = "entregadas", required = false) String entregadas) {

        if (entregadas != null) {
            return new ResponseEntity<>(new CustomResponseType("Lista de facturas",
                    "facturas",
                    facturaService.listFacturasEntregadas(PageRequest.of(page,  15)), "").getResponse(),
                    HttpStatus.OK);
        }

        return new ResponseEntity<>(new CustomResponseType("Lista de facturas",
                "facturas",
                facturaService.listFacturasActivas(PageRequest.of(page, 15)), "").getResponse(),
                HttpStatus.OK);
    }

    // OBTENER FACTURA POR FOLIO
    @RequestMapping(value = "facturas/{folio}", method = RequestMethod.GET)
    public ResponseEntity<?> getFacturaByFolio(@PathVariable("folio") String folio) {
        Factura factura = facturaService.getFacturaByFolio(folio);
        return new ResponseEntity<>(new CustomResponseType("Factura encontrada",
                "factura", factura,"Factura encontrada").getResponse(), HttpStatus.OK);
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
    public ResponseEntity<?> crearFactura(@RequestBody() Factura factura) {

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

        List<FacturaProducto> facturaItems = new ArrayList<>();
        facturaItems.addAll(factura.getItems());
        factura.getItems().clear();

        for (int i = 0; i < facturaItems.size(); i++) {
            Producto producto = facturaItems.get(i).getProducto();
            Producto productoAux = productoService.getProductoByDescripcion(producto.getDescripcion().toLowerCase());
            FacturaProducto facturaProducto = new FacturaProducto();
            // si el producto no existe
            if (productoAux == null) {
                producto.setIdUsuario(factura.getIdUsuario());
                producto.setCantidad(facturaItems.get(i).getCantidad());
                productoService.saveProducto(producto);
                facturaProducto.setProducto(producto);
                facturaProducto.setCantidad(facturaItems.get(i).getCantidad());
                factura.addItemFactura(facturaProducto);
            } else {
                // si el producto existe
                productoAux.setCantidad(productoAux.getCantidad() + facturaItems.get(i).getCantidad());
                facturaProducto.setProducto(productoAux);
                facturaProducto.setCantidad(facturaItems.get(i).getCantidad());
                factura.addItemFactura(facturaProducto);
            }
        }
        facturaService.saveFactura(factura);

        return new ResponseEntity<>(new CustomResponseType("Ha sido registrada la factura  con el folio " + factura.getFolio(),
                "folio", factura.getFolio(), "Factura registrada").getResponse(), HttpStatus.OK);

    }

    // UPLOAD FACTURA
    @RequestMapping(value = "/facturas/upload", method = RequestMethod.PATCH)
    public ResponseEntity<?> uploadFactura(@RequestParam(value = "archivo") MultipartFile documento,
                                           @RequestParam(value = "folio") String folioFactura) {


        if (folioFactura == null || folioFactura.isEmpty()) {
            return new ResponseEntity<>(new CustomErrorType("El folio es necesario",
                    "No selecciono una factura").getResponse(), HttpStatus.CONFLICT);
        }

        Factura factura = facturaService.getFacturaByFolio(folioFactura);
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

        // BORRAMOS EL ARCHIVO ANTERIOR
        if (factura.getDocumento() != null) {
            String fileName = factura.getDocumento();
            Path rutaAnterior = Paths.get("uploads/facturas").resolve(fileName).toAbsolutePath();
            File f = rutaAnterior.toFile();
            if (f.exists()) {
                f.delete();
            }
        }

        //NOMBRE DEL DOCUMENTO
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String dateName = dateFormat.format(date);
        String nombreArchivo = factura.getFolio().replace(" ", "-") + "-factura-" + dateName + "." + documento.getContentType().split("/")[1];
        Path rutaArchivo = Paths.get("uploads/facturas").resolve(nombreArchivo).toAbsolutePath();
        try {
            Files.copy(documento.getInputStream(), rutaArchivo);
        } catch (IOException e) {
            return new ResponseEntity<>(new CustomErrorType("Ocurrio un error al subir el archivo",
                    "Error al subir el archivo").getResponse(), HttpStatus.CONFLICT);
        }

        // ACTUALIZAMOS LA FACTURA CON EL DOCUMENTO
        factura.setDocumento(nombreArchivo);
        facturaService.saveFactura(factura);
        return new ResponseEntity<>(new CustomResponseType("Documento guardado",
                "", "", "").getResponse(), HttpStatus.CREATED);


    }

    // DESCARGAR DOCUMENTO DE LA FACTURA
    @RequestMapping(value = "/facturas/documento/{nombre}", method = RequestMethod.GET)
    public ResponseEntity<org.springframework.core.io.Resource> getArchivo(@PathVariable(value = "nombre") String nombreArchivo) {

        Path rutaArchivo = Paths.get("uploads/facturas").resolve(nombreArchivo).toAbsolutePath();
        org.springframework.core.io.Resource recurso = null;
        try {
            recurso =  new UrlResource(rutaArchivo.toUri());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        if (!recurso.exists() && !recurso.isReadable()) {
            throw new RuntimeException("Error no se pudo cargar el archivo: " + nombreArchivo);
        }

        HttpHeaders cabecera = new HttpHeaders();
        cabecera.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"");

        return new ResponseEntity<Resource>(recurso, cabecera, HttpStatus.OK);

    }

    // VALES DE SALIDAS

    @RequestMapping(value = "/vales", method = RequestMethod.GET)
    public ResponseEntity<?> getValesSalidas(@RequestParam(value = "entregadas", required = false) String entregadas) {

        if (entregadas != null) {
            return new ResponseEntity<>(new CustomResponseType("Lista de vales entregadas",
                    "vales",
                    facturaService.listValeSalidaEntregadas(), "").getResponse(),
                    HttpStatus.OK);
        }

        return new ResponseEntity<>(new CustomResponseType("Lista de vales activas",
                "vales",
                facturaService.listValeSalidaActivas(), "").getResponse(),
                HttpStatus.OK);
    }


//    @RequestMapping(value = "/vales", method = RequestMethod.POST)
//    public ResponseEntity<?> createValeSalida(@RequestBody() ValeSalida valeSalida) {
//
//    }


}
