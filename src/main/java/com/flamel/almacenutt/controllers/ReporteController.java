package com.flamel.almacenutt.controllers;

import com.flamel.almacenutt.models.entity.*;
import com.flamel.almacenutt.models.model.ReporteGastoArea;
import com.flamel.almacenutt.models.model.ReporteProducto;
import com.flamel.almacenutt.models.model.ReporteProductosValeSalida;
import com.flamel.almacenutt.models.service.*;
import com.flamel.almacenutt.util.CustomErrorType;
import com.flamel.almacenutt.util.CustomResponseType;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1")
public class ReporteController {

    @Autowired
    ReporteService reporteService;

    @Autowired
    ValeSalidaService valeSalidaService;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    ProveedorService proveedorService;

    @Autowired
    ProductoService productoService;

    @Autowired
    JasperReportServiceImpl jasperReportService;

    @RequestMapping(value = "/reportes/productos", method = RequestMethod.GET)
    public ResponseEntity<?> getReporteProductos(@RequestParam("mes") Integer mes,
                                                      @RequestParam("anio") Integer anio, Authentication authentication) {

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


        byte[] reporte = null;
        Map<String, Object> params = new HashMap<>();
        params.put("mes", obtenerNombreMes(mes));
        params.put("anio", String.valueOf(anio));
        List<ReporteProducto> productos = reporteService.findAllProductosByDate(mes, anio);
        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(productos);

        reporte = jasperReportService.generatePDFReport("reporteProductos", params, ds);
        HttpHeaders cabecera = new HttpHeaders();
         cabecera.add(HttpHeaders.CONTENT_DISPOSITION, "filename=\"productos.pdf\"");
        return new ResponseEntity<byte[]>(reporte, cabecera, HttpStatus.OK);

    }

    // PRODUCTOS POR PROVEEDOR
    @RequestMapping(value = "/reportes/productos/proveedor/{nombreProveedor}", method = RequestMethod.GET)
    public ResponseEntity<?> getProductosByProveedor(@PathVariable() String nombreProveedor, Authentication authentication) {

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

        Proveedor proveedor = proveedorService.getProveedorByNombre(nombreProveedor);
        if (proveedor == null) {
            return new ResponseEntity<>(new CustomErrorType("No existe el proveedor", "").getResponse(),
                    HttpStatus.CONFLICT);
        }
        byte[] reporte = null;
        Map<String, Object> params = new HashMap<>();
        params.put("proveedor", nombreProveedor);
        List<Producto> productosProveedor = reporteService.getProductosByProveedor(proveedor.getIdProveedor());
        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(productosProveedor);

        reporte = jasperReportService.generatePDFReport("proveedor", params, ds);
        HttpHeaders cabecera = new HttpHeaders();
        cabecera.add(HttpHeaders.CONTENT_DISPOSITION, "filename=\"proveedor.pdf\"");
        return new ResponseEntity<byte[]>(reporte, cabecera, HttpStatus.OK);
    }

    @RequestMapping(value = "/reportes/areas/gastos", method = RequestMethod.GET)
    public ResponseEntity<?> getAreasGastos(@RequestParam("del") String fecha1,
                                            @RequestParam("al") String fecha2, Authentication authentication) {

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

        byte[] reporte = null;
        Map<String, Object> params = new HashMap<>();
        params.put("fecha1", fecha1);
        params.put("fecha2", fecha2);
        List<ReporteGastoArea> areaGastos = reporteService.getAreasGastos(fecha1, fecha2);
        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(areaGastos);

        reporte = jasperReportService.generatePDFReport("gastos", params, ds);
        HttpHeaders cabecera = new HttpHeaders();
        cabecera.add(HttpHeaders.CONTENT_DISPOSITION, "filename=\"gastos.pdf\"");
        return new ResponseEntity<byte[]>(reporte, cabecera, HttpStatus.OK);
    }


    // IMPRIMIR VALE DE SALIDA

    @RequestMapping(value = "/generar/vales/{idVale}", method = RequestMethod.GET)
    public ResponseEntity<?> getValeSalida(@PathVariable("idVale") Long idVale, Authentication authentication) {

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

        byte[] reporte = null;
        ValeSalida vale = valeSalidaService.getValeSalidaById(idVale);
        Map<String, Object> params = new HashMap<>();
        String fechaSeparada[] = vale.getFechaEntrega().split("-");
        String mesSeparado = fechaSeparada[1].replace("0", "");
        String fecha = fechaSeparada[2] + " de " + obtenerNombreMes(Integer.parseInt(mesSeparado)) + " del " + fechaSeparada[0];
        params.put("numeroRequisicion", String.valueOf(vale.getNumeroRequisicion()));
        params.put("area", vale.getArea().getNombre().toUpperCase());
        params.put("responsable", vale.getArea().getResponsable());
        params.put("fechaEntrega", fecha);
        List<ReporteProductosValeSalida> valeSalida = reporteService.getProductosByValeSalidaId(vale.getIdValeSalida());
        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(valeSalida);

        reporte = jasperReportService.generatePDFReport("vale_salida", params, ds);
        HttpHeaders cabecera = new HttpHeaders();
        cabecera.add(HttpHeaders.CONTENT_DISPOSITION, "filename=\"vale.pdf\"");
        return new ResponseEntity<byte[]>(reporte, cabecera, HttpStatus.OK);
    }

    @RequestMapping(value = "/reportes/productos/areas", method = RequestMethod.GET)
    public ResponseEntity<?> getAreasGastos(@RequestParam("del") String fecha1,
                                            @RequestParam("al") String fecha2,
                                            @RequestParam("area") Long idArea, Authentication authentication) {


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

        Area area = usuarioService.findAreaByid(idArea);
        if (area == null) {
            return new ResponseEntity<>(new CustomErrorType("No existe el área", "").getResponse(),
                    HttpStatus.CONFLICT);
        }
        byte[] reporte = null;
        double total = 0;
        Map<String, Object> params = new HashMap<>();
        params.put("fecha1", fecha1);
        params.put("fecha2", fecha2);
        params.put("area", area.getNombre());
        List<ReporteProducto> productosArea = reporteService.getProductosByArea(idArea, fecha1, fecha2);
        for (ReporteProducto producto: productosArea) {
            total += producto.getPrecio() * producto.getCantidad();
        }
        params.put("total", total);
        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(productosArea);

        reporte = jasperReportService.generatePDFReport("reporteProductosArea", params, ds);
        HttpHeaders cabecera = new HttpHeaders();
        cabecera.add(HttpHeaders.CONTENT_DISPOSITION, "filename=\"areas.pdf\"");
        return new ResponseEntity<byte[]>(reporte, cabecera, HttpStatus.OK);


    }

    public String obtenerNombreMes(Integer mes) {
        String meses[] = {"", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        return meses[mes];
    }

}


