package com.flamel.almacenutt.controllers;

import com.flamel.almacenutt.models.entity.Factura;
import com.flamel.almacenutt.models.entity.FacturaProducto;
import com.flamel.almacenutt.models.entity.Producto;
import com.flamel.almacenutt.models.service.FacturaService;
import com.flamel.almacenutt.util.CustomResponseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/v1")
public class FacturaController {

    @Autowired
    private FacturaService facturaService;

    @RequestMapping(value = "/facturas", method = RequestMethod.GET)
    public ResponseEntity<?> getFacturas() {
        return new ResponseEntity<>(new CustomResponseType("Lista de facturas",
                "facturas",
                facturaService.findAllFacturas(),"").getResponse(),
                HttpStatus.OK);
    }

    @RequestMapping(value = "/facturas", method = RequestMethod.POST)
    public ResponseEntity<?> createFactura(@RequestBody() Producto[] productos) {

       // System.out.println(factura.getFechaExpedicion());
        System.out.println(productos[0].getDescripcion());

        return new ResponseEntity<>("ok", HttpStatus.OK);
    }

}
