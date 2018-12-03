package com.flamel.almacenutt.models.service;

import com.flamel.almacenutt.models.dao.ProveedorDao;
import com.flamel.almacenutt.models.entity.Proveedor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProveedorServiceImpl implements ProveedorService{


    @Autowired
    private ProveedorDao proveedorDao;

    @Override
    public List<Proveedor> findAllProveedores() {
        return proveedorDao.findAllProveedores();
    }

    @Override
    public void saveProveedor(Proveedor proveedor) {
        proveedorDao.save(proveedor);
    }

    @Override
    public Proveedor getProveedorByNombre(String nombre) {
        return proveedorDao.getProveedorByNombreAndStutusUno(nombre);
    }

    @Override
    public List<Proveedor> findAllProveedorLikeNombre(String nombre) {
        return proveedorDao.findProveedorLikeNombre(nombre);
    }

    @Override
    public Page<Proveedor> findAllProveedores(Pageable pageable) {
        return proveedorDao.findAllProveedores(pageable);
    }


}
