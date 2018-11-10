package com.flamel.almacenutt.models.service;

import com.flamel.almacenutt.models.dao.ProveedorDao;
import com.flamel.almacenutt.models.entity.Proveedor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProveedorServiceImpl implements ProveedorService{


    @Autowired
    private ProveedorDao proveedorDao;

    @Override
    public List<Proveedor> listAllProveedores() {
        return proveedorDao.findAll();
    }

    @Override
    public void saveProveedor(Proveedor proveedor) {
        proveedorDao.save(proveedor);
    }

    @Override
    public Proveedor getProveedorByNombre(String nombre) {
        return proveedorDao.getProveedorByNombre(nombre);
    }
}
