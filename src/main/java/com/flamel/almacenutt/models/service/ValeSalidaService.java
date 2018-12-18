package com.flamel.almacenutt.models.service;

import com.flamel.almacenutt.models.entity.Factura;
import com.flamel.almacenutt.models.entity.FacturaProducto;
import com.flamel.almacenutt.models.entity.ValeSalida;
import com.flamel.almacenutt.models.model.ValesByArea;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ValeSalidaService {

    // VALES DE SALIDAS

    void saveValeSalida(ValeSalida valeSalida);
    Page<ValeSalida> listValeSalidaActivas(Pageable pageable);
    ValeSalida findValeSalidaByNumeroRequisicion(Long numeroRequisicion);
    List<ValeSalida> findValeSalidaByIdArea(Long idArea);
    ValeSalida getValeSalidaByNumeroRequisicion(Long numeroRequisicion);
    List<ValeSalida> getValesByTerminoLike(@Param("termino") String termino);
    ValeSalida getValeSalidaById(Long idVale);
    void updateFacturaProducto(FacturaProducto facturaProducto);




}
