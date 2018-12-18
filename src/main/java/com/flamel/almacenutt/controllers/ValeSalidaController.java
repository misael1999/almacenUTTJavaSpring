package com.flamel.almacenutt.controllers;

import com.flamel.almacenutt.models.entity.*;
import com.flamel.almacenutt.models.service.FacturaService;
import com.flamel.almacenutt.models.service.ProductoService;
import com.flamel.almacenutt.models.service.UsuarioService;
import com.flamel.almacenutt.models.service.ValeSalidaService;
import com.flamel.almacenutt.util.CustomErrorType;
import com.flamel.almacenutt.util.CustomResponseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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

//        if (entregadas != null) {
//            return new ResponseEntity<>(new CustomResponseType("Lista de vales entregadas",
//                    "vales",
//                    valeSalidaService.listValeSalidaEntregadas(PageRequest.of(page, 15, sort)), "").getResponse(),
//                    HttpStatus.OK);
//        }

        System.out.println();
        Page<ValeSalida> valeSalidas = valeSalidaService.listValeSalidaActivas(PageRequest.of(page, 15, sort));
        System.out.println(valeSalidas.getTotalElements());

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
                valeSalidaService.findValeSalidaByIdArea(idArea),"").getResponse(),
                HttpStatus.OK);
    }


    @RequestMapping(value = "/vales", method = RequestMethod.POST)
    public ResponseEntity<?> crearValeSalida(@RequestBody() ValeSalida vale, Authentication authentication) {

        Usuario usuario = usuarioService.findByNombreUsuario(authentication.getName());
        boolean privilegio = false;
        for (PrivilegioUsuario privilegioUsuario: usuario.getPrivilegios()) {
            if (privilegioUsuario.getPrivilegio().getNombre().equals("generar vales de salidas")) {
                privilegio = true;
                break;
            }
        }
        if (!privilegio) {
            return new ResponseEntity<>(new CustomErrorType("No tienes permisos para esta accion", "No has ingresado ni un producto").getResponse(), HttpStatus.CONFLICT);
        }

//        ValeSalida valeExist = valeSalidaService.findValeSalidaByNumeroRequisicion(vale.getNumeroRequisicion());
//        if (valeExist != null) {
//            return new ResponseEntity<>(new CustomErrorType("El vale de salida de la requisición número " + vale.getNumeroRequisicion() + " ya existe",
//                    "El vale de salida ya existe").getResponse(), HttpStatus.CONFLICT);
//        }

        if ( vale.getArea() == null ||  vale.getNumeroRequisicion() == null || vale.getFactura() == null) {
            return new ResponseEntity<>(new CustomErrorType("Ingresa los campos obligatorios", "No has ingresado todos los campos").getResponse(),
                    HttpStatus.CONFLICT);
        }

        if (vale.getItems().size() == 0) {
            return new ResponseEntity<>(new CustomErrorType("Ingresa al menos un producto", "No has ingresado ni un producto").getResponse(),
                    HttpStatus.CONFLICT);
        }
        // Productos agregados al vale de salida
        List<ValeProducto> listValeProductos = vale.getItems();

        Factura factura = vale.getFactura();
        // productos de la factura
        ArrayList<FacturaProducto> listProductsFactura = new ArrayList<>(factura.getItems());

        // VERFICAMOS SI TIENE LA CANTIDAD NECESARIA PARA SACAR LOS PRODUCTOS
        for (ValeProducto salidaProducto : listValeProductos) {
            FacturaProducto productoFacturaProducto = salidaProducto.getFacturaProducto();
            if (salidaProducto.getCantidadEntregada() > productoFacturaProducto.getCantidadRestante()) {
                return new ResponseEntity<>(new CustomErrorType("No hay cantidad de suficiente del producto " + salidaProducto.getFacturaProducto().getProducto().getDescripcion(),
                        "Cantidad insuficiente").getResponse(),
                        HttpStatus.CONFLICT);
            }
        // TERMINAMOS LA VERIFICACION ==============================
            productoFacturaProducto.setCantidadRestante(productoFacturaProducto.getCantidadRestante() - salidaProducto.getCantidadEntregada());
            valeSalidaService.updateFacturaProducto(productoFacturaProducto);
        }
        vale.setIdUsuario(usuario.getIdUsuario());
        valeSalidaService.saveValeSalida(vale);

        return new ResponseEntity<>(new CustomResponseType("Se regitró el vale de salida con el número " + vale.getNumeroRequisicion(),
                "idVale", vale.getIdValeSalida(), "Vale de salida registrado").getResponse(), HttpStatus.OK);
    }
}
