package com.flamel.almacenutt.models.service;

import com.flamel.almacenutt.models.entity.Producto;
import com.flamel.almacenutt.models.entity.Proveedor;
import com.flamel.almacenutt.models.model.ReporteGastoArea;
import com.flamel.almacenutt.models.model.ReporteProducto;
import com.flamel.almacenutt.models.model.ReporteProductosValeSalida;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReporteService {

    List<ReporteProducto> findAllProductosByDate(Integer mes, Integer ano);
    List<ReporteProductosValeSalida> getProductosByValeSalidaId(Long numeroRequisicion);
    List<Producto> getProductosByProveedor(Long idProveedor);
    List<ReporteGastoArea> getAreasGastos(String fecha1, String fecha2);
    List<ReporteProducto> getProductosByArea(Long idArea, String fecha1, String fecha2);


}
