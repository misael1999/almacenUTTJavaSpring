package com.flamel.almacenutt.controllers;

import com.flamel.almacenutt.models.entity.Factura;
import com.flamel.almacenutt.models.entity.FacturaProducto;
import com.flamel.almacenutt.models.entity.Producto;
import com.flamel.almacenutt.models.service.FacturaService;
import com.flamel.almacenutt.models.service.ProductoService;
import com.flamel.almacenutt.util.CustomErrorType;
import com.flamel.almacenutt.util.CustomResponseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/v1")
public class FacturaController {

    @Autowired
    private FacturaService facturaService;

    @Autowired
    private ProductoService productoService;

    @RequestMapping(value = "/facturas", method = RequestMethod.GET)
    public ResponseEntity<?> getFacturas() {
        return new ResponseEntity<>(new CustomResponseType("Lista de facturas",
                "facturas",
                facturaService.findAllFacturas(), "").getResponse(),
                HttpStatus.OK);
    }
    @RequestMapping(value = "/facturas", method = RequestMethod.POST)
    public ResponseEntity<?> crearFactura(@RequestBody() Factura factura) {

            if (factura.getFechaExpedicion() == null || factura.getProveedor().getIdProveedor() == null) {
                return new ResponseEntity<>(new CustomErrorType("Ingresa los campos obligatorios", "No has ingresado los campos obligatorios").getResponse(), HttpStatus.CONFLICT);
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
                if (productoAux == null) {
                    producto.setIdUsuario(factura.getIdUsuario());
                    productoService.saveProducto(producto);
                    facturaProducto.setProducto(producto);
                    factura.addItemFactura(facturaProducto);
                } else {
                    productoAux.setCantidad(productoAux.getCantidad() + producto.getCantidad());
                    facturaProducto.setProducto(productoAux);
                    factura.addItemFactura(facturaProducto);
                }
            }

            facturaService.saveFactura(factura);

            return new ResponseEntity<>(new CustomResponseType("Ha sido registrada la factura  con el folio " + factura.getIdFactura(),
                    "", "", "Factura registrada").getResponse(), HttpStatus.OK);

    }

}
