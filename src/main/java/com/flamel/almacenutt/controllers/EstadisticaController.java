package com.flamel.almacenutt.controllers;

import com.flamel.almacenutt.models.model.ValesByArea;
import com.flamel.almacenutt.models.service.EstadisticaService;
import com.flamel.almacenutt.util.CustomResponseType;
import com.flamel.almacenutt.util.ValeWithMes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/v1")
public class EstadisticaController {

    @Autowired
    EstadisticaService estadisticaService;

    @RequestMapping(value = "/estadisticas/vales", method = RequestMethod.GET)
    public ResponseEntity<?> getValesByArea() {

        Map<String, ValeWithMes> fechas = new HashMap<>();
        Calendar date = Calendar.getInstance();
        Integer mesActual = date.get(Calendar.MONTH) + 1;
        Integer anoActual = date.get(Calendar.YEAR);
        Integer anoDos = date.get(Calendar.YEAR);
        Integer anoTres = date.get(Calendar.YEAR);
        Integer mesDos = 0;
        Integer mesTres = 0;
        if (mesActual == 1) {
            mesDos = 12;
            mesTres = mesDos - 1;
            anoDos = anoActual - 1;
            anoTres = anoDos;
        } else if (mesActual == 2) {
            mesDos = mesActual - 1;
            mesTres = 12;
            anoTres = anoActual - 1;
        }
        if (mesActual > 2) {
            mesDos = mesActual - 1;
            mesTres = mesDos - 1;
        }


        fechas.put("fecha1", new ValeWithMes( obtenerTotalVales(mesActual, anoActual), obtenerNombreMes(mesActual)));
        fechas.put("fecha2", new ValeWithMes( obtenerTotalVales(mesDos, anoDos), obtenerNombreMes(mesDos)));
        fechas.put("fecha3", new ValeWithMes( obtenerTotalVales(mesTres, anoTres), obtenerNombreMes(mesTres)));
        return new ResponseEntity<>(fechas, HttpStatus.OK);
    }

    public String obtenerNombreMes(Integer mes) {
        String meses[] = {"", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        return meses[mes];
    }

    public List<ValesByArea> obtenerTotalVales(Integer mes, Integer año) {
        return estadisticaService.valesByArea(mes, año);
    }

}
