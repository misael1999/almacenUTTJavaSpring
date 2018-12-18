package com.flamel.almacenutt.models.service;

import com.flamel.almacenutt.models.dao.ProductoDao;
import com.flamel.almacenutt.models.dao.ValeSalidaDao;
import com.flamel.almacenutt.models.entity.Producto;
import com.flamel.almacenutt.models.entity.Proveedor;
import com.flamel.almacenutt.models.model.ReporteGastoArea;
import com.flamel.almacenutt.models.model.ReporteProducto;
import com.flamel.almacenutt.models.model.ReporteProductosValeSalida;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReporteServiceImpl implements ReporteService {

    @Autowired
    ProductoDao productoDao;

    @Autowired
    ValeSalidaDao valeSalidaDao;

    @Override
    public List<ReporteProducto> findAllProductosByDate(Integer mes, Integer ano) {
        return productoDao.findAllProductosByDate(mes, ano);
    }

    @Override
    public List<ReporteProductosValeSalida> getProductosByValeSalidaNumeroRequisicion(Long numeroRequisicion) {
        return valeSalidaDao.getProductosByValeSalidaNumeroRequisicion(numeroRequisicion);
    }

    @Override
    public List<Producto> getProductosByProveedor(Long idProveedor) {
        return productoDao.getProductosByProveedor(idProveedor);
    }

    @Override
    public List<ReporteGastoArea> getAreasGastos(String fecha1, String fecha2) {
        return valeSalidaDao.getAreasGastos(fecha1, fecha2);
    }

    @Override
    public List<ReporteProducto> getProductosByArea(Long idArea, String fecha1, String fecha2) {
        return productoDao.getProductosByArea(idArea, fecha1, fecha2);
    }
}
