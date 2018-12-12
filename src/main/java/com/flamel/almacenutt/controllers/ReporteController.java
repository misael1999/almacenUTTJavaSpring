package com.flamel.almacenutt.controllers;

import com.flamel.almacenutt.models.entity.Producto;
import com.flamel.almacenutt.models.entity.ValeSalida;
import com.flamel.almacenutt.models.model.ReporteProducto;
import com.flamel.almacenutt.models.model.ReporteProductosValeSalida;
import com.flamel.almacenutt.models.service.JasperReportServiceImpl;
import com.flamel.almacenutt.models.service.ReporteService;
import com.flamel.almacenutt.models.service.ValeSalidaService;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    JasperReportServiceImpl jasperReportService;

    @RequestMapping(value = "/reportes/productos", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getReporteProductos(@RequestParam("mes") Integer mes,
                                                      @RequestParam("ano") Integer ano) {

        byte[] reporte = null;
        Map<String, Object> params = new HashMap<>();
        params.put("mes", obtenerNombreMes(mes));
        List<ReporteProducto> productos = reporteService.findAllProductosByDate(mes, ano);
        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource (productos);

        reporte = jasperReportService.generatePDFReport("reporteProductos", params, ds );
        HttpHeaders cabecera = new HttpHeaders();
        cabecera.add(HttpHeaders.CONTENT_TYPE, "application/pdf; charset=UTF-8");
        // cabecera.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"productos.pdf\"");
        return new ResponseEntity<byte[]>(reporte, cabecera, HttpStatus.OK);

    }

    // IMPRIMIR VALE DE SALIDA

    @RequestMapping(value = "/generar/vales/{numeroRequisicion}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getValeSalida(@PathVariable("numeroRequisicion") Long numeroRequisicion) {

        byte[] reporte = null;
        ValeSalida vale = valeSalidaService.getValeSalidaByNumeroRequisicion(numeroRequisicion);
        Map<String, Object> params = new HashMap<>();
        params.put("numeroRequisicion", String.valueOf(vale.getNumeroRequisicion()));
        params.put("area", vale.getArea().getNombre().toUpperCase());
        params.put("responsable", vale.getArea().getResponsable());
        params.put("fechaEntrega", String.valueOf(vale.getFechaEntrega()));
        List<ReporteProductosValeSalida> valeSalida = reporteService.getProductosByValeSalidaNumeroRequisicion(vale.getNumeroRequisicion());
        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource (valeSalida);

        reporte = jasperReportService.generatePDFReport("vale_salida", params, ds );
        HttpHeaders cabecera = new HttpHeaders();
        cabecera.add(HttpHeaders.CONTENT_TYPE, "application/pdf; charset=UTF-8");
        // cabecera.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"productos.pdf\"");
        return new ResponseEntity<byte[]>(reporte, cabecera, HttpStatus.OK);


    }


    public String obtenerNombreMes(Integer mes) {
        String meses[] = {"", "ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO", "JUNIO", "JULIO", "AGOSTO", "SEPTIEMBRE", "OCTUBRE", "NOVIEMBRE", "DICIEMBRE"};
        return meses[mes];
    }

}


