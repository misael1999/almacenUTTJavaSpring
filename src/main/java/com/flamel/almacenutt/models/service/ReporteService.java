package com.flamel.almacenutt.models.service;

import com.flamel.almacenutt.models.entity.Producto;
import com.flamel.almacenutt.models.model.ReporteProducto;
import com.flamel.almacenutt.models.model.ReporteProductosValeSalida;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReporteService {

    List<ReporteProducto> findAllProductosByDate(Integer mes, Integer ano);
    List<ReporteProductosValeSalida> getProductosByValeSalidaNumeroRequisicion(Long numeroRequisicion);


}
