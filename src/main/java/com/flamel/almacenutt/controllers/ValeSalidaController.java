package com.flamel.almacenutt.controllers;

import com.flamel.almacenutt.models.entity.*;
import com.flamel.almacenutt.models.service.ProductoService;
import com.flamel.almacenutt.models.service.ValeSalidaService;
import com.flamel.almacenutt.util.CustomErrorType;
import com.flamel.almacenutt.util.CustomResponseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1")
public class ValeSalidaController {

    @Autowired
    private ValeSalidaService valeSalidaService;

    @Autowired
    private ProductoService productoService;

    // VALES DE SALIDAS

    @RequestMapping(value = "/vales", method = RequestMethod.GET)
    public ResponseEntity<?> getValesSalidas(@RequestParam(value = "entregadas", required = false) String entregadas) {

        if (entregadas != null) {
            return new ResponseEntity<>(new CustomResponseType("Lista de vales entregadas",
                    "vales",
                    valeSalidaService.listValeSalidaEntregadas(), "").getResponse(),
                    HttpStatus.OK);
        }

        return new ResponseEntity<>(new CustomResponseType("Lista de vales activas",
                "vales",
                valeSalidaService.listValeSalidaActivas(), "").getResponse(),
                HttpStatus.OK);
    }

    @RequestMapping(value = "/vales", method = RequestMethod.POST)
    public ResponseEntity<?> crearValeSalida(@RequestBody() ValeSalida vale) {
        ValeSalida valeExist = valeSalidaService.findValeSalidaByNumeroRequisicion(vale.getNumeroRequisicion());

        if (valeExist != null) {
            return new ResponseEntity<>(new CustomErrorType("El vale de salida de la requisición número " + vale.getNumeroRequisicion() + " ya existe",
                    "El vale de salida ya existe").getResponse(), HttpStatus.CONFLICT);
        }

        if ( vale.getArea() == null || vale.getFactura() == null || vale.getNumeroRequisicion() == null) {
            return new ResponseEntity<>(new CustomErrorType("Ingresa los campos obligatorios", "No has ingresado todos los campos").getResponse(),
                    HttpStatus.CONFLICT);
        }

        if (vale.getItems().size() == 0) {
            return new ResponseEntity<>(new CustomErrorType("Ingresa al menos un producto", "No has ingresado ni un producto").getResponse(),
                    HttpStatus.CONFLICT);
        }
        List<ValeProducto> listValeProductos = vale.getItems();
        for (ValeProducto salidaProducto : listValeProductos) {
            Producto producto = salidaProducto.getProducto();
            // VALIDAMOS QUE LA CANTIDAD DEL PRODUCTO INGRESADA EN LA FACTURA SEA MAYOR O IGUAL A LA
            // CANTIDAD ENTREGADA
            if (salidaProducto.getCantidadEntregada() > producto.getCantidad()) {
                return new ResponseEntity<>(new CustomErrorType("No hay cantidad de suficiente del producto " + producto.getDescripcion(),
                        "Cantidad insuficiente").getResponse(),
                        HttpStatus.CONFLICT);
            }
        }
        valeSalidaService.saveValeSalida(vale);

        return new ResponseEntity<>(new CustomResponseType("Se regitró el vale de salida con el número " + vale.getNumeroRequisicion(),
                "número de requisición", vale.getNumeroRequisicion(), "Vale de salida registrado").getResponse(), HttpStatus.OK);
    }
}
