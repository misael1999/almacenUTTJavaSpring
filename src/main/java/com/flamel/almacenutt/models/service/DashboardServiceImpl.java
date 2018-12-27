package com.flamel.almacenutt.models.service;

import com.flamel.almacenutt.models.dao.FacturaDao;
import com.flamel.almacenutt.models.dao.ProductoDao;
import com.flamel.almacenutt.models.dao.ProveedorDao;
import com.flamel.almacenutt.models.dao.ValeSalidaDao;
import com.flamel.almacenutt.models.entity.Producto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DashboardServiceImpl implements DashboardService{

    @Autowired
    FacturaDao facturaDao;

    @Autowired
    ProductoDao productoDao;

    @Autowired
    ValeSalidaDao valeSalidaDao;

    @Autowired
    ProveedorDao proveedorDao;

    @Override
    public Long getTotalFacturas() {
        return facturaDao.getTotalFacturas();
    }

    @Override
    public Long getTotalProductos() {
        return productoDao.getTotalProductos();
    }

    @Override
    public Long getTotalValeSalida() {
        return valeSalidaDao.getTotalValeSalida();
    }

    @Override
    public Long getTotalProveedores() {
        return proveedorDao.getTotalProveedores();
    }

    @Override
    public Page<Producto> productoRecientes(Pageable pageable) {
        return productoDao.productoRecientes(pageable);
    }
}
